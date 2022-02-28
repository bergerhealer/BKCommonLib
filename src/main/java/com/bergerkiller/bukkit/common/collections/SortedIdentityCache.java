package com.bergerkiller.bukkit.common.collections;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Maps cached computed values to keys automatically by keeping the underlying
 * structure ordered in the same way as the source that synchronizes it.
 * Can be used to efficiently map values for every key that exists in another
 * data structure. This eliminates the iterator overhead that LogicUtil
 * synchronizeList has.<br>
 * <br>
 * Keys are compared using identity equality. This class is not multi-thread safe.
 * Only one of each key can ever be stored, trying to add/fill more than one of
 * the same key will result in only one value to be stored.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public final class SortedIdentityCache<K, V> implements Iterable<V> {
    private final Synchronizer<K, V> synchronizer;
    private final Map<K, LinkEntry<K, V>> entriesByKey = new IdentityHashMap<>();

    /*
     * Start and end of the chain. These two entries do not store a key/value,
     * but act as start/end markers during iteration.
     */
    private final LinkEntry<K, V> first = new LinkEntry<>();
    private final LinkEntry<K, V> last = new LinkEntry<>();

    /**
     * Alternates true/false every fill operation to track what entries were already
     * filled. This prevents duplicate keys from entering the cache, always keeping
     * the first key (order) encountered.
     */
    private boolean fillState = false;

    /**
     * Creates a new SortedIdentityCache
     *
     * @param <K> Key type
     * @param <V> Value type
     * @param synchronizer Synchronizer used to map keys to values
     * @return new SortedIdentityCache
     */
    public static <K, V> SortedIdentityCache<K, V> create(Synchronizer<K, V> synchronizer) {
        return new SortedIdentityCache<K, V>(synchronizer);
    }

    private SortedIdentityCache(Synchronizer<K, V> synchronizer) {
        this.synchronizer = synchronizer;
        this.first.next = this.last;
        this.last.prev = this.first;
    }

    /**
     * Gets the number of key-values stored inside this cache
     *
     * @return size
     */
    public int size() {
        return this.entriesByKey.size();
    }

    @Override
    public Iterator<V> iterator() {
        return new MappedIterator<V>(this.first.next, LinkEntry::getValue);
    }

    /**
     * Iterates all the keys of entries stored in this cache
     *
     * @return key iterable
     */
    public Iterable<K> keys() {
        return () -> new MappedIterator<K>(this.first.next, LinkEntry::getKey);
    }

    /**
     * Iterates all the entries (key+value) stored in this cache
     *
     * @return entries iterable
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Iterable<Map.Entry<K, V>> entries() {
        return () -> (Iterator<Map.Entry<K, V>>) new MappedIterator(this.first.next, Function.identity());
    }

    @Override
    public void forEach(Consumer<? super V> action) {
        LinkEntry<K, V> last = this.last;
        for (LinkEntry<K, V> curr = this.first.next; curr != last; curr = curr.next) {
            action.accept(curr.value);
        }
    }

    /**
     * Returns an ordered Stream of values stored inside this cache
     *
     * @return Stream of values
     */
    public Stream<V> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                this.iterator(),
                Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    /**
     * Fills this cache with values for all keys encountered in the specified
     * stream, in the same element order. Elements that are not filled are
     * removed from this cache. This synchronizes the elements of this cache
     * with the keys found in the specified stream.
     *
     * @param keyStream Stream of keys to sync with
     */
    public void sync(Stream<K> keyStream) {
        Syncher syncher = new Syncher();
        keyStream.forEachOrdered(syncher);
        syncher.trimRemaining();
    }

    /**
     * Fills this cache with values for all keys encountered in the specified
     * iterable, in the same element order. Elements that are not filled are
     * removed from this cache. This synchronizes the elements of this cache
     * with the keys found in the specified iterable.
     *
     * @param keys Keys to sync with
     */
    public void sync(Iterable<K> keys) {
        Syncher syncher = new Syncher();
        keys.forEach(syncher);
        syncher.trimRemaining();
    }

    /**
     * Fills this cache with values for all keys encountered in the specified
     * iterator, in the same element order. Elements that are not filled are
     * removed from this cache. This synchronizes the elements of this cache
     * with the keys found in the specified iterator.
     *
     * @param keysIterator Iterator of keys to sync with
     */
    public void sync(Iterator<K> keysIterator) {
        Syncher syncher = new Syncher();
        keysIterator.forEachRemaining(syncher);
        syncher.trimRemaining();
    }

    /**
     * Clears all the values inside this cache. For every key/value stored,
     * the {@link Synchronizer#onRemoved(Object, Object)} is called.
     */
    public void clear() {
        LinkEntry<K, V> curr = this.first.next;
        LinkEntry<K, V> last = this.last;

        entriesByKey.clear();
        first.next = last;
        last.prev = first;

        for (; curr != last; curr = curr.next) {
            synchronizer.onRemoved(curr.key, curr.value);
        }
    }

    /**
     * Removes an element from this cache
     *
     * @param key
     * @return Value mapped to key, or null if not contained
     */
    public V remove(K key) {
        LinkEntry<K, V> entry = entriesByKey.remove(key);
        if (entry != null) {
            entry.unbind();
            synchronizer.onRemoved(entry.key, entry.value);
            return entry.value;
        } else {
            return null;
        }
    }

    /**
     * Gets the value mapped to a key in this cache
     *
     * @param key
     * @return Value mapped to this key, or null if not contained
     */
    public V get(K key) {
        LinkEntry<K, V> entry = entriesByKey.get(key);
        return (entry == null) ? null : entry.value;
    }

    /**
     * Adds a new value at the beginning of this cache.
     * If the value is already contained, returns the existing
     * value without re-ordering.
     *
     * @param key
     * @return Existing value at this key, or a new computed value
     */
    public V addFirst(K key) {
        LinkEntry<K, V> newEntry = computeValue(key);
        if (newEntry.next == null) {
            newEntry.bind(this.first, this.first.next);
        }
        return newEntry.value;
    }

    /**
     * Adds a new value at the end of this cache.
     * If the value is already contained, returns the existing
     * value without re-ordering.
     *
     * @param key
     * @return Existing value at this key, or a new computed value
     */
    public V addLast(K key) {
        LinkEntry<K, V> newEntry = computeValue(key);
        if (newEntry.next == null) {
            newEntry.bind(this.last.prev, this.last);
        }
        return newEntry.value;
    }

    private LinkEntry<K, V> computeValue(K key) {
        return entriesByKey.computeIfAbsent(key,
                k -> new LinkEntry<K, V>(k, synchronizer.onAdded(k), fillState));
    }

    private final class Syncher implements Consumer<K> {
        private LinkEntry<K, V> prev = SortedIdentityCache.this.first;
        private LinkEntry<K, V> curr = prev.next;

        public Syncher() {
            // Before we fill, flip the fillState around. All existing entries will
            // then be seen as 'not yet filled', and every item we then fill with keys,
            // will be made correct. This eliminates otherwise needed iteration.
            SortedIdentityCache.this.fillState = !SortedIdentityCache.this.fillState;
        }

        @Override
        public void accept(K key) {
            // Most common case - value already there
            final LinkEntry<K, V> curr = this.curr;
            final boolean fillState = SortedIdentityCache.this.fillState;
            if (key == curr.key) {
                curr.fillState = fillState;
                this.prev = curr;
                this.curr = curr.next;
                return;
            }

            // Find an existing link entry efficiently, or create a new one if not found
            LinkEntry<K, V> newEntry = computeValue(key);
            if (newEntry.next != null) {
                // If we find that we already filled this one, ignore the key entirely.
                if (newEntry.fillState == fillState) {
                    return; // Already added this key
                }

                // Replace the current entry with the found entry
                // The current entry is put at the end of the chain, as it was likely removed
                // This is slightly more efficient, as it avoids 'hopping' at the current removed
                // entry over and over.
                newEntry.unbind();
                newEntry.bind(prev, curr.next);
                newEntry.fillState = fillState;
                curr.bind(last.prev, last);

                // Resume from the existing entry
                this.prev = newEntry;
                this.curr = newEntry.next;
            } else {
                // Insert the new entry at the current position
                newEntry.bind(prev, curr);
                newEntry.fillState = fillState;
                this.prev = newEntry; // Resume with same curr, new prev
            }
        }

        public void trimRemaining() {
            // Break the chain at the current link entry
            LinkEntry<K, V> end = SortedIdentityCache.this.last;
            prev.next = end;
            end.prev = prev;

            // All entries that now remain have been removed
            // Remove them from the HashMap as well
            LinkEntry<K, V> curr = this.curr;
            for (; curr != end; curr = curr.next) {
                entriesByKey.remove(curr.key);
                synchronizer.onRemoved(curr.key, curr.value);
            }
        }
    }

    private static final class LinkEntry<K, V> implements Map.Entry<K, V> {
        public final K key;
        public final V value;
        public LinkEntry<K, V> prev;
        public LinkEntry<K, V> next;
        private boolean fillState;

        @SuppressWarnings("unchecked")
        public LinkEntry() {
            this.key = (K) new Object(); // Avoids null mapping to first/last
            this.value = null;
            this.fillState = false;
        }

        public LinkEntry(K key, V value, boolean fillState) {
            this.key = key;
            this.value = value;
            this.fillState = fillState;
        }

        /**
         * Binds this entry to a (new) position in the chain
         *
         * @param prev New previous entry
         * @param next New next entry
         */
        public void bind(LinkEntry<K, V> prev, LinkEntry<K, V> next) {
            this.prev = prev;
            this.next = next;
            prev.next = this;
            next.prev = this;
        }

        /**
         * Removes this entry from the prev/next entries.
         * Own prev/next fields are not updated
         */
        public void unbind() {
            LinkEntry<K, V> prev = this.prev;
            LinkEntry<K, V> next = this.next;
            prev.next = next;
            next.prev = prev;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("Values are immutable");
        }
    }

    private final class MappedIterator<E> implements Iterator<E> {
        private final Function<LinkEntry<K, V>, E> mapper;
        private final LinkEntry<K, V> end;
        private LinkEntry<K, V> curr;

        public MappedIterator(LinkEntry<K, V> start, Function<LinkEntry<K, V>, E> mapper) {
            this.mapper = mapper;
            this.end = SortedIdentityCache.this.last;
            this.curr = start;
        }

        @Override
        public boolean hasNext() {
            return this.curr != this.end;
        }

        @Override
        public E next() {
            LinkEntry<K, V> curr = this.curr;
            if (curr == this.end) {
                throw new NoSuchElementException("End reached");
            }
            this.curr = curr.next;
            return this.mapper.apply(curr);
        }

        @Override
        public void remove() {
            // Remove the value we returned previously in next()
            // Will do some broken stuff if called multiple times in a row
            LinkEntry<K, V> lastReturned = this.curr.prev;
            if (lastReturned == SortedIdentityCache.this.first) {
                throw new NoSuchElementException("Next not called before remove()");
            }

            lastReturned.unbind();
            entriesByKey.remove(lastReturned.key);
            synchronizer.onRemoved(lastReturned.key, lastReturned.value);
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            while (this.curr != this.end) {
                action.accept(this.mapper.apply(this.curr));
                this.curr = this.curr.next;
            }
        }
    }

    /**
     * Synchronizer that maps keys to values for the SortedIdentityCache. Also
     * receives callbacks when values are removed from the cache.
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    @FunctionalInterface
    public static interface Synchronizer<K, V> {
        /**
         * Called when a new item needs to be added to the SortedIdentityCache.
         * No modification of the cache should occur when handling this
         * method.
         *
         * @param key Key for which to create a new value
         * @return Value to add to the cache, mapped to this key
         */
        V onAdded(K key);

        /**
         * Called after a key (and its value) are removed from the
         * SortedIdentityCache
         *
         * @param key Key that was removed
         * @param value Value that was removed
         */
        default void onRemoved(K key, V value) {
        }
    }
}
