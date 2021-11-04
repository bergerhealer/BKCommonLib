package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet.Tracker;

public class FastTrackedUpdateSetTest {

    @Test
    public void testTrackerSet() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        Tracker<String> t = set.track("hello");
        assertEquals("hello", t.getValue());
        assertFalse(t.isSet());
        t.set(true);
        assertTrue(t.isSet());

        Iterator<String> iter = set.iterateAndClear().iterator();
        assertTrue(iter.hasNext());
        assertEquals("hello", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testTrackerSetAndClear() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        Tracker<String> t = set.track("hello");
        assertEquals("hello", t.getValue());
        assertFalse(t.isSet());
        t.set(true);
        assertTrue(t.isSet());
        t.set(false);
        assertFalse(t.isSet());

        Iterator<String> iter = set.iterateAndClear().iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    public void testTrackerSetMultiple() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        set.track("a").set(true);
        set.track("b").set(true);
        Tracker<String> t = set.track("c");
        t.set(true);
        t.set(false);
        set.track("d").set(true);

        Iterator<String> iter = set.iterateAndClear().iterator();
        assertTrue(iter.hasNext());
        assertEquals("a", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("b", iter.next());
        assertTrue(iter.hasNext());
        assertEquals("d", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testTrackerSetMultipleUnsafe() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        set.track("a").set(true);
        set.track("b").set(true);
        Tracker<String> t = set.track("c");
        t.set(true);
        t.set(false);
        set.track("d").set(true);

        Iterator<String> iter = set.iterateAndClear().iterator();
        assertEquals("a", iter.next());
        assertEquals("b", iter.next());
        assertEquals("d", iter.next());
        try {
            iter.next();
            fail("No exception was thrown");
        } catch (NoSuchElementException ex) {
            // Expect this
        }
    }

    @Test
    public void testTrackerConcurrentSettingOne() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        set.track("a").set(true);
        set.track("b").set(true);

        Iterator<String> iter = set.iterateAndClear().iterator();
        assertEquals("a", iter.next());
        set.track("c").set(true);
        assertEquals("b", iter.next());
        assertEquals("c", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testTrackerConcurrentSettingTwo() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        set.track("a").set(true);
        set.track("b").set(true);

        Iterator<String> iter = set.iterateAndClear().iterator();
        assertEquals("a", iter.next());
        assertEquals("b", iter.next());
        set.track("c").set(true);
        assertEquals("c", iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testTrackerResumeIteration() {
        FastTrackedUpdateSet<String> set = new FastTrackedUpdateSet<String>();
        set.track("a").set(true);
        set.track("b").set(true);
        set.track("c").set(true);

        // First iterator
        {
            Iterator<String> iter = set.iterateAndClear().iterator();
            assertEquals("a", iter.next());
            assertEquals("b", iter.next());
        }

        // Second iterator
        {
            Iterator<String> iter = set.iterateAndClear().iterator();
            assertEquals("c", iter.next());
            assertFalse(iter.hasNext());
        }
    }
}
