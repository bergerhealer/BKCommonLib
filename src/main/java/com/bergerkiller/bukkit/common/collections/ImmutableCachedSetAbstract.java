package com.bergerkiller.bukkit.common.collections;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.google.common.collect.MapMaker;

/**
 * A base abstract implementation of an immutable cached set. When adding or removing elements,
 * a new immutable hash set is created efficiently using a constructor function. Because
 * contents are immutable, this allows for efficient sharing and lookup in a cache.<br>
 * <br>
 * Null values are not supported. Returned ImmutableCachedSet instances will remain
 * cached until they are garbage-collected.<br>
 * <br>
 * To start using this immutable cached set, call {@link #createNew(Constructor)}
 * with a suitable constructor for constructing new immutable hash set instances. For this,
 * ImmutableCachedSetAbstract must be implemented by a class first. An example is
 * {@link ImmutableCachedSet}<br>
 * <br>
 * With {@link #add(Object)} new elements can then be added to this set, producing
 * a new immutable cached set. Performing the same add operation again will result
 * in a cached value being returned, provided the original immutable cached set did
 * not get garbage collected.<br>
 * <br>
 * This class is primarily useful when a large number of identical sets exist at runtime,
 * where the same sequence of add or remove operations occur often.
 *
 * @param <E> Element type
 */
public abstract class ImmutableCachedSetAbstract<E, S extends ImmutableCachedSetAbstract<E, S>> implements Iterable<E> {
    private final Cache<E, S> cache;
    private final Set<E> values;
    private final Set<E> unmodifiableValues;
    private final int hashCode;

    // This prevents the last-known add operation that produced this set to be
    // garbage collected. Is swapped out with every new add operation, and
    // requires no locking because of the global GC lock that already exists.
    // No memory leak is risked because the add operation itself stores values
    // that all already exist inside this hashset anyway.
    private Cache.AddOperation<E, S> lastAddOperationGC = null;

    // Stores the last add() performed on this immutable cached set. To allow the
    // resulting set to garbage-collect, this is stored by a weak reference.
    private WeakReference<Cache.AddOperation<E, S>> lastAddOperation = LogicUtil.nullWeakReference();

    // Stores the last remove() performed on this immutable cached set. Since the values
    // stored in this operation include all values stored in this set itself, there is no
    // weak reference required. Instead, a weak reference is put inside the remove operation
    // itself for the result value.
    private Cache.RemoveOperation<E, S> lastRemoveOperation = Cache.RemoveOperation.none();

    protected ImmutableCachedSetAbstract(Cache<E, S> cache, Set<E> values, int hashCode) {
        this.cache = cache;
        this.values = values;
        this.unmodifiableValues = Collections.unmodifiableSet(values);
        this.hashCode = hashCode;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * Gets the number of values stored in this immutable cached set
     *
     * @return value count
     */
    public int size() {
        return this.values.size();
    }

    /**
     * Gets whether this immutable cached set is empty.
     *
     * @return True if empty
     */
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return this.unmodifiableValues.iterator();
    }

    /**
     * Returns a new stream of all the values contained within this immutable cached set
     *
     * @return stream of values
     */
    public Stream<E> stream() {
        return this.values.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ImmutableCachedSetAbstract) {
            ImmutableCachedSetAbstract<?, ?> other = (ImmutableCachedSetAbstract<?, ?>) o;
            return this.values.equals(other.values);
        } else if (o instanceof Collection) {
            Collection<?> other = (Collection<?>) o;
            return this.values.equals(other);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        Iterator<E> iter = this.values.iterator();
        if (iter.hasNext()) {
            StringBuilder str = new StringBuilder();
            str.append('{').append(iter.next());
            while (iter.hasNext()) {
                str.append(", ").append(iter.next());
            }
            str.append('}');
            return str.toString();
        } else {
            return "{}";
        }
    }

    /**
     * Checks whether a particular value is contained
     *
     * @param o Value to check
     * @return True if the value is contained in this set
     */
    public boolean contains(E o) {
        return this.values.contains(o);
    }

