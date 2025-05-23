package com.bergerkiller.bukkit.common.collections.octree;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.IndexedCollection;

/**
 * Maps values to 3D x/y/z coordinates with integer resolution.
 * This is done by building an octree out of the x/y/z values, where each bit
 * in the value is a single layer. This layout is slower than hashtables,
 * but has the advantage of allowing faster lookups of all the values stored
 * inside a region. Values that lay outside of the region can quickly be
 * eliminated, reducing the number of comparison checks required.
 * 
 * @param <T> The value type of the Octree
 */
public class Octree<T> implements OctreeIterable<T> {
    protected int[] table;
    protected int table_size;
    protected final IndexedCollection<T> values;
    private int deallocated_node_index;
    protected final OctreePointIterator<T> remove_iter;

    public Octree() {
        this.values = new IndexedCollection<T>();
        this.values.reserve(1);
        this.values.setAt(0, null);
        this.clear();
        this.remove_iter = new OctreePointIterator<T>(this, 0, 0, 0);
    }

    /**
     * Clears all the contents of this Octree, freeing all memory associated with it.
     */
    public void clear() {
        this.values.clear();
        this.deallocated_node_index = 0; // must be resized
        this.table_size = 1; // stores root node only
        this.table = new int[8];
    }

    /**
     * Retrieves a node index from the pool of deallocated entries.
     * If more space is needed, the tree is resized to increase this pool.
     * The node will have zeroed-out values.
     * 
     * @return node index
     */
    protected int allocate() {
        // Grow the tree by 2x size if more nodes are needed, with a chain of deallocated nodes
        // The last node of the tree will have no next deallocated node (0)
        if (this.deallocated_node_index == 0) {
            this.deallocated_node_index = this.table.length;
            this.table = Arrays.copyOf(this.table, this.table.length << 1);
            int end_node_index = this.table.length - 8;
            for (int node = this.deallocated_node_index; node < end_node_index; node += 8) {
                this.table[node+1] = node+8;
            }
        }

        // Retrieve the deallocated node index and set the deallocated node index to next in line
        // Set the second field that was storing this value to 0 again.
        int node = this.deallocated_node_index;
        this.deallocated_node_index = this.table[++node];
        this.table[node--] = 0;
        this.table_size++;
        return node;
    }

    /**
     * Stores a node in the pool of deallocated entries.
     * A deallocated node has the first entry set to 0, with the following
     * entry set to the index of the previous free node in the chain.
     * If the usage of the table is significantly low enough after deallocating,
     * the table is resized to reduce memory usage.
     * 
     * @param node to deallocate
     */
    protected void deallocate(int node) {
        int next_deallocated_node_index = this.deallocated_node_index;
        this.deallocated_node_index = node;
        this.table[node] = 0;
        this.table[++node] = next_deallocated_node_index;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table[++node] = 0;
        this.table_size--;
    }

    /**
     * Defragments the tree so that, during natural iteration, the table is read incrementally.
     * Resizes the table structure to reduce memory usage if possible.
     */
    public void compress() {
        // Iterate all values in the tree to generate a remapping array
        OctreeDefragmentIterator<T> iter = new OctreeDefragmentIterator<T>(this);
        while (iter.hasNext()) {
            iter.next();
        }

        // Figure out the new size for the table
        int new_table_size = 8;
        while (new_table_size < iter.getTableSize()) {
            new_table_size <<= 1;
        }

        // In the order that we found the nodes using the iterator, move the nodes in the tree
        // Do not alter the indices of entries that refer to data values
        int[] new_table = new int[new_table_size];
        for (int i = 0; i < this.table.length; i += 8) {
            if (this.table[i] == 0) {
                continue; // deallocated entry
            }
            int new_pos = iter.getRemapped(i);
            if (iter.isStoringDataValues(i)) {
                System.arraycopy(this.table, i, new_table, new_pos, 8);
            } else {
                for (int k = 0; k < 8; k++) {
                    new_table[new_pos + k] = iter.getRemapped(this.table[i + k]);
                }
            }
        }

        // For all remaining entries in the table, initialize it with deallocated entries
        if (new_table_size == iter.getTableSize()) {
            this.deallocated_node_index = 0;
        } else {
            this.deallocated_node_index = iter.getTableSize();
            int end_node_index = new_table_size - 8;
            for (int node = this.deallocated_node_index; node < end_node_index; node += 8) {
                new_table[node+1] = node+8;
            }
        }

        // Assign the new table data
        this.table = new_table;
    }

    // Check table size < 1/2 of array, compressing if so to reduce memory usage
    private void compressIfNeeded() {
        if (this.table_size < (this.table.length >> 9)) {
            this.compress();
        }
    }

    /**
     * Gets the total number of tree nodes in this octree. The amount that is required highly
     * depends on the complexity of the data index.
     * 
     * @return table node count
     */
    public int getNodeCount() {
        return this.table_size;
    }

    /**
     * Gets the total number of data values stored in this octree.
     * 
     * @return data value count
     */
    public int size() {
        return this.values.size();
    }

    /**
     * Gets an unmodifiable view of all the values inside this tree
     * 
     * @return values
     */
    public Collection<T> values() {
        return Collections.unmodifiableCollection(this.values);
    }

    @Override
    public OctreeIterator<T> iterator() {
        return new OctreeIterator<T>(this);
    }

