package com.bergerkiller.bukkit.common.collections.octree;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator that iterates all stored values inside the table index.
 * Includes facility to extend it and add node cluster filtering.
 */
public class OctreeIterator<T> implements Iterator<T> {
    protected final Octree<T> tree;
    protected final int[] index;
    private boolean coord_dirty;
    private int x;
    private int y;
    private int z;
    private int depth;
    private IteratorState state;
    private int skipIntersectionBelowDepth;

    public OctreeIterator(Octree<T> tree) {
        this.tree = tree;
        this.coord_dirty = true;
        this.index = new int[33];
        this.reset();
    }

    /**
     * Resets the iterator back to the beginning
     */
    public void reset() {
        this.depth = this.index.length-1;
        this.index[this.depth] = 0;
        this.skipIntersectionBelowDepth = -1;
        this.state = IteratorState.INITIAL;
    }

    /**
     * Gets whether this iterator is in its initial state.
     * It will stay in this state until {@link #hasNext()} or {@link #next()}
     * are called.
     * 
     * @return True if in initial state, False if not.
     */
    public boolean isInitialState() {
        return this.state == IteratorState.INITIAL;
    }

    /**
     * Gets the X-coordinate of the value last returned by {@link #next()}
     * 
     * @return X-coordinate
     */
    public int getX() {
        if (this.coord_dirty) {
            this.genCoord();
        }
        return this.x;
    }

    /**
     * Gets the Y-coordinate of the value last returned by {@link #next()}
     * 
     * @return Y-coordinate
     */
    public int getY() {
        if (this.coord_dirty) {
            this.genCoord();
        }
        return this.y;
    }

    /**
     * Gets the Z-coordinate of the value last returned by {@link #next()}
     * 
     * @return Z-coordinate
     */
    public int getZ() {
        if (this.coord_dirty) {
            this.genCoord();
        }
        return this.z;
    }

    /**
     * Gets the index in the tree table where the node data can be found.
     * 
     * @return node index
     */
    protected int getNode() {
        return this.index[this.depth] & ~0x7;
    }

    /**
     * Gets the index in the tree table where the parent node that refers to {@link #getNode()}
     * can be found.
     * 
     * @return parent node index
     */
    protected int getParentNode() {
        return this.index[this.depth + 1] & ~0x7;
    }

    /**
     * Gets whether the current node obtained using {@link #getNode()} refers
     * to a data value, or not.
     * 
     * @return True if this node is a data value
     */
    protected boolean isDataNode() {
        return this.depth == 0;
    }

    /**
     * Gets the scale of the current node
     * 
     * @return scale
     */
    protected int getScale() {
        return 1 << this.depth;
    }

    /**
     * Override to handle the intersection with the search space.
     * <ul>
     * <li>To include this node and all nodes below, return INSIDE.
     * <li>To exclude this node and all nodes below, return OUTSIDE.
     * <li>To search deeper but perform further intersection tests, return PARTIAL.
     * </ul>
     * A guarantee is made that if this method only returns PARTIAL, every single
     * table tree node except for the root node shall be handled exactly once.
     * Methods such as {@link #getX()} and {@link #getNode()} provide information
     * about what is being tested right now.
     * 
     * @return intersection result
     */
    protected Intersection intersect() {
        return Intersection.INSIDE;
    }

    @Override
    public boolean hasNext() {
        return this.search() != 0;
    }

    @Override
    public T next() {
        int dataIndex = this.search();
        if (dataIndex == 0) {
            throw new NoSuchElementException("No more elements in Octal Search Tree");
        }
        T result = this.tree.values.getAt(dataIndex);
        this.state = IteratorState.FIND_NEXT;
        return result;
    }

    @Override
    public void remove() {
        if (this.state != IteratorState.FIND_NEXT) {
            throw new NoSuchElementException("Next must be called before elements can be removed");
        }

        // Remove data value from data at index[0]
        this.tree.values.removeAt(this.index[0] >>> 3);

        // This deallocates nodes without children recursively
        int node = this.index[++this.depth];
        while (true) {
            this.tree.table[node] = 0;
            if (this.tree.clean(node | 0x7)) {
                break; // has other children
            }
            if (this.depth == (this.index.length-1)) {
                break; // top node reached (entire tree is empty)
            }

            this.tree.deallocate(node & ~0x7);
            node = this.index[++this.depth];
        }
    }