    /**
     * Checks whether all va;ies specified are contained within this set
     *
     * @param values to check
     * @return True if all values are contained
     */
    public boolean containsAll(Collection<E> values) {
        return this.values.containsAll(values);
    }

    /**
     * Returns a new immutable cached set with the contents of this set, with
     * the value specified removed.
     * If no changes occur, the same immutable set instance is returned.
     *
     * @param value The value to remove
     * @return changed immutable set, or this set if not contained or value is null
     */
    @SuppressWarnings("unchecked")
    public S remove(E value) {
        {
            Cache.RemoveOperation<E, S> op = this.lastRemoveOperation;
            if (op.removedElement.equals(value)) {
                // Almost certainly the value is contained, otherwise this check would never pass.
                S result = op.result.get();
                if (result != null) {
                    return result;
                }
            }
        }

        // Check contained at all, if not, return self
        // Generally we do not want to cache this operation, as this could happen a lot
        // Besides, contains() is about as fast as the equals() because of the hash code.
        if (!this.values.contains(value)) {
            return (S) this;
        }

        S result;
        if (this.values.size() == 1) {
            result = this.cache.EMPTY;
        } else {
            result = this.cache.remove(this, value);
        }

        // Remember operation for next time
        this.lastRemoveOperation = new Cache.RemoveOperation<E, S>(value, result);

        return result;
    }

    /**
     * Returns a new immutable cached set with the contents of this set, with
     * the value specified added.
     * If no changes occur, the same immutable set instance is returned.
     *
     * @param value The value to add
     * @return changed immutable set, or this set if value is already contained
     * @throws IllegalArgumentException If the input value is null
     */
    @SuppressWarnings("unchecked")
    public S add(E value) {
        if (this.values.contains(value)) {
            return (S) this;
        } else {
            Cache.AddOperation<E, S> op = this.lastAddOperation.get();
            if (op != null && op.addedElement.equals(value)) {
                return op.result;
            } else {
                S result = this.cache.add(this, value);

                // Store as add-operation so future add() with the same value is optimized
                {
                    op = new Cache.AddOperation<E, S>(value, result, this);
                    this.lastAddOperation = new WeakReference<Cache.AddOperation<E, S>>(op);
                    ((ImmutableCachedSetAbstract<E, S>) result).lastAddOperationGC = op;
                }

                return result;
            }
        }
    }

    /**
     * Returns a new immutable cached set with the contents of this set, with
     * the values specified added.
     * If no changes occur, the same immutable set instance is returned.
     *
     * @param values The values to add
     * @return changed immutable set, or this set if all values were already contained
     * @throws IllegalArgumentException If one or more of the input values are null
     */
    public S addAll(Iterable<E> values) {
        @SuppressWarnings("unchecked")
        S result = (S) this;
        for (E value : values) {
            result = result.add(value);
        }
        return result;
    }

    /**
     * Returns a new immutable cached set with the contents of this set, with
     * the values specified removed.
     * If no changes occur, the same immutable set instance is returned.
     *
     * @param values The values to remove
     * @return changed immutable set, or this set if none of the values were contained
     */
    public S removeAll(Iterable<E> values) {
        @SuppressWarnings("unchecked")
        S result = (S) this;
        for (E value : values) {
            result = result.remove(value);
        }
        return result;
    }

    /**
     * Conditionally adds or removes a value based on a boolean state.
     * If no changes occur, the same immutable set instance is returned.
     *
     * @param value The value to add or remove
     * @param add option, True to add, False to remove
     * @return changed immutable set of players
     * @throws IllegalArgumentException If value is null and add is true
     */
    public S addOrRemove(E value, boolean add) {
        return add ? add(value) : remove(value);
    }

    /**
     * Returns the initial, empty immutable cached set that contains no values.
     * This method returns a constant and is cheap, no expensive operations
     * are performed.
     * 
     * @return cleared immutable cached set
     */
    public S clear() {
        return this.cache.EMPTY;
    }

