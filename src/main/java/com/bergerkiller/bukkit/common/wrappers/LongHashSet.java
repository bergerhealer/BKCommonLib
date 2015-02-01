package com.bergerkiller.bukkit.common.wrappers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.bergerkiller.bukkit.common.reflection.classes.LongHashSetRef;

/**
 * A wrapper around the internal LongHashSet implementation. This type of
 * HashSet allows storing long keys. Ideally, two int values are merged into one
 * long to store 2D-coordinates.
 */
public class LongHashSet extends BasicWrapper implements Iterable<Long> {

    public LongHashSet() {
        this.setHandle(LongHashSetRef.constructor1.newInstance());
    }

    public LongHashSet(int size) {
        this.setHandle(LongHashSetRef.constructor2.newInstance(size));
    }

    public LongHashSet(Object handle) {
        this.setHandle(handle);
    }

    public Iterator<Long> iterator() {
        return LongHashSetRef.iterator.invoke(handle);
    }

    public boolean add(int msw, int lsw) {
        return ((org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet) handle).add(msw, lsw);
    }

    public boolean add(long value) {
        return ((org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet) handle).add(value);
    }

    public boolean contains(int msw, int lsw) {
        return ((org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet) handle).contains(msw, lsw);
    }

    public boolean contains(long value) {
        return ((org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet) handle).contains(value);
    }

    public void remove(int msw, int lsw) {
        LongHashSetRef.remove2.invoke(handle, msw, lsw);
    }

    public boolean remove(long value) {
        return LongHashSetRef.remove1.invoke(handle, value);
    }

    public void clear() {
        LongHashSetRef.clear.invoke(handle);
    }

    public long[] toArray() {
        return LongHashSetRef.toArray.invoke(handle);
    }

    public long popFirst() {
        return ((org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet) handle).popFirst();
    }

    public long[] popAll() {
        return LongHashSetRef.popAll.invoke(handle);
    }

    public int hash(long value) {
        return LongHashSetRef.hash.invoke(handle, value);
    }

    public void rehash() {
        LongHashSetRef.rehash0.invoke(handle);
    }

    public void rehash(int newCapacity) {
        LongHashSetRef.rehash1.invoke(handle, newCapacity);
    }

    public boolean isEmpty() {
        return LongHashSetRef.isEmpty.invoke(handle);
    }

    /**
     * Gets the amount of Long values stored in this LongHashSet
     *
     * @return size
     */
    public int size() {
        return Math.max(((org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet) handle).size(), 0);
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
        private final Object handle;
        private final long[] values;

        public LongIterator(LongHashSet source) {
            this.handle = source.handle;
            this.values = LongHashSetRef.values.get(handle);
            for (index = 0; index < values.length && (values[index] == LongHashSetRef.FREE || values[index] == LongHashSetRef.REMOVED); index++) {
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
            for (index += 1; index < length && (values[index] == LongHashSetRef.FREE || values[index] == LongHashSetRef.REMOVED); index++) {
                // This is just to drive the index forward to the next valid entry
            }

            if (values[lastReturned] == LongHashSetRef.FREE) {
                return LongHashSetRef.FREE;
            } else {
                return values[lastReturned];
            }
        }

        public void remove() {
            if (lastReturned == -1 || lastReturned == -2) {
                throw new IllegalStateException();
            }

            if (values[lastReturned] != LongHashSetRef.FREE && values[lastReturned] != LongHashSetRef.REMOVED) {
                values[lastReturned] = LongHashSetRef.REMOVED;
                LongHashSetRef.elements.set(handle, LongHashSetRef.elements.get(handle) - 1);
            }
        }
    }
}
