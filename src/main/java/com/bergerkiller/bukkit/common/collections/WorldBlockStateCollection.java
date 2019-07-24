package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.utils.ChunkUtil;

/**
 * Exposes a Collection of chunks as a Collection of Block States for a world.
 * Makes sure no chunks are loaded while iterating.
 */
public class WorldBlockStateCollection implements Collection<BlockState> {
    private final Collection<Chunk> chunks;

    public WorldBlockStateCollection(Collection<Chunk> chunks) {
        this.chunks = chunks;
    }

    @Override
    public java.util.Iterator<BlockState> iterator() {
        return new Iterator(this.chunks.iterator());
    }

    @Override
    public boolean contains(Object o) {
        for (Chunk chunk : this.chunks) {
            if (ChunkUtil.getBlockStates(chunk).contains(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        int size = 0;
        for (Chunk chunk : this.chunks) {
            size += ChunkUtil.getBlockStates(chunk).size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (Chunk chunk : this.chunks) {
            if (!ChunkUtil.getBlockStates(chunk).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean add(BlockState e) {
        throw new UnsupportedOperationException("World Block States Collection is not modifiable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("World Block States Collection is not modifiable");
    }

    @Override
    public boolean addAll(Collection<? extends BlockState> c) {
        throw new UnsupportedOperationException("World Block States Collection is not modifiable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("World Block States Collection is not modifiable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("World Block States Collection is not modifiable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("World Block States Collection is not modifiable");
    }

    private final class Iterator implements java.util.Iterator<BlockState> {
        private final java.util.Iterator<Chunk> chunkIter;
        private java.util.Iterator<BlockState> currBlockStateIter;

        public Iterator(java.util.Iterator<Chunk> chunkIter) {
            this.chunkIter = chunkIter;
            this.currBlockStateIter = Collections.emptyIterator();
        }

        @Override
        public boolean hasNext() {
            while (!this.currBlockStateIter.hasNext()) {
                if (this.chunkIter.hasNext()) {
                    this.currBlockStateIter = ChunkUtil.getBlockStates(this.chunkIter.next()).iterator();
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public BlockState next() {
            while (!this.currBlockStateIter.hasNext()) {
                if (this.chunkIter.hasNext()) {
                    this.currBlockStateIter = ChunkUtil.getBlockStates(this.chunkIter.next()).iterator();
                } else {
                    throw new NoSuchElementException("No next element available");
                }
            }
            return this.currBlockStateIter.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("World Block States Collection is not modifiable");
        }
    }
}
