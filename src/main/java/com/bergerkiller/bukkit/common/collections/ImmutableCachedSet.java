package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonListener;

/**
 * An immutable cached set. When adding or removing elements, a new
 * immutable hash set is created efficiently. Because contents are immutable, this
 * allows for efficient sharing and lookup in a cache.<br>
 * <br>
 * Null values are not supported. Returned ImmutableCachedSet instances will remain
 * cached until they are garbage-collected.<br>
 * <br>
 * To start using this immutable cached set, call {@link #createNew()}.
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
public final class ImmutableCachedSet<E> extends ImmutableCachedSetAbstract<E, ImmutableCachedSet<E>> {

    private ImmutableCachedSet(Cache<E, ImmutableCachedSet<E>> cache, Set<E> values, int hashCode) {
        super(cache, values, hashCode);
    }

    @Override
    public Iterator<E> iterator() {
        return super.iterator();
    }

    @Override
    public Stream<E> stream() {
        return super.stream();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean contains(E value) {
        return super.contains(value);
    }

    @Override
    public boolean containsAll(Collection<E> values) {
        return super.containsAll(values);
    }

    @Override
    public ImmutableCachedSet<E> remove(E value) {
        return super.remove(value);
    }

    @Override
    public ImmutableCachedSet<E> addAll(Iterable<E> values) {
        return super.addAll(values);
    }

    @Override
    public ImmutableCachedSet<E> add(E value) {
        return super.add(value);
    }

    @Override
    public ImmutableCachedSet<E> removeAll(Iterable<E> values) {
        return super.removeAll(values);
    }

    @Override
    public ImmutableCachedSet<E> addOrRemove(E value, boolean add) {
        return super.addOrRemove(value, add);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public final ImmutableCachedSet<E> clear() {
        return super.clear();
    }

    /**
     * Starts a new empty root of immutable cached sets. From this root, new sets can be created
     * which will be cached efficiently. To create a new empty immutable cached set that uses
     * the same cache, call {@link #clear()}.
     *
     * @param <E> Element type
     * @return New empty immutable cached set with its own dedicated cache for itself and all
     *         immutable sets derived from it.
     */
    public static <E> ImmutableCachedSet<E> createNew() {
        return createNew(ImmutableCachedSet::new);
    }

    /**
     * Creates a new immutable player set. When players log off, the cache is automatically cleaned
     * up.
     *
     * @return New empty immutable player set backed by it's own cache
     * @see {@link #createNew()}
     */
    public static ImmutableCachedSet<Player> createNewPlayerSet() {
        ImmutableCachedSet<Player> set = createNew();
        CommonListener.registerImmutablePlayerSet(set);
        return set;
    }
}