    /**
     * Deletes all cached immutable cached sets from the cache pool that store the specified
     * value. Doing so may help speed up garbage collecting these sets. Note that it does not
     * matter on what immutable instance this method is called, as it operates on the cache
     * shared by all.
     *
     * @param value Immutable cached sets that contain this value are removed
     */
    public void releaseFromCache(E value) {
        if (value != null) {
            this.cache.release(value);
        }
    }

    /**
     * Creates a new immutable cached set cache
     *
     * @param <E> Element type
     * @param <S> Immutable Cached Set class implementation type
     * @param constructor Constructor to call to instantiate new immutable cached sets
     * @return Immutable cached set empty constant, with it's own cache
     */
    protected static <E, S extends ImmutableCachedSetAbstract<E, S>> S createNew(Cache.Constructor<E, S> constructor) {
        return (S) (new Cache<E, S>(constructor)).EMPTY;
    }

    /**
     * Stores a weak-valued cache of all created immutable cached set instances
     *
     * @param <E> Element type
     */
    protected static final class Cache<E, S extends ImmutableCachedSetAbstract<E, S>> {
        private final Constructor<E, S> _constructor;
        private final ConcurrentMap<Key<E>, S> _cache;
        public final S EMPTY;

        public Cache(Constructor<E, S> constructor) {
            this._constructor = constructor;
            this._cache = new MapMaker()
                    .concurrencyLevel(1)
                    .weakValues()
                    .makeMap();
            this.EMPTY = constructor.create(this, Collections.emptySet(), 0);
        }

        public S add(ImmutableCachedSetAbstract<E, ?> current, E elementToAdd) {
            Key<E> key;
            try {
                key = current.isEmpty() ? new KeyAddOne<E>(elementToAdd) : new KeyAdd<E>(current, elementToAdd);
            } catch (NullPointerException ex) {
                if (elementToAdd == null) {
                    throw new IllegalArgumentException("Input element to add is null");
                }
                throw ex;
            }

            S set = _cache.get(key);
            if (set == null) {
                key = new KeyValues<E>(key);
                set = _constructor.create(this, key.values(), key.hashCode());
                _cache.put(key, set);
            }

            return set;
        }

        public S remove(ImmutableCachedSetAbstract<E, ?> current, Object elementToRemove) {
            Key<E> key = new KeyRemove<E>(current, elementToRemove);
            S set = _cache.get(key);
            if (set == null) {
                key = new KeyValues<E>(key);
                set = _constructor.create(this, key.values(), key.hashCode());
                _cache.put(key, set);
            }

            return set;
        }

        public void release(E value) {
            Iterator<S> iter = this._cache.values().iterator();
            while (iter.hasNext()) {
                ImmutableCachedSetAbstract<E, S> set = iter.next();
                if (set.contains(value)) {
                    // Reset any add operation that resulted in this set being made
                    {
                        AddOperation<E, S> op = set.lastAddOperationGC;
                        if (op != null) {
                            set.lastAddOperationGC = null;
                            ImmutableCachedSetAbstract<E, S> creator = op.creator.get();
                            if (creator != null && creator.lastAddOperation.get() == op) {
                                creator.lastAddOperation = LogicUtil.nullWeakReference();
                            }
                        }
                    }

                    iter.remove();
                }
            }
        }

        /**
         * For custom implementations: defines the constructor to call to create a new
         * immutable cached set.
         *
         * @param <E>
         */
        @FunctionalInterface
        public static interface Constructor<E, S extends ImmutableCachedSetAbstract<E, S>> {
            S create(Cache<E, S> cache, Set<E> values, int hashCode);
        }

        /**
         * Stores the result of an add operation that produced a resulting ImmutableCachedSet.
         * This makes repeated add() with the same value more efficient, at a very low
         * memory overhead.
         *
         * @param <E> Element type
         */
        private static class AddOperation<E, S extends ImmutableCachedSetAbstract<E, S>> {
            public final E addedElement;
            public final S result;
            public final WeakReference<ImmutableCachedSetAbstract<E, S>> creator;

