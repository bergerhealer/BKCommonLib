package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Simple cache of objects. Each cache consists of an allocator method, which creates
 * new instances, and a reset method which resets the objects to its initial state.
 *
 * @param <T>
 */
public final class ObjectCache<T> {
    private final Supplier<T> _allocator;
    private final Consumer<T> _resetMethod;
    private final Entry<T> _root;
    private static final ObjectCache<?> HASHSET_CACHE = create(HashSet::new, HashSet::clear);
    private static final ObjectCache<?> ARRAYLIST_CACHE = create(ArrayList::new, ArrayList::clear);
    private static final ObjectCache<?> HASHMAP_CACHE = create(HashMap::new, HashMap::clear);

    private ObjectCache(Supplier<?> allocator, Consumer<?> resetMethod) {
        _allocator = CommonUtil.unsafeCast(allocator);
        _resetMethod = CommonUtil.unsafeCast(resetMethod);
        _root = new Entry<T>(this, null);
    }

    /**
     * Retrieves a new empty {@link HashSet} instance from the cache,
     * or by creating a new one. These instances are reused by clearing their contents.
     * 
     * @return HashSet entry, close when done using it (hint: use java8 try-with-resources)
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectCache.Entry<Set<T>> newHashSet() {
        return (Entry<Set<T>>) HASHSET_CACHE.create();
    }

    /**
     * Retrieves a new empty {@link ArrayList} instance from the cache,
     * or by creating a new one. These instances are reused by clearing their contents.
     * 
     * @return ArrayList entry, close when done using it (hint: use java8 try-with-resources)
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectCache.Entry<List<T>> newArrayList() {
        return (Entry<List<T>>) ARRAYLIST_CACHE.create();
    }

    /**
     * Retrieves a new empty {@link HashMap} instance from the cache,
     * or by creating a new one. These instances are reused by clearing their contents.
     * 
     * @return HashMap entry, close when done using it (hint: use java8 try-with-resources)
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ObjectCache.Entry<Map<K, V>> newHashMap() {
        return (Entry<Map<K, V>>) HASHMAP_CACHE.create();
    }

    /**
     * Creates a new Object Cache, creating the object using the allocator method specified,
     * and resetting the state of the object using the reset method specified.
     * 
     * @param allocator The method producing new objects
     * @param resetMethod The method resetting existing objects to constructed state
     * @return Object Cache
     */
    public static <T, I> ObjectCache<T> create(Supplier<I> allocator, Consumer<I> resetMethod) {
        return new ObjectCache<T>(allocator, resetMethod);
    }

    /**
     * Retrieves an entry from this cache, initializing a new value if required.
     * The returned entry should be closed to place it back into the cache, preferablly
     * by making use of java8's try-with-resources idiom.
     * 
     * @return entry
     */
    public synchronized Entry<T> create() {
        Entry<T> result = _root._next;
        if (result == null) {
            return new Entry<T>(this, this._allocator.get());
        } else {
            _root._next = result._next;
            result._next = null;
            return result;
        }
    }

    /**
     * Clears all objects in this cache, freeing the memory they occupy
     */
    public synchronized void clear() {
        _root._next = null;
    }

    /**
     * Clears all the default object caches managed by this Class.
     * Internal use only.
     */
    public static void clearDefaultCaches() {
        HASHSET_CACHE.clear();
        ARRAYLIST_CACHE.clear();
        HASHMAP_CACHE.clear();
    }

    /**
     * A single entry in the cache, storing a single value.
     * When the entry is closed, the value is put back into the cache.
     * It is recommended to use java8's try-with-resources feature
     * when interacting with this Entry.
     * 
     * @param <T>
     */
    public static final class Entry<T> implements AutoCloseable {
        private final ObjectCache<T> _cache;
        private final T _value;
        protected Entry<T> _next;

        private Entry(ObjectCache<T> cache, T value) {
            this._cache = cache;
            this._value = value;
            this._next = null;
        }

        /**
         * Gets the current value obtained from the cache
         * 
         * @return value
         */
        public T get() {
            return _value;
        }

        /**
         * Gets the object cache this entry is part of
         * 
         * @return object cache
         */
        public ObjectCache<T> getCache() {
            return this._cache;
        }

        /**
         * Closes this entry, allowing to be used again by others retrieving
         * values from the cache.
         */
        @Override
        public void close() {
            this._cache._resetMethod.accept(this._value);
            synchronized (this._cache) {
                this._next = this._cache._root._next;
                this._cache._root._next = this;
            }
        }
    }
}
