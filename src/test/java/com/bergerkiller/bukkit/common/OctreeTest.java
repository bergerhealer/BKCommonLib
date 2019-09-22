package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.octree.OctreeIterator;
import com.bergerkiller.bukkit.common.collections.octree.Octree;

public class OctreeTest {

    private void putDemoValues(Octree<String> tree) {
        tree.put(0, 0, 0, "A");
        tree.put(1000, 1000, 1000, "B");
        tree.put(-1000, -1000, -1000, "C");
        tree.put(-1000, 1000, -1000, "D");
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
        assertEquals("D", tree.get(-1000, 1000, -1000));
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
        assertTrue(iter.hasNext());
        assertEquals("E", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("B", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("F", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("G", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("D", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("H", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("C", iter.next());
        assertFalse(iter.hasNext());
    }

    @Ignore
    @Test
    public void runTest() {
        Octree.test2();
    }
}
