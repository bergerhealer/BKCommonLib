package com.bergerkiller.bukkit.common.collections.octree;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for the {@link DoubleOctree} with some additional methods
 * 
 * @param <T> The value type of the Octree
 */
public class DoubleOctreeIterator<T> implements Iterator<T> {
    private final OctreeIterator<DoubleOctree.Entry<T>> base_iter;
    private DoubleOctree.Entry<T> last_returned_parent;
    private DoubleOctree.Entry<T> last_returned;

    public DoubleOctreeIterator(OctreeIterator<DoubleOctree.Entry<T>> baseIterator) {
        this.base_iter = baseIterator;
        this.last_returned_parent = null;
        this.last_returned = null;
    }

    /**
     * Resets the iterator back to the beginning
     */
    public void reset() {
        this.base_iter.reset();
        this.last_returned = null;
        this.last_returned_parent = null;
    }

    /**
     * Gets the X-coordinate of the 1x1x1 block that the value returned by
     * {@link #next()} is stored in.
     * 
     * @return X-coordinate of the 1x1x1 block
     */
    public int getBlockX() {
        return this.base_iter.getX();
    }

    /**
     * Gets the Y-coordinate of the 1x1x1 block that the value returned by
     * {@link #next()} is stored in.
     * 
     * @return Y-coordinate of the 1x1x1 block
     */
    public int getBlockY() {
        return this.base_iter.getY();
    }

    /**
     * Gets the Z-coordinate of the 1x1x1 block that the value returned by
     * {@link #next()} is stored in.
     * 
     * @return Z-coordinate of the 1x1x1 block
     */
    public int getBlockZ() {
        return this.base_iter.getZ();
    }

    /**
     * Gets the X-coordinate of the value last returned by {@link #next()}
     * 
     * @return X-coordinate
     */
    public double getX() {
        return this.last_returned.getX();
    }

    /**
     * Gets the Y-coordinate of the value last returned by {@link #next()}
     * 
     * @return Y-coordinate
     */
    public double getY() {
        return this.last_returned.getY();
    }

    /**
     * Gets the Z-coordinate of the value last returned by {@link #next()}
     * 
     * @return Z-coordinate
     */
    public double getZ() {
        return this.last_returned.getZ();
    }

    @Override
    public boolean hasNext() {
        if (this.last_returned != null) {
            return this.last_returned.next != null || this.base_iter.hasNext();
        } else {
            return (this.last_returned_parent != null && this.last_returned_parent.next != null) || this.base_iter.hasNext();
        }
    }

    @Override
    public T next() {
        return this.nextEntry().getValue();
    }

    /**
     * Gets the next entry storing the next value and coordinates
     * 
     * @return next entry
     * @see #next()
     */
    public DoubleOctree.Entry<T> nextEntry() {
        if (this.last_returned == null) {
            if (this.last_returned_parent != null) {
                // Previous entry was removed using remove()
                // Continue iteration with parent next
                this.last_returned = this.last_returned_parent.next;
            } else {
                // First time calling nextEntry() or we removed the last entry of a chain
                this.last_returned = this.base_iter.next();
            }
            return this.last_returned;
        }

        // Go to next entry in the chain
        this.last_returned_parent = this.last_returned;
        this.last_returned = this.last_returned.next;
        if (this.last_returned == null) {
            // End of the chain. Find next chain.
            this.last_returned_parent = null;
            this.last_returned = this.base_iter.next();
        }
        return this.last_returned;
    }

    /**
     * Gets the last entry returned by {@link #nextEntry()}
     * 
     * @return last returned entry
     */
    public DoubleOctree.Entry<T> getEntry() {
        return this.last_returned;
    }

    @Override
    public void remove() {
        if (this.last_returned == null) {
            throw new NoSuchElementException("No call was done to next() or nextEntry()");
        }
        if (this.last_returned_parent == null) {
            if (this.last_returned.next == null) {
                this.base_iter.remove();
            } else {
                this.base_iter.put(this.last_returned.next);
            }
        } else {
            this.last_returned_parent.next = this.last_returned.next;
            if (this.last_returned_parent.next == null) {
                this.last_returned_parent = null;
            }
        }
        this.last_returned = null;
    }
}
