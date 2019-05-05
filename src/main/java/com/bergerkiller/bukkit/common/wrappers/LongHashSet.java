package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.logic.LongHashSet_Iterator_1_14;
import com.bergerkiller.bukkit.common.internal.logic.LongHashSet_Iterator_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongHashSetHandle;

import java.util.Iterator;

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
        return handle.add(MathUtil.toLong(msw, lsw));
    }

    public boolean add(long value) {
        return handle.add(value);
    }

    public boolean contains(int msw, int lsw) {
        return handle.contains(MathUtil.toLong(msw, lsw));
    }

    public boolean contains(long value) {
        return handle.contains(value);
    }

    public boolean remove(int msw, int lsw) {
        return handle.remove(MathUtil.toLong(msw, lsw));
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
        return handle.popFirstElement();
    }

    public long[] popAll() {
        return handle.popAll();
    }

    public void trim() {
        handle.trim();
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
        if (CommonCapabilities.UTIL_COLLECTIONS_REMOVED) {
            return new LongHashSet_Iterator_1_14(this.getRawHandle());
        } else {
            return new LongHashSet_Iterator_1_8_to_1_13_2(this.getRawHandle());
        }
    }

    /**
     * Iterates over all long values in the HashSet efficiently
     */
    public static abstract class LongIterator {
        public abstract boolean hasNext();
        public abstract long next();
        public abstract void remove();
    }
}