    /**
     * Gets a view of the contents of this tree that lay inside a cuboid area.
     * 
     * @param min coordinates of the cuboid (inclusive)
     * @param max coordinates of the cuboid (inclusive)
     * @return iterable
     */
    public OctreeIterable<T> cuboid(final IntVector3 min, final IntVector3 max) {
        return () -> new OctreeCuboidIterator<T>(Octree.this, min, max);
    }

    /**
     * Gets a view of the contents of this tree that lay inside a cuboid area.<br>
     * <br>
     * <b>Note:</b> the cuboid {@link IntCuboid#max} is exclusive, not inclusive like
     * the other cuboid method of this class is.
     *
     * @param cuboid IntCuboid with the min/max coordinate
     * @return iterable
     */
    public OctreeIterable<T> cuboid(final IntCuboid cuboid) {
        return cuboid(cuboid.min, cuboid.max.subtract(1, 1, 1));
    }

    protected boolean clean(int parent) {
        int node = 0;
        do {
            int new_node = this.table[parent];
            if (new_node != 0 && (new_node & 0x7) == (parent & 0x7)) {
                node = new_node;
            } else {
                this.table[parent] = node;
            }
        } while ((parent-- & 0x7) != 0);
        return node != 0;
    }

    /**
     * Removes the value stored at the x/y/z coordinates specified.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return value that was removed, or null if none was stored
     */
    public T remove(int x, int y, int z) {
        this.remove_iter.reset(x, y, z);
        if (this.remove_iter.hasNext()) {
            T value = this.remove_iter.next();
            this.remove_iter.remove();
            this.compressIfNeeded();
            return value;
        }
        return null;
    }

    /**
     * Gets whether a value is stored at the x/y/z coordinates specified.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * 
     * @return True if a value is stored, false if not
     */
    public boolean contains(int x, int y, int z) {
        return getValueIndex(x, y, z, false) != 0;
    }

    /**
     * Gets the value stored at the x/y/z coordinates specified.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @return value stored, or null if none was stored
     */
    public T get(int x, int y, int z) {
        return getValueAtIndex(getValueIndex(x, y, z, false));
    }

    /**
     * Puts a new value at the x/y/z coordinates specified.
     * 
     * @param x The X-coordinate
     * @param y The Y-coordinate
     * @param z The Z-coordinate
     * @param value
     * @return previous value stored at these coordinates, or null if none was stored
     */
    public T put(int x, int y, int z, T value) {
        return putValueAtIndex(getValueIndex(x, y, z, true), value);
    }

    /**
     * Puts a new value at a value index previously returned by {@link #getValueIndex(int, int, int, boolean)}.
     * 
     * @param index of the value data entry
     * @param value to set to
     * @return previous value stored at the index, or null if none was stored
     */
    public T putValueAtIndex(int index, T value) {
        T result = this.values.getAt(index);
        this.values.setAt(index, value);
        return result;
    }

    /**
     * Gets the value stored at a value index previously returned by {@link #getValueIndex(int, int, int, boolean)}.
     * Returns null for index 0.
     * 
     * @param index of the value data entry
     * @return value stored at the index, or null if none was stored
     */
    public T getValueAtIndex(int index) {
        return this.values.getAt(index);
    }

    /**
     * Gets the index to where the value is stored for a given x/y/z. If no value
     * is yet stored here, the missing entries are created to allocate such an index
     * when create is true. Future writes to this Octree invalidate the index returned by this method.
     * 
     * @param x       The X-coordinate
     * @param y       The Y-coordinate
     * @param z       The Z-coordinate
     * @param create  Whether to create a new entry for the value if it does not exist
     * @return value index, 0 if create is false and no entry exists
     */
    public int getValueIndex(int x, int y, int z, boolean create) {
        if (create) {
            // Go by all 32 bits of the x/y/z values and select the right relative index (0 - 7)
            int index = 0;
            for (int n = 0; n < 31; n++) {
                int subaddr = ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);
                index |= subaddr;

                int next_index = this.table[index];
                if (next_index == 0 || ((next_index & 0x7) != subaddr)) {
                    // Add a new node at this position
                    next_index = this.allocate();
                    this.table[index] = (next_index | subaddr);
                    clean(index);
                }

                index = next_index & ~0x7;
                x <<= 1;
                y <<= 1;
                z <<= 1;
            }

            // For the last bit we point to an entry in the data list
            int subaddr = ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);;
            index |= subaddr;
            int data_index = this.table[index];
            if (data_index == 0 || ((data_index & 0x7) != subaddr)) {
                data_index = this.values.addAndGetIndex(null);
                this.table[index] = (data_index << 3) | subaddr;
                clean(index);
            } else {
                data_index >>>= 3;
            }

            return data_index;
        } else {
            // Go by all 32 bits of the x/y/z values and select the right relative index (0 - 7)
            int index = 0;
            for (int n = 0; n < 31; n++) {
                int sub = ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);
                index |= sub;

                int next_index = this.table[index];
                if (next_index == 0 || ((next_index & 0x7) != sub)) {
                    // Not found and create is false, return 0
                    return 0;
                }

                index = next_index & ~0x7;
                x <<= 1;
                y <<= 1;
                z <<= 1;
            }

            // For the last bit we point to an entry in the data list
            int subaddr = ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);;
            index |= subaddr;
            int data_index = this.table[index];
            if (data_index == 0 || ((data_index & 0x7) != subaddr)) {
                // Not found and create is false, return 0
                return 0;
            } else {
                data_index >>>= 3;
            }

            return data_index;
        }
    }
}
