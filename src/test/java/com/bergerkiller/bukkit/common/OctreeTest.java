package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.bergerkiller.bukkit.common.bases.IntCuboid;
import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.octree.Octree;

public class OctreeTest {

    /**
     * Forces a huge amount of random values with a random cuboid search over it,
     * lots of times.
     */
    @Ignore
    @Test
    public void fuzzTest() {
        for (int i = 0; i < 1000; i++) {
            Octree<String> tree = new Octree<>();

            Random r = new Random();
            Set<IntVector3> expectedCoords = new HashSet<>();

            IntCuboid cuboid = IntCuboid.create(
                    IntVector3.of(
                            -2000 + r.nextInt(1500),
                            -2000 + r.nextInt(1500),
                            -2000 + r.nextInt(1500)),
                    IntVector3.of(
                            2000 + r.nextInt(1500),
                            2000 + r.nextInt(1500),
                            2000 + r.nextInt(1500)));

            for (int n = 0; n < 10000; n++) {
                int x = r.nextInt(4000) - 2000;
                int y = r.nextInt(4000) - 2000;
                int z = r.nextInt(4000) - 2000;
                if (cuboid.contains(x, y, z)) {
                    expectedCoords.add(IntVector3.of(x, y, z));
                }
                tree.put(x, y, z, "[" + x + " " + y + " " + z + "]");
            }

            Set<IntVector3> containedInZone = new HashSet<>(expectedCoords.size());

            OctreeIterator<String> iter = tree.cuboid(cuboid).iterator();
            while (iter.hasNext()) {
                iter.next();
                containedInZone.add(IntVector3.of(iter.getX(), iter.getY(), iter.getZ()));
            }

            assertEquals(expectedCoords, containedInZone);
        }
    }

    private void putDemoValues(Octree<String> tree) {
        tree.put(0, 0, 0, "A");
        tree.put(1000, 1000, 1000, "B");
        tree.put(-1000, -1000, -1000, "C");
        tree.put(10, 100, 1000, "D");
        tree.put(512, 512, 512, "E");
        tree.put(1073741824, 1073741824, 1073741824, "F");
        tree.put(2147483647, 2147483647, 2147483647, "G");
        tree.put(-2147483648, -2147483648, -2147483648, "H");
    }

    @Test
    public void testPutGet() {
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        assertEquals("A", tree.get(0, 0, 0));
        assertEquals("B", tree.get(1000, 1000, 1000));
        assertEquals("C", tree.get(-1000, -1000, -1000));
        assertEquals("D", tree.get(10, 100, 1000));
        assertEquals("E", tree.get(512, 512, 512));
        assertEquals("F", tree.get(1073741824, 1073741824, 1073741824));
        assertEquals("G", tree.get(2147483647, 2147483647, 2147483647));
        assertEquals("H", tree.get(-2147483648, -2147483648, -2147483648));
    }

    @Test
    public void testRemove() {
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        // This complexity was expected to require 178 nodes
        assertEquals(178, tree.getNodeCount());

        // Remove D and E
        assertEquals("D", tree.remove(10, 100, 1000));
        assertEquals(null, tree.get(10, 100, 1000));
        assertEquals("E", tree.remove(512, 512, 512));
        assertEquals(null, tree.get(512, 512, 512));

        // Complexity should have gone down to 161 nodes
        assertEquals(161, tree.getNodeCount());

        // Others should remain unchanged
        assertEquals("A", tree.get(0, 0, 0));
        assertEquals("B", tree.get(1000, 1000, 1000));
        assertEquals("C", tree.get(-1000, -1000, -1000));
        assertEquals("F", tree.get(1073741824, 1073741824, 1073741824));
        assertEquals("G", tree.get(2147483647, 2147483647, 2147483647));
        assertEquals("H", tree.get(-2147483648, -2147483648, -2147483648));

        // Remove all other nodes
        assertEquals("A", tree.remove(0, 0, 0));
        assertEquals("B", tree.remove(1000, 1000, 1000));
        assertEquals("C", tree.remove(-1000, -1000, -1000));
        assertEquals("F", tree.remove(1073741824, 1073741824, 1073741824));
        assertEquals("G", tree.remove(2147483647, 2147483647, 2147483647));
        assertEquals("H", tree.remove(-2147483648, -2147483648, -2147483648));

        // With all data gone, we expect only the root node to remain
        assertEquals(1, tree.getNodeCount());
    }

