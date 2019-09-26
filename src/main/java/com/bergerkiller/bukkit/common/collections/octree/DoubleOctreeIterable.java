package com.bergerkiller.bukkit.common.collections.octree;

/**
 * Iterable that returns a {@link DoubleOctreeIterator}
 * 
 * @param <T> The value type of the Double Octree
 */
public interface DoubleOctreeIterable<T> extends Iterable<T> {
    @Override
    public DoubleOctreeIterator<T> iterator();
}