    /**
     * Gets the value last returned using {@link #next()}
     * 
     * @return last returned value
     */
    public T get() {
        if (this.state != IteratorState.FIND_NEXT) {
            throw new NoSuchElementException("Next must be called before get() can be called");
        }
        return this.tree.values.getAt(this.index[0] >>> 3);
    }

    /**
     * Replaces the value last returned using {@link #next()} with a new value,
     * stored at the same coordinates.
     * 
     * @param value to set
     * @return previously stored value
     */
    public T put(T value) {
        if (this.state != IteratorState.FIND_NEXT) {
            throw new NoSuchElementException("Next must be called before get() can be called");
        }
        int index = (this.index[0] >>> 3);
        T oldValue = this.tree.values.getAt(index);
        this.tree.values.setAt(index, value);
        return oldValue;
    }

    /**
     * Performs a search operation for the next node
     *
     * @return found data index, or 0 if no more results are available
     */
    protected int search() {
        if (this.state == IteratorState.INITIAL) {
            this.state = IteratorState.READY;

            // Initially we start out at the root node, this requires special handling
            // Handle special case at 32 depth, this breaks because of Java's signed int logic
            // At 32 depth we also check that the root table entry isn't all-0 (wiped)
            // This state is impossible everywhere else (the node is put in garbage in that case)
            if (this.depth == 32) {
                this.depth--;
                int parent = 0;
                int node_index;
                while (true) {
                    // Find next child of root parent
                    if ((node_index = this.tree.table[parent]) != 0) {
                        parent = (parent & ~0x7) | (node_index & 0x7);
                        node_index &= ~0x7;

                        this.index[31] = node_index;
                        this.index[32] = parent;
                        this.coord_dirty = true;

                        Intersection intersection = this.intersect();
                        if (intersection == Intersection.PARTIAL) {
                            break;
                        } else if (intersection == Intersection.INSIDE) {
                            this.skipIntersectionBelowDepth = this.depth;
                            this.findFirstValueSkipIntersection(node_index);
                            return this.index[0] >> 3;
                        }
                    }

                    // Detect a fully empty tree
                    if ((++parent & 0x7) == 0) {
                        this.end();
                        return 0;
                    }
                }
            }

            // Find the first node in the tree
            this.findDeeper();
        } else if (this.state == IteratorState.FIND_NEXT) {
            this.state = IteratorState.READY;
            if (this.findNextSibling()) {
                this.findDeeper();
            }
        }
        return this.index[0] >>> 3;
    }

    /**
     * Looks for the very first data value from a parent node.
     * Recursively the first child node is picked, until a data value node is found.
     * Because every node is guaranteed to have a child or data value, this method
     * is extremely well optimized.
     * 
     * @param parent node from which to start looking
     */
    private void findFirstValueSkipIntersection(int parent) {
        this.coord_dirty = true;
        while (this.depth > 0) {
            int node_index = this.tree.table[parent];
            this.index[this.depth] = parent | (node_index & 0x7);
            parent = node_index & ~0x7;
            this.depth--;
        }

        // Store the final node as well (depth will be 0)
        this.index[this.depth] = parent;
    }

    private void findDeeper() {
        // Check that the current depth is below a point where we skip intersections
        // This can be omitted because we call findDeeperSkipIntersection() in place
        // As a result, when calling this function, this code is never hit!
        //
        // if (this.depth <= this.skipIntersectionBelowDepth) {
        //     this.findDeeperSkipIntersection();
        //     return;
        // }

        // Maintains the current value of index[depth], reducing lookups
        int parent = this.index[this.depth--] & ~0x7;
        while (true) {
            // Find one of the 8 children that is nonzero and perform intersection checks
            // This means there is a failure case where none intersect with a PARTIAL parent.
            int node_index;
            if ((node_index = this.tree.table[parent]) != 0) {
                parent = (parent & ~0x7) | (node_index & 0x7);
                node_index &= ~0x7;

                // Store this node and the changed parent in the index
                this.index[this.depth+1] = parent;
                this.index[this.depth] = node_index;
                this.coord_dirty = true;

                Intersection intersection = this.intersect();
                if (intersection == Intersection.PARTIAL) {
                    if (this.depth == 0) {
                        // End node reached, this should technically not happen as it is
                        // inside the final value node at this point. But we shall allow it...
                        this.coord_dirty = true;
                        return;
                    } else {
                        // Continue looping with the new node as a parent
                        parent = node_index;
                        this.depth--;
                        continue;
                    }
                } else if (intersection == Intersection.INSIDE) {
                    // This node and all that is below will be included, go through it fast
                    this.skipIntersectionBelowDepth = this.depth;
                    this.findFirstValueSkipIntersection(node_index);
                    return;
                } else if ((++parent & 0x7) != 0) {
                    // Next child node
                    continue;
                }
            }

            // Reached the end having found no available children, go to next sibling
            // If this returns false, then we reached a data value or the end of the tree
            if (!this.findNextSibling()) {
                return;
            }

            // Go deeper again with the new sibling as parent
            parent = this.index[this.depth--] & ~0x7;
        }
    }

