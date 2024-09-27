package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
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
public abstract class SortedIdentityCache<K, V> implements Iterable<V> {
    protected final Synchronizer<K, V> synchronizer;

    /**
     * Creates a new SortedIdentityCache. This uses the default linear List implementation,
     * which grows the backing array to meet capacity requirements. Since elements are
     * stored linearly, iteration is hyper-fast, while non-linear synchronization might
     * be slower than the linked list variant.
     *
     * @param <K> Key type
     * @param <V> Value type
     * @param synchronizer Synchronizer used to map keys to values
     * @return new SortedIdentityCache
     */
    public static <K, V> SortedIdentityCache<K, V> create(Synchronizer<K, V> synchronizer) {
        return new SortedIdentityCacheList<K, V>(synchronizer);
    }

    /**
     * Creates a new SortedIdentityCache using a Linked List kind of implementation.
     * This will be more efficient for small collections, or when data is inserted
     * all at once. With large entries, over time this collection will become much slower
     * to iterate over, due to memory fragmentation.
     *
     * @param <K> Key type
     * @param <V> Value type
     * @param synchronizer Synchronizer used to map keys to values
     * @return new SortedIdentityCache
     */
    public static <K, V> SortedIdentityCache<K, V> createLinked(Synchronizer<K, V> synchronizer) {
        return new SortedIdentityCacheLinkedList<K, V>(synchronizer);
    }

    protected SortedIdentityCache(Synchronizer<K, V> synchronizer) {
        this.synchronizer = synchronizer;
    }

    /**
     * Gets the number of key-values stored inside this cache
     *
     * @return size
     */
    public abstract int size();

    /**
     * Clears all the values inside this cache. For every key/value stored,
     * the {@link Synchronizer#onRemoved(Object, Object)} is called.
     */
    public abstract void clear();

    @Override
    public abstract Iterator<V> iterator();

    /**
     * Iterates all the keys of entries stored in this cache
     *
     * @return key iterable
     */
    public abstract Iterable<K> keys();

    /**
     * Iterates all the entries (key+value) stored in this cache
     *
     * @return entries iterable
     */
    public abstract Iterable<Map.Entry<K, V>> entries();

    @Override
    public abstract void forEach(Consumer<? super V> action);

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
     * @return True if values were added and/or removed.
     *         Change of element order is ignored.
     */
    public final boolean sync(Stream<? extends K> keyStream) {
        return sync(keyStream::forEachOrdered);
    }

    /**
     * Fills this cache with values for all keys encountered in the specified
     * iterable, in the same element order. Elements that are not filled are
     * removed from this cache. This synchronizes the elements of this cache
     * with the keys found in the specified iterable.
     *
     * @param keys Keys to sync with
     * @return True if values were added and/or removed.
     *         Change of element order is ignored.
     */
    public final boolean sync(Iterable<? extends K> keys) {
        return sync(keys::forEach);
    }

    /**
     * Fills this cache with values for all keys encountered in the specified
     * iterator, in the same element order. Elements that are not filled are
     * removed from this cache. This synchronizes the elements of this cache
     * with the keys found in the specified iterator.
     *
     * @param keysIterator Iterator of keys to sync with
     * @return True if values were added and/or removed.
     *         Change of element order is ignored.
     */
    public final boolean sync(Iterator<? extends K> keysIterator) {
        return sync(keysIterator::forEachRemaining);
    }

    /**
     * Fills this cache with values for all keys pushed by the specified callback,
     * in the same element order. Elements that are not filled are
     * removed from this cache. This synchronizes the elements of this cache
     * with the keys supplied by the fill method.
     *
     * @param fillMethod Method accepting a consumer, which should call the consumer
     *                   with all keys to be added.
     * @return True if values were added and/or removed.
     *         Change of element order is ignored.
     */
    public abstract boolean sync(Consumer<Consumer<K>> fillMethod);

    /**
     * Removes an element from this cache
     *
     * @param key
     * @return Value mapped to key, or null if not contained
     */
    public abstract V remove(K key);

