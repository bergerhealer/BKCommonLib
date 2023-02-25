package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.Iterator;

import com.bergerkiller.bukkit.common.wrappers.LongHashSet;

/**
 * Proxies the LongIterator that exists inside org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.
 * We have no efficient way to call nextLong() without having a random Object cast in the middle of it.
 * So we might as well just treat it like a normal Iterator&lt;Long&gt; and unbox the value.
 */
public class LongHashSet_Iterator_1_14 extends LongHashSet.LongIterator {
    private final Iterator<Long> iterator;

    @SuppressWarnings("unchecked")
    public LongHashSet_Iterator_1_14(Object handle) {
        this.iterator = ((Iterable<Long>) handle).iterator();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public long next() {
        return this.iterator.next().longValue();
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }

}
