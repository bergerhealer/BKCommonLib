package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.octree.Octree;

public class OctreeTest {

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
    public void testIterator() {
        Octree<String> tree = new Octree<String>();
        putDemoValues(tree);

        OctreeIterator<String> iter = tree.iterator();

        assertTrue(iter.hasNext());
        assertEquals("A", iter.next());
        assertPosEquals(iter, 0, 0, 0);

        assertTrue(iter.hasNext());
        assertEquals("D", iter.next());
        assertPosEquals(iter, 10, 100, 1000);

        assertTrue(iter.hasNext());
        assertEquals("E", iter.next());
        assertPosEquals(iter, 512, 512, 512);

        assertTrue(iter.hasNext());
        assertEquals("B", iter.next());
        assertPosEquals(iter, 1000, 1000, 1000);

        assertTrue(iter.hasNext());
        assertEquals("F", iter.next());
        assertPosEquals(iter, 1073741824, 1073741824, 1073741824);

        assertTrue(iter.hasNext());
        assertEquals("G", iter.next());
        assertPosEquals(iter, 2147483647, 2147483647, 2147483647);

        assertTrue(iter.hasNext());
        assertEquals("H", iter.next());
        assertPosEquals(iter, -2147483648, -2147483648, -2147483648);

        assertTrue(iter.hasNext());
        assertEquals("C", iter.next());
        assertPosEquals(iter, -1000, -1000, -1000);

        assertFalse(iter.hasNext());
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

        assertTrue(iter.hasNext());
        assertEquals("A", iter.next());
        assertPosEquals(iter, block.x, block.y, block.z);

        assertTrue(iter.hasNext());
        assertEquals("C", iter.next());
        assertPosEquals(iter, block.x, block.y+1, block.z);

        assertFalse(iter.hasNext());
    }

    @Ignore
    @Test
    public void runTest() {
        Octree.test2();
    }

    private void assertPosEquals(OctreeIterator<String> iter, int x, int y, int z) {
        assertEquals(x, iter.getX());
        assertEquals(y, iter.getY());
        assertEquals(z, iter.getZ());
    }
}