    /**
     * Gets the value mapped to a key in this cache
     *
     * @param key
     * @return Value mapped to this key, or null if not contained
     */
    public abstract V get(K key);

    /**
     * Adds a new value at the beginning of this cache.
     * If the value is already contained, returns the existing
     * value without re-ordering.
     *
     * @param key
     * @return Existing value at this key, or a new computed value
     */
    public abstract V addFirst(K key);

    /**
     * Adds a new value at the end of this cache.
     * If the value is already contained, returns the existing
     * value without re-ordering.
     *
     * @param key
     * @return Existing value at this key, or a new computed value
     */
    public abstract V addLast(K key);

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

    /**
     * Implementation of the SortedIdentityCache which stores all keys and values in
     * a simple key-value entry array that grows as needed.
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    private static final class SortedIdentityCacheList<K, V> extends SortedIdentityCache<K, V> {
        private static final int INDEX_NOT_INSERTED = -1;
        private static final int INDEX_PENDING_FLATTENING = -2;
        private final Map<K, LinkEntry<K, V>> entriesByKey = new IdentityHashMap<>();
        @SuppressWarnings("unchecked")
        private LinkEntry<K, V>[] entries = new LinkEntry[10];

        public SortedIdentityCacheList(Synchronizer<K, V> synchronizer) {
            super(synchronizer);
        }

        @Override
        public int size() {
            return entriesByKey.size();
        }

        @Override
        public void clear() {
            if (!entriesByKey.isEmpty()) {
                entriesByKey.clear();
                Arrays.fill(entries, null);
            }
        }

        @Override
        public Iterator<V> iterator() {
            return new MappedIterator<V>(0, LinkEntry::getValue);
        }

        @Override
        public Iterable<K> keys() {
            return () -> new MappedIterator<K>(0, LinkEntry::getKey);
        }

        @Override
        @SuppressWarnings({"unchecked"})
        public Iterable<Map.Entry<K, V>> entries() {
            return () -> (Iterator<Map.Entry<K, V>>) (Object) new MappedIterator<LinkEntry<K, V>>(0, Function.identity());
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            LinkEntry<K, V>[] entries = this.entries;
            int numEntries = this.entriesByKey.size();
            for (int i = 0; i < numEntries; i++) {
                action.accept(entries[i].getValue());
            }
        }

        @Override
        public boolean sync(Consumer<Consumer<K>> fillMethod) {
            Syncher syncher = new Syncher();
            fillMethod.accept(syncher);
            syncher.trimRemaining();
            return syncher.hasChanges;
        }

        @Override
        public V remove(K key) {
            LinkEntry<K, V> removedEntry = entriesByKey.remove(key);
            if (removedEntry == null) {
                return null;
            }

            LinkEntry<K, V>[] entries = this.entries;
            int numEntries = entriesByKey.size();
            for (int i = removedEntry.index; i < numEntries; i++) {
                LinkEntry<K, V> entry = entries[i + 1];
                entry.index--;
                entries[i] = entry;
            }
            return removedEntry.getValue();
        }

        @Override
        public V get(K key) {
            LinkEntry<K, V> entry = entriesByKey.get(key);
            return (entry == null) ? null : entry.getValue();
        }

        @Override
        public V addFirst(K key) {
            LinkEntry<K, V> entry = computeValue(key);
            if (entry.index == INDEX_NOT_INSERTED) {
                insertAt(0, entry);
            }
            return entry.value;
        }

        @Override
        public V addLast(K key) {
            LinkEntry<K, V> entry = computeValue(key);
            if (entry.index == INDEX_NOT_INSERTED) {
                int numEntries = entriesByKey.size() - 1;
                insertAtEnd(entry, numEntries);
            }
            return entry.value;
        }

        @SuppressWarnings("unchecked")
        private void insertAt(int index, LinkEntry<K, V> newEntry) {
            LinkEntry<K, V>[] entries = this.entries;
            int numEntries = entriesByKey.size() - 1;
            if (numEntries == entries.length) {
                // Got to grow the array first
                // Can use copyOf when adding to the end
                int newSize = Math.max(20, (numEntries * 4) / 3);
                if (index == numEntries) {
                    this.entries = entries = Arrays.copyOf(entries, newSize);
                } else {
                    LinkEntry<K, V>[] newEntries = (LinkEntry<K, V>[]) new LinkEntry[newSize];
                    System.arraycopy(entries, 0, newEntries, 0, index);
                    for (int i = index; i < numEntries; i++) {
                        LinkEntry<K, V> entry = entries[i];
                        entry.index++;
                        newEntries[i + 1] = entry;
                    }
                    this.entries = entries = newEntries;
                }
            } else if (index < numEntries) {
                // Can update existing array. Must move elements after element to be inserted
                for (int i = numEntries - 1; i >= index; --i) {
                    LinkEntry<K, V> entry = entries[i];
                    entry.index++;
                    entries[i + 1] = entry;
                }
            }

            // Generate and assign a new link entry at this index, that is now freed
            newEntry.index = index;
            entries[index] = newEntry;
        }

        private void insertAtEnd(LinkEntry<K, V> newEntry, int numEntries) {
            LinkEntry<K, V>[] entries = this.entries;
            if (numEntries == entries.length) {
                // Got to grow the array first
                // Can use copyOf when adding to the end
                int newSize = Math.max(20, (numEntries * 4) / 3);
                this.entries = entries = Arrays.copyOf(entries, newSize);
            }

            // Assign the link entry at the end
            newEntry.index = numEntries;
            entries[numEntries] = newEntry;
        }

        private LinkEntry<K, V> computeValue(K key) {
            return entriesByKey.computeIfAbsent(key,
                    k -> new LinkEntry<K, V>(k, synchronizer.onAdded(k)));
        }

        private static final class LinkEntry<K, V> implements Map.Entry<K, V> {
            private final K key;
            private V value;
            private int index;
            private List<LinkEntry<K, V>> insertedAfter;

            public LinkEntry(K key, V value) {
                this.key = key;
                this.value = value;
                this.index = INDEX_NOT_INSERTED;
                this.insertedAfter = Collections.emptyList();
            }

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                V oldValue = this.value;
                this.value = value;
                return oldValue;
            }
        }

        private final class Syncher implements Consumer<K> {
            private int currentIndex;
            private int numEntries;
            private LinkEntry<K, V> firstCompactedEntry;
            private int numCompacted;
            public boolean hasChanges;

            public Syncher() {
                this.currentIndex = 0;
                this.numEntries = entriesByKey.size();
                this.firstCompactedEntry = null;
                this.numCompacted = 0;
                this.hasChanges = false;
            }

            @Override
            public void accept(K key) {
                int index = currentIndex;
                int count = numEntries;

                // Add a new entry at the end, unless it already exists
                if (index == count) {
                    LinkEntry<K, V> newEntry = computeValue(key);
                    if (newEntry.index == INDEX_NOT_INSERTED) {
                        insertAtEnd(newEntry, count);
                        numEntries = count + 1;
                        currentIndex = index + 1;
                        hasChanges = true;
                    }
                    return;
                }

                // Match entry against current entry at this index
                LinkEntry<K, V> entries[] = SortedIdentityCacheList.this.entries;
                LinkEntry<K, V> entry = entries[index];
                boolean isValidEntry = (entry.index == index);
                if (isValidEntry && entry.getKey() == key) {
                    currentIndex = index + 1;
                    return;
                }

                // Figure out where an entry for this key exists, or create a new one
                LinkEntry<K, V> newEntry = computeValue(key);

                // Wipe and overwrite invalid entries if we can
                if (!isValidEntry) {
                    if (newEntry.index == INDEX_NOT_INSERTED) {
                        hasChanges = true;
                    }
                    if (newEntry.index == INDEX_NOT_INSERTED || newEntry.index > index) {
                        // Overwrite invalid entry at this position to point to the found entry
                        newEntry.index = index;
                        entries[index] = newEntry;
                        currentIndex = index + 1;
                    }
                    return;
                }

                if (newEntry.index == INDEX_NOT_INSERTED) {
                    // New entry added in the middle of the chain.
                    // We track a separate list on the previous entry, storing entries
                    // to be inserted afterwards. During trimRemaining() this will
                    // be flattened.
                    // Resizing the array with every insertion is too slow.
                    newEntry.index = INDEX_PENDING_FLATTENING;
                    hasChanges = true;
                    if (index == 0) {
                        // Got no previous one, a new one is inserted later
                        LinkEntry<K, V> first = firstCompactedEntry;
                        if (first == null) {
                            firstCompactedEntry = newEntry;
                            numCompacted = 1;
                        } else {
                            if (first.insertedAfter.isEmpty()) {
                                first.insertedAfter = new ArrayList<>(16);
                            }
                            first.insertedAfter.add(newEntry);
                            numCompacted++;
                        }
                    } else {
                        // Add to list of previous link entry
                        LinkEntry<K, V> previous = entries[index - 1];
                        if (previous.insertedAfter.isEmpty()) {
                            previous.insertedAfter = new ArrayList<>(16);
                            if (firstCompactedEntry == null) {
                                firstCompactedEntry = previous;
                            }
                        }
                        previous.insertedAfter.add(newEntry);
                        numCompacted++;
                    }
                    return;
                }
                if (newEntry.index < index) {
                    return; // Already added earlier. Also covers INDEX_PENDING_FLATTENING
                }

                // Likely the entry at the current index (and more) got removed
                // Move the entry to the back to speed up removal later
                // Move the found entry to the new position
                // At the gap formed, the entry automatically becomes 'invalid' because its index is wrong
                if (count == entries.length) {
                    // Got to grow the array first
                    // Can use copyOf when adding to the end
                    int newSize = Math.max(20, (count * 4) / 3);
                    SortedIdentityCacheList.this.entries = entries = Arrays.copyOf(entries, newSize);
                }

                //entries[newEntry.index] = null; // Invalid entry is 'inserted' by making the index invalid for this position
                newEntry.index = index;
                entries[index] = newEntry;
                entry.index = count;
                entries[count] = entry;

                currentIndex = index + 1;
                numEntries = count + 1; // Invalid entry was added
            }

            @SuppressWarnings("unchecked")
            private void flatten() {
                if (numCompacted == 0) {
                    return;
                }

                LinkEntry<K, V>[] entries = SortedIdentityCacheList.this.entries;
                int numEntries = this.numEntries;

                // Because we're going to be moving entries, it's not safe to keep the old invalid entries
                // Explicitly overwrite them with null so they can't possibly end up in the wrong place
                for (int i = currentIndex; i < numEntries; i++) {
                    if (entries[i].index != i) {
                        entries[i] = null;
                    }
                }

                // Actual flattening
                int numTotalEntries = numEntries + numCompacted;
                if (numTotalEntries > entries.length) {
                    // Flatten all entries into a brand-spankin' new array
                    // Can be done in a positive order without problems
                    int newSize = (numTotalEntries * 4) / 3;
                    LinkEntry<K, V>[] newEntries = new LinkEntry[newSize];
                    int destIndex = -1;

                    // Inserted one or more entries at the front
                    LinkEntry<K, V> frontEntry = firstCompactedEntry;
                    if (frontEntry.index == INDEX_PENDING_FLATTENING) {
                        destIndex = setEntries(newEntries, destIndex, frontEntry);
                    }

                    // Up to current index are guaranteed to be valid entries
                    int numValidEntries = this.currentIndex;
                    for (int i = 0; i < numValidEntries; i++) {
                        destIndex = setEntries(newEntries, destIndex, entries[i]);
                    }

                    // Beyond, we got to do some null checks
                    for (int i = numValidEntries; i < numEntries; i++) {
                        LinkEntry<K, V> entry = entries[i];
                        if (entry != null) {
                            destIndex = setEntries(newEntries, destIndex, entry);
                        } else {
                            ++destIndex;
                        }
                    }

                    SortedIdentityCacheList.this.entries = newEntries;
                } else {
                    // Regenerate the entries array in reverse, with compacted entries inserted
                    // By doing it in reverse we avoid accidentally overwriting entries
                    // We only need to process up to the first compacted entry index
                    int startIndex = Math.max(0, firstCompactedEntry.index);
                    int safeIndex = currentIndex;
                    int srcIndex = numEntries;
                    int destIndex = numTotalEntries;
                    while (srcIndex > safeIndex) {
                        LinkEntry<K, V> entry = entries[--srcIndex];
                        if (entry != null) {
                            destIndex = setEntriesReverse(entries, destIndex, entry);
                        } else {
                            entries[--destIndex] = null;
                        }
                    }
                    while (srcIndex > startIndex) {
                        destIndex = setEntriesReverse(entries, destIndex, entries[--srcIndex]);
                    }

                    // If needed, insert an additional entry and it's pending insertAfter
                    if (firstCompactedEntry.index == INDEX_PENDING_FLATTENING) {
                        setEntriesReverse(entries, destIndex, firstCompactedEntry);
                    }
                }

                this.firstCompactedEntry = null;
                this.numCompacted = 0;
                this.currentIndex = numTotalEntries - (numEntries - this.currentIndex);
                this.numEntries = numTotalEntries;
            }

            public void trimRemaining() {
                flatten();

                // Wipe entries beyond the current index
                LinkEntry<K, V>[] entries = SortedIdentityCacheList.this.entries;
                int numEntries = this.numEntries;
                int removeStart = currentIndex;
                while (--numEntries >= removeStart) {
                    LinkEntry<K, V> entry = entries[numEntries];
                    if (entry != null && entry.index == numEntries) {
                        entries[numEntries] = null;
                        entriesByKey.remove(entry.getKey());
                        synchronizer.onRemoved(entry.getKey(), entry.getValue());
                        hasChanges = true;
                    }
                }
            }
        }

        private static <K, V> int setEntries(LinkEntry<K, V>[] entries, int destIndex, LinkEntry<K, V> entry) {
            entries[++destIndex] = entry;
            entry.index = destIndex;
            for (LinkEntry<K, V> compacted : entry.insertedAfter) {
                entries[++destIndex] = compacted;
                compacted.index = destIndex;
            }
            entry.insertedAfter = Collections.emptyList();
            return destIndex;
        }

        private static <K, V> int setEntriesReverse(LinkEntry<K, V>[] entries, int destIndex, LinkEntry<K, V> entry) {
            // Insert additional inserted entries, first, in reverse
            int entryNumCompacted = entry.insertedAfter.size();
            if (entryNumCompacted > 0) {
                for (int i = entryNumCompacted - 1; i >= 0; --i) {
                    LinkEntry<K, V> compacted = entry.insertedAfter.get(i);
                    entries[--destIndex] = compacted;
                    compacted.index = destIndex;
                }
                entry.insertedAfter = Collections.emptyList();
            }

            // Entry itself
            entries[--destIndex] = entry;
            entry.index = destIndex;

            return destIndex;
        }

        private final class MappedIterator<E> implements Iterator<E> {
            private final Function<LinkEntry<K, V>, E> mapper;
            private int currentIndex;

            public MappedIterator(int currentIndex, Function<LinkEntry<K, V>, E> mapper) {
                this.mapper = mapper;
                this.currentIndex = currentIndex;
            }

            @Override
            public boolean hasNext() {
                return currentIndex < size();
            }

            @Override
            public E next() {
                int index = currentIndex++;
                LinkEntry<K, V> curr;
                try {
                    curr = entries[index];
                } catch (IndexOutOfBoundsException ex) {
                    if (index < size()) {
                        throw ex;
                    }
                    curr = null;
                }
                if (curr == null) {
                    currentIndex = index;
                    throw new NoSuchElementException("End reached");
                }
                return this.mapper.apply(curr);
            }

            @Override
            public void remove() {
                int indexRemoved = --currentIndex;
                if (indexRemoved < 0) {
                    ++currentIndex;
                    throw new NoSuchElementException("Next not called before remove()");
                }

                LinkEntry<K, V>[] entries = SortedIdentityCacheList.this.entries;
                LinkEntry<K, V> removed = entries[indexRemoved];
                int numEntries = entriesByKey.size();
                for (int i = indexRemoved; i < numEntries; i++) {
                    LinkEntry<K, V> entry = entries[i + 1];
                    entry.index = i;
                    entries[i] = entry;
                }
                entries[numEntries - 1] = null;
                entriesByKey.remove(removed.getKey());
                synchronizer.onRemoved(removed.getKey(), removed.getValue());
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                LinkEntry<K, V>[] entries = SortedIdentityCacheList.this.entries;
                int index = currentIndex;
                int numEntries = entriesByKey.size();
                while (index < numEntries) {
                    action.accept(this.mapper.apply(entries[index]));
                    index++;
                }
                currentIndex = index;
            }
        }
    }

    /**
     * Implementation of the SortedIdentityCache which stores all keys and values in
     * a linked list kind of arrangement. This makes updating the collection very fast,
     * but iteration will be much slower. Especially when there are a lot of entries that
     * are distantly spaced apart in memory.
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    private static final class SortedIdentityCacheLinkedList<K, V> extends SortedIdentityCache<K, V> {
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

        public SortedIdentityCacheLinkedList(Synchronizer<K, V> synchronizer) {
            super(synchronizer);
            this.first.next = this.last;
            this.last.prev = this.first;
        }

        @Override
        public int size() {
            return this.entriesByKey.size();
        }

        @Override
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

        @Override
        public Iterator<V> iterator() {
            return new MappedIterator<V>(this.first.next, LinkEntry::getValue);
        }

        @Override
        public Iterable<K> keys() {
            return () -> new MappedIterator<K>(this.first.next, LinkEntry::getKey);
        }

        @Override
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

        @Override
        public boolean sync(Consumer<Consumer<K>> fillMethod) {
            Syncher syncher = new Syncher();
            fillMethod.accept(syncher);
            syncher.trimRemaining();
            return syncher.hasChanges;
        }

        @Override
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

        @Override
        public V get(K key) {
            LinkEntry<K, V> entry = entriesByKey.get(key);
            return (entry == null) ? null : entry.value;
        }

        @Override
        public V addFirst(K key) {
            LinkEntry<K, V> newEntry = computeValue(key);
            if (newEntry.next == null) {
                newEntry.bind(this.first, this.first.next);
            }
            return newEntry.value;
        }

        @Override
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
            private LinkEntry<K, V> prev = SortedIdentityCacheLinkedList.this.first;
            private LinkEntry<K, V> curr = prev.next;
            public boolean hasChanges = false;

            public Syncher() {
                // Before we fill, flip the fillState around. All existing entries will
                // then be seen as 'not yet filled', and every item we then fill with keys,
                // will be made correct. This eliminates otherwise needed iteration.
                SortedIdentityCacheLinkedList.this.fillState = !SortedIdentityCacheLinkedList.this.fillState;
            }

            @Override
            public void accept(K key) {
                // Most common case - value already there
                final LinkEntry<K, V> curr = this.curr;
                final boolean fillState = SortedIdentityCacheLinkedList.this.fillState;
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
                    this.hasChanges = true;
                }
            }

            public void trimRemaining() {
                // Break the chain at the current link entry
                LinkEntry<K, V> end = SortedIdentityCacheLinkedList.this.last;
                prev.next = end;
                end.prev = prev;

                // All entries that now remain have been removed
                // Remove them from the HashMap as well
                LinkEntry<K, V> curr = this.curr;
                for (; curr != end; curr = curr.next) {
                    entriesByKey.remove(curr.key);
                    synchronizer.onRemoved(curr.key, curr.value);
                    hasChanges = true;
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
                this.end = SortedIdentityCacheLinkedList.this.last;
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
                if (lastReturned == SortedIdentityCacheLinkedList.this.first) {
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
    }
}
