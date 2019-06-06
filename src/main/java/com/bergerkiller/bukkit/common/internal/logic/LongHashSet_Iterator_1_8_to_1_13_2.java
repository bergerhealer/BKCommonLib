package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.wrappers.LongHashSet;

/**
 * Proxy for our internal LongHashSet iterator.
 */
public class LongHashSet_Iterator_1_8_to_1_13_2 extends LongHashSet.LongIterator {
    private final LongHashSet_pre_1_13_2.LongIterator baseIter;

    public LongHashSet_Iterator_1_8_to_1_13_2(Object handle) {
        this.baseIter = ((LongHashSet_pre_1_13_2) handle).iterator();
    }

    @Override
    public boolean hasNext() {
        return this.baseIter.hasNext();
    }

    @Override
    public long next() {
        return this.baseIter.nextLong();
    }

    @Override
    public void remove() {
        this.baseIter.remove();
    }
}
