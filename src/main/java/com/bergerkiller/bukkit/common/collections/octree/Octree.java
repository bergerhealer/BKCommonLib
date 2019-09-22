package com.bergerkiller.bukkit.common.collections.octree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.bergerkiller.bukkit.common.bases.IntVector3;

public class Octree<T> implements OctreeIterable<T> {
    private static final int TABLE_GROW_RATE = 10240*8;
    protected int[] table;
    protected int table_size;
    protected final ArrayList<T> data;

    public Octree() {
        this.data = new ArrayList<T>();
        this.data.add(null); //idx=0 terminator
        this.table_size = 8;
        this.table = new int[TABLE_GROW_RATE];
    }

    @Override
    public OctreeIterator<T> iterator() {
        return new OctreeIterator<T>(this);
    }

    /**
     * Gets a view of the contents of this tree that lays inside a cuboid area.
     * 
     * @param min coordinates of the cuboid (inclusive)
     * @param max coordinates of the cuboid (inclusive)
     * @return iterable
     */
    public OctreeIterable<T> cuboid(final IntVector3 min, final IntVector3 max) {
        return new OctreeIterable<T>() {
            @Override
            public OctreeIterator<T> iterator() {
                return new OctreeCuboidIterator<T>(Octree.this, min, max);
            }
        };
    }

    /**
     * Defragments the tree so that, during natural iteration, the table is read incrementally.
     * Appears to have little to no effect on performance. Or will it? Nah...or?
     */
    public void defragment() {
        // Iterate all values in the tree to generate a remapping array
        OctreeDefragmentIterator<T> iter = new OctreeDefragmentIterator<T>(this);
        while (iter.hasNext()) {
            iter.next();
        }

        // In the order that we found the nodes using the iterator, move the nodes in the tree
        // Do not alter the indices of entries that refer to data values
        int[] new_table = new int[this.table.length];
        for (int i = 0; i < this.table_size; i += 8) {
            int new_pos = iter.getRemapped(i);
            if (iter.isStoringDataValues(i)) {
                System.arraycopy(this.table, i, new_table, new_pos, 8);
            } else {
                for (int k = 0; k < 8; k++) {
                    new_table[new_pos + k] = iter.getRemapped(this.table[i + k]);
                }
            }
        }

        // Assign the new table data
        this.table = new_table;
    }

    public void optimize(int parent) {
        int node = 0;
        do {
            int new_node = this.table[parent];
            if (new_node != 0 && (new_node & 0x7) == (parent & 0x7)) {
                node = new_node;
            } else {
                this.table[parent] = node;
            }
        } while ((parent-- & 0x7) != 0);
    }