    /**
     * Finds the next sibling node
     * 
     * @return True if further search using findDeeper() is needed, False otherwise
     */
    private boolean findNextSibling() {
        // Initially we start at depth+1 because we want a new child of the parent
        int parent = this.index[++this.depth];

        // Make sure we iterate only up to 8 siblings. Move back up if not found.
        // Check if the next node is available, and if so, pick the first child of it
        // If next depth is outside the 'fast' zone, break out to check intersection again.
        // Reset the previously set threshold as well, since it will refer to a new branch.
        // We can skip intersection checks during this (below threshold)
        if (this.depth <= this.skipIntersectionBelowDepth) {
            int node_index;
            while (true) {
                if ((++parent & 0x7) != 0 && (node_index = this.tree.table[parent]) != 0) {
                    this.index[this.depth--] = (parent & ~0x7) | (node_index & 0x7);
                    this.findFirstValueSkipIntersection(node_index & ~0x7);
                    return false;
                } else if (this.depth == this.skipIntersectionBelowDepth) {
                    this.skipIntersectionBelowDepth = -1;
                    parent--;
                    break;
                } else {
                    parent = this.index[++this.depth];
                }
            }
        }

        // Find the next sibling node of the parent
        // Slower because we must perform intersection tests
        // Make sure we iterate only up to 8 siblings. Move back up if not found.
        // If next depth is the limit then we reached the end of the tree.
        int node_index;
        while (true) {
            if ((++parent & 0x7) != 0 && (node_index = this.tree.table[parent]) != 0) {
                parent &= ~0x7;
                parent |= (node_index & 0x7);
                node_index &= ~0x7;

                // Store this node and the changed parent in the index
                this.index[this.depth] = parent;
                this.index[--this.depth] = node_index;
                this.coord_dirty = true;

                Intersection intersection = this.intersect();
                if (intersection == Intersection.INSIDE) {
                    this.skipIntersectionBelowDepth = this.depth;
                    this.findFirstValueSkipIntersection(node_index);
                    return false;
                } else if (intersection == Intersection.PARTIAL) {
                    // Now go deeper again to obtain the first value
                    // Not needed if depth is at 0 and we already reached the value
                    // Technically intersect() should not return partial in that case...
                    return this.depth > 0;
                } else {
                    // Restore depth and try next node
                    this.depth++;
                    continue;
                }
            }
            if (++this.depth == this.index.length) {
                this.end();
                return false;
            }
            parent = this.index[this.depth];
        }
    }

    private void end() {
        this.index[0] = 0;
    }

    private void genCoord() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        int node;
        for (int n = 32; n >= 3; n--) {
            node = this.index[n];
            this.x |= (node & 0x1);
            this.x <<= 1;
            this.y |= (node & 0x2);
            this.y <<= 1;
            this.z <<= 1;
            this.z |= (node & 0x4);
        }

        node = this.index[2];
        this.z |= (node & 0x4) >> 1;
        this.y |= (node & 0x2);
        this.x |= (node & 0x1);
        this.x <<= 1;

        node = this.index[1];
        this.z |= (node & 0x4) >> 2;
        this.y |= (node & 0x2) >> 1;
        this.x |= (node & 0x1);

        int mask = ~((1<<this.depth)-1);
        this.x &= mask;
        this.y &= mask;
        this.z &= mask;
        this.coord_dirty = false;
    }

    private static enum IteratorState {
        READY, INITIAL, FIND_NEXT
    }

    protected static enum Intersection {
        INSIDE, OUTSIDE, PARTIAL
    }
}
