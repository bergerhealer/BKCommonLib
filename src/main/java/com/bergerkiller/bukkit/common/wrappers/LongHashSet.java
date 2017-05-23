package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongHashSetHandle;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBLongHashSet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A wrapper around the internal LongHashSet implementation. This type of
 * HashSet allows storing long keys. Ideally, two int values are merged into one
 * long to store 2D-coordinates.
 */
public class LongHashSet extends BasicWrapper<LongHashSetHandle> implements Iterable<Long> {

    public LongHashSet() {
        this.setHandle(LongHashSetHandle.createNew());
    }

    public LongHashSet(int size) {
        this.setHandle(LongHashSetHandle.createNew(size));
    }

    public LongHashSet(Object handle) {
        this.setHandle(LongHashSetHandle.createHandle(handle));
    }

    public Iterator<Long> iterator() {
        return handle.iterator();
    }

    public boolean add(int msw, int lsw) {
        return handle.addPair(msw, lsw);
    }

    public boolean add(long value) {
        return handle.add(value);
    }

    public boolean contains(int msw, int lsw) {
        return handle.containsPair(msw, lsw);
    }

    public boolean contains(long value) {
        return handle.contains(value);
    }

    public void remove(int msw, int lsw) {
        handle.removePair(msw, lsw);
    }

    public boolean remove(long value) {
        return handle.remove(value);
    }

    public void clear() {
        handle.clear();
    }

    public long[] toArray() {
        return handle.toArray();
    }

    public long popFirst() {
        return handle.popFirst();
    }

    public long[] popAll() {
        return handle.popAll();
    }

    public int hash(long value) {
        return CBLongHashSet.hash.invoke(handle, value);
    }

    public void rehash() {
        handle.rehash();
    }

    public void rehash(int newCapacity) {
        handle.rehashResize(newCapacity);
    }

    public boolean isEmpty() {
        return handle.isEmpty();
    }

    /**
     * Gets the amount of Long values stored in this LongHashSet
     *
     * @return size
     */
    public int size() {
        return Math.max(handle.size(), 0);
    }

    /**
     * Obtains an Iterator that returns long values instead of the Long object
     * type. This may be used to reduce garbage memory when iterating.
     *
     * @return long iterator
     */
    public LongIterator longIterator() {
        return new LongIterator(this);
    }

    /**
     * Class pretty much cloned from CraftBukkit's util/LongHashSet class. All
     * credits go to them (or whoever wrote it) Changes:<br>
     * - removed modified check (would slow down too much)<br>
     * - changed Long to long
     */
    public static class LongIterator {

        private int index;
        private int lastReturned = -1;
        private final LongHashSetHandle handle;
        private final long[] values;

        public LongIterator(LongHashSet source) {
            this.handle = source.handle;
            this.values = handle.getValuesField();
            for (index = 0; index < values.length && (values[index] == CBLongHashSet.FREE || values[index] == CBLongHashSet.REMOVED); index++) {
                // This is just to drive the index forward to the first valid entry
            }
        }

        public boolean hasNext() {
            return index != values.length;
        }

        public long next() {
            int length = values.length;
            if (index >= length) {
                lastReturned = -2;
                throw new NoSuchElementException();
            }

            lastReturned = index;
            for (index += 1; index < length && (values[index] == CBLongHashSet.FREE || values[index] == CBLongHashSet.REMOVED); index++) {
                // This is just to drive the index forward to the next valid entry
            }

            if (values[lastReturned] == CBLongHashSet.FREE) {
                return CBLongHashSet.FREE;
            } else {
                return values[lastReturned];
            }
        }

        public void remove() {
            if (lastReturned == -1 || lastReturned == -2) {
                throw new IllegalStateException();
            }

            if (values[lastReturned] != CBLongHashSet.FREE && values[lastReturned] != CBLongHashSet.REMOVED) {
                values[lastReturned] = CBLongHashSet.REMOVED;
                handle.setElementsCountField(handle.getElementsCountField() - 1);
            }
        }
    }
}