    @Test
    public void testIterator() {
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        OctreeIterator<String> iter = tree.iterator();
        assertNext(iter, "A", 0, 0, 0);
        assertNext(iter, "D", 10, 100, 1000);
        assertNext(iter, "E", 512, 512, 512);
        assertNext(iter, "B", 1000, 1000, 1000);
        assertNext(iter, "F", 1073741824, 1073741824, 1073741824);
        assertNext(iter, "G", 2147483647, 2147483647, 2147483647);
        assertNext(iter, "H", -2147483648, -2147483648, -2147483648);
        assertNext(iter, "C", -1000, -1000, -1000);
        assertFalse(iter.hasNext());
    }

    @Test
    public void testValues() {
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        Collection<String> values = tree.values();
        assertTrue(values.contains("A"));
        assertTrue(values.contains("B"));
        assertTrue(values.contains("C"));
        assertTrue(values.contains("D"));
        assertTrue(values.contains("E"));
        assertTrue(values.contains("F"));
        assertTrue(values.contains("G"));
        assertTrue(values.contains("H"));
        assertFalse(values.contains(null));

        // We expect this to iterate in the order we added them
        Iterator<String> iter = values.iterator();
        assertNext(iter, "A");
        assertNext(iter, "B");
        assertNext(iter, "C");
        assertNext(iter, "D");
        assertNext(iter, "E");
        assertNext(iter, "F");
        assertNext(iter, "G");
        assertNext(iter, "H");
        assertFalse(iter.hasNext());
    }

    @Test
    public void testIteratorRemove() {
        //TODO!!!
    }

    @Test
    public void testSmallCuboid() {
        // Tests a single-block area cuboid, which is challenging because
        // the last data-entry holding node will be filtered only
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        IntVector3 block = new IntVector3(10, 100, 1000);
        OctreeIterator<String> iter = tree.cuboid(block, block).iterator();
        assertTrue(iter.hasNext());
        assertEquals("D", iter.next());
        assertEquals(10, iter.getX());
        assertEquals(100, iter.getY());
        assertEquals(1000, iter.getZ());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testInvalidCuboid() {
        // Tests an invalid cuboid (max is less than min) to simulate an 'empty' cuboid search
        // Should return no values and exit sanely
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        IntVector3 block = new IntVector3(10, 100, 1000);
        OctreeIterator<String> iter = tree.cuboid(block, block.subtract(1, 1, 1)).iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    public void testIteratorSelectSibling() {
        // Tests a very specific edge case where, while trying to find a value,
        // it first runs into a direct sibling of the value. This causes the algorithm
        // to look for a sibling at depth=0 which has some interesting consequences.
        IntVector3 block = new IntVector3(1, 0, 0);
        Octree<String> tree = new Octree<String>();
        tree.put(block.x, block.y, block.z, "A");
        tree.put(block.x, block.y, block.z+1, "B");
        tree.put(block.x, block.y+1, block.z, "C");

        OctreeIterator<String> iter = tree.cuboid(block, block.add(1, 1, 0)).iterator();
        assertNext(iter, "A", block.x, block.y, block.z);
        assertNext(iter, "C", block.x, block.y+1, block.z);
        assertFalse(iter.hasNext());
    }

    private void assertNext(Iterator<String> iter, String value) {
        assertTrue(iter.hasNext());
        assertEquals(value, iter.next());
    }

    private void assertNext(OctreeIterator<String> iter, String value, int x, int y, int z) {
        assertTrue(iter.hasNext());
        assertEquals(value, iter.next());
        assertEquals(x, iter.getX());
        assertEquals(y, iter.getY());
        assertEquals(z, iter.getZ());
    }
}
