package com.bergerkiller.bukkit.common.collections.octree;

/**
 * Iterable that returns am {@link OctreeIterator}
 * 
 * @param <T>
 */
public interface OctreeIterable<T> extends Iterable<T> {
    @Override
    public OctreeIterator<T> iterator();
}
