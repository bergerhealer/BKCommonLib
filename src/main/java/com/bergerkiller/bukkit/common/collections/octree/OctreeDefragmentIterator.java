package com.bergerkiller.bukkit.common.collections.octree;

import java.util.BitSet;

/**
 * Iterates all values of the tree and generates a remapping array to defragment it
 * 
 * @param <T>
 */
public class OctreeDefragmentIterator<T> extends OctreeIterator<T> {
    private final int[] remapping;
    private final BitSet data_entries;
    private int counter;

    public OctreeDefragmentIterator(Octree<T> tree) {
        super(tree);
        this.remapping = new int[tree.table_size];
        this.data_entries = new BitSet(tree.table_size<<3);
        this.counter = 1; // Root at index=0 with value=0
    }

    /**
     * Gets the table size requires to represent all values optimally
     * 
     * @return table size
     */
    public int getTableSize() {
        return this.remapping.length << 3;
    }

    public int getRemapped(int node) {
        return (this.remapping[node >>> 3] << 3) | (node & 0x7);
    }

    public boolean isStoringDataValues(int node) {
        return this.data_entries.get(node >>> 3);
    }

    @Override
    protected Intersection intersect() {
        if (this.isDataNode()) {
            this.data_entries.set(this.getParentNode() >>> 3);
        } else {
            this.remapping[this.getNode()>>>3] = this.counter++;
        }
        return Intersection.PARTIAL;
    }
}