    public T get(int x, int y, int z) {
        // Go by all 32 bits of the x/y/z values and select the right relative index (0 - 7)
        int index = 0;
        for (int n = 0; n < 31; n++) {
            int sub = ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);
            index = this.table[index | sub];
            if (index == 0 || ((index & 0x7) != sub)) {
                return null;
            }

            index &= ~0x7;
            x <<= 1;
            y <<= 1;
            z <<= 1;
        }
        index |= ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);
        return this.data.get(this.table[index] >> 3);
    }

    public void put(int x, int y, int z, T value) {
        // Go by all 32 bits of the x/y/z values and select the right relative index (0 - 7)
        int index = 0;
        for (int n = 0; n < 31; n++) {
            int subaddr = ((x&(0x80000000))>>>31) | ((y&(0x80000000))>>>30) | ((z&(0x80000000))>>>29);
            index |= subaddr;

            int next_index = this.table[index];
            if (next_index == 0 || ((next_index & 0x7) != subaddr)) {
                // Add a new node at this position
                next_index = this.table_size;
                this.table_size += 8;
                if (this.table_size == this.table.length) {
                    this.table = Arrays.copyOf(this.table, this.table_size + TABLE_GROW_RATE);
                }
                this.table[index] = (next_index | subaddr);
                optimize(index);
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
            //System.out.println("STORE DATA " + index + " -> " + this.data.size());
            this.table[index] = (this.data.size() << 3) | subaddr;
            optimize(index);
            this.data.add(value);
        } else {
            this.data.set(data_index >> 3, value);
        }
    }

    private static void addCluster(Octree<String> tree, int x, int y, int z, int radius, int count) {
        Random rand = new Random(0x23242342);
        x -= radius;
        y -= radius;
        z -= radius;
        radius *= 2;
        while (count-- > 0) {
            int vx = x + rand.nextInt(radius);
            int vy = y + rand.nextInt(radius);
            int vz = z + rand.nextInt(radius);
            //String value = "[" + Integer.toString(vx) + "/" + Integer.toString(vy) + "/" + Integer.toString(vz) + "]";
            tree.put(vx, vy, vz, "lol");
        }
    }

    public static void test3() {
        Octree<String> tree = new Octree<String>();

        tree.put(12214211, 42352352, 235236236, "hello");
        tree.put(12214211, 42352351, 235236236, "wat");
        tree.put(2147483647, -2147483648, 2147483647, "test");
        tree.put(2147483646, -2147483648, 2147483646, "west");
        tree.put(10, -200, 500, "cuboid");
        tree.put(10, -200-3, 500, "cuboid_wot");
        tree.put(10, -200-5, 500, "cuboid_not");
        tree.put(1000, -200-5, 5000, "cuboid_lot");

        tree.defragment();

        System.out.println(tree.get(12214211, 42352352, 235236236));
        System.out.println(tree.get(12214211, 42352351, 235236236));
        System.out.println(tree.get(2147483647, -2147483648, 2147483647));
        System.out.println(tree.get(2147483646, -2147483648, 2147483646));

        OctreeIterator<String> iter = tree.iterator();
        System.out.println("ALL VALUES:");
        while (iter.hasNext()) {
            System.out.println("- " + iter.next() + " [ " + iter.getX() + "/" + iter.getY() + "/" + iter.getZ() + " ]");
        }

        OctreeIterator<String> citer = tree.cuboid(new IntVector3(10-1, -210-1, 500-1), new IntVector3(10000+1, -200+1, 500000+1)).iterator();
        System.out.println("ALL VALUES INSIDE CUBOID:");
        while (citer.hasNext()) {
            System.out.println("- " + citer.next() + " [ " + citer.getX() + "/" + citer.getY() + "/" + citer.getZ() + " ]");
        }
    }

    public static void test2() {
        Octree<String> bst = new Octree<String>();
        IntVector3 test_cluster = new IntVector3(0, 0, 0);
        addCluster(bst, test_cluster.x, test_cluster.y, test_cluster.z, 500, 1000);
        //bst.put(test_cluster.x, test_cluster.y, test_cluster.z, "lol");

        Random rand = new Random(0x6323432);
        int total_clusters = 500;
        int cluster_spread = 25000;
        for (int n = 0; n < total_clusters; n++) {
            addCluster(bst, rand.nextInt(2*cluster_spread)-cluster_spread, rand.nextInt(2*cluster_spread)-cluster_spread, rand.nextInt(2*cluster_spread)-cluster_spread, 500, 1000);
            System.out.println("ADDED CLUSTER " + (n+1) + "/" + total_clusters + " (table length: " + bst.table_size + ")");
        }

        bst.defragment();

        IntVector3 cluster_min = test_cluster.subtract(500, 500, 500);
        IntVector3 cluster_max = test_cluster.add(500, 500, 500);
        //IntVector3 cluster_min = test_cluster.subtract(0, 0, 0);
        //IntVector3 cluster_max = test_cluster.add(0, 0, 0);

        int total_in_tree = 0;
        int totalcount_expected = 0;
        {
            OctreeIterator<String> citer = bst.iterator();
            while (citer.hasNext()) {
                citer.next();
                total_in_tree++;
                if (citer.getX() >= cluster_min.x && citer.getX() <= cluster_max.x &&
                    citer.getY() >= cluster_min.y && citer.getY() <= cluster_max.y &&
                    citer.getZ() >= cluster_min.z && citer.getZ() <= cluster_max.z) {
                    totalcount_expected++;
                }
            }
        }

        int totalcount = 0;
        long start = System.currentTimeMillis();
        int iterations = 10000;
        for (int n = 0; n < iterations; n++) {
            totalcount = 0;
            OctreeIterator<String> citer = bst.cuboid(cluster_min, cluster_max).iterator();
            while (citer.hasNext()) {
                citer.next();
                totalcount++;
            }
        }

        System.out.println("TOTAL VALUES IN TREE: " + total_in_tree);
        System.out.println("ITERATING CUBOIDS: " + ((double)(System.currentTimeMillis()-start)/(double)iterations) + "ms (" + totalcount + "/" + totalcount_expected + ")");
        System.out.println("DEBUG NUMBER OF BOUNDS CHECKS: " + (OctreeCuboidIterator.NUM_INTERSECTS/iterations));
        System.out.println("DEBUG A: " + (OctreeIterator.COUNTER_A/iterations));
        System.out.println("DEBUG B: " + (OctreeIterator.COUNTER_B/iterations));
        System.out.println("DEBUG C: " + (OctreeIterator.COUNTER_C/iterations));
        System.out.println("DEBUG D: " + (OctreeIterator.COUNTER_D/iterations));
        /*
        ArrayList<IntVector3> inputs = new ArrayList<IntVector3>();
        Random r = new Random();
        for (int n = 0; n < 100000; n++) {
            int x = r.nextInt(10000) - 5000;
            int y = r.nextInt(10000) - 5000;
            int z = r.nextInt(10000) - 5000;
            bst.put(x, y, z, Integer.toString(n));
            inputs.add(new IntVector3(x,y,z));
        }

        System.out.println("VERIFY");

        IntVector3 v = inputs.get(0);
        for (int k = 0; k < 10000000; k++) {
            bst.get(v.x, v.y, v.z);
        }
        */

        /*
        for (int n = 0; n < 100000; n++) {
            IntVector3 v = inputs.get(n);
            if (!Integer.toString(n).equals(bst.get(v.x, v.y, v.z))) {
                System.out.println("NOOOOO");
            }
        }
        */

        //System.out.println("PUT EM ALL "  + bst.table.length);
    }
}