            public AddOperation(E addedElement, S result, ImmutableCachedSetAbstract<E, S> creator) {
                this.addedElement = addedElement;
                this.result = result;
                this.creator = new WeakReference<ImmutableCachedSetAbstract<E, S>>(creator);
            }
        }

        /**
         * Stores the result of a remove operation that produced a resulting ImmutableCachedSet.
         * This makes repeated remove() with the same value more efficient, at a very low
         * memory overhead.
         *
         * @param <E> Element type
         */
        private static class RemoveOperation<E, S extends ImmutableCachedSetAbstract<E, S>> {
            @SuppressWarnings("rawtypes")
            private static final RemoveOperation NONE = new RemoveOperation();

            public final Object removedElement;
            public final WeakReference<S> result;

            private RemoveOperation() {
                this.removedElement = new NoneValue(); // Equals will fail, always!
                this.result = LogicUtil.nullWeakReference();
            }

            public RemoveOperation(Object removedElement, S result) {
                this.removedElement = removedElement;
                this.result = new WeakReference<S>(result);
            }

            @SuppressWarnings("unchecked")
            public static <E, S extends ImmutableCachedSetAbstract<E, S>> RemoveOperation<E, S> none() {
                return NONE;
            }

            private static final class NoneValue {
                @Override
                public boolean equals(Object o) {
                    return false;
                }
            }
        }

        private static abstract class Key<E> {
            public final Set<E> values;
            public final int hashCode;

            public Key(Set<E> values, int hashCode) {
                this.values = values;
                this.hashCode = hashCode;
            }

            @Override
            public final int hashCode() {
                return this.hashCode;
            }

            @Override
            public abstract boolean equals(Object o);

            public abstract boolean equalsValues(Set<E> values);

            public abstract Set<E> values();
        }

        private static final class KeyValues<E> extends Key<E> {

            public KeyValues(Key<E> input) {
                super(input.values(), input.hashCode);
            }

            @Override
            public Set<E> values() {
                return this.values;
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean equals(Object o) {
                return ((Key<E>) o).equalsValues(this.values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return this.values.equals(values);
            }
        }

        private static final class KeyAddOne<E> extends Key<E> {
            private final E added;

            public KeyAddOne(E added) {
                super(Collections.emptySet(), added.hashCode());
                this.added = added;
            }

            @Override
            public Set<E> values() {
                return Collections.singleton(this.added);
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean equals(Object o) {
                return equalsValues(((KeyValues<E>) o).values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return values.size() == 1 && values.contains(added);
            }
        }

        private static final class KeyAdd<E> extends Key<E> {
            private final E added;

            public KeyAdd(ImmutableCachedSetAbstract<E, ?> current, E added) {
                super(current.values, current.hashCode ^ added.hashCode());
                this.added = added;
            }

            @Override
            public Set<E> values() {
                Set<E> combined = new HashSet<E>(values.size() + 1);
                combined.addAll(values);
                combined.add(added);
                return combined;
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean equals(Object o) {
                return equalsValues(((KeyValues<E>) o).values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return values.size() == (this.values.size() + 1) &&
                       values.containsAll(this.values) &&
                       values.contains(this.added);
            }
        }

        private static final class KeyRemove<E> extends Key<E> {
            private final Object removed;

            public KeyRemove(ImmutableCachedSetAbstract<E, ?> current, Object removed) {
                super(current.values, current.hashCode ^ removed.hashCode());
                this.removed = removed;
            }

            @Override
            public Set<E> values() {
                Set<E> combined = new HashSet<E>(values);
                combined.remove(removed);
                return combined;
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean equals(Object o) {
                return equalsValues(((KeyValues<E>) o).values);
            }

            @Override
            public boolean equalsValues(Set<E> values) {
                return values.size() == (this.values.size() - 1) &&
                       this.values.containsAll(values) &&
                       !values.contains(this.removed);
            }
        }
    }
}
