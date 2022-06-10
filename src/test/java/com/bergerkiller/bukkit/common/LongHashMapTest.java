package com.bergerkiller.bukkit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.bergerkiller.bukkit.common.wrappers.LongHashMap;

public class LongHashMapTest {

    @Test
    public void testHashMap() {
        LongHashMap<String> test = new LongHashMap<String>();
        test.put(1546464643643436634L, "test_string");
        assertEquals("test_string", test.get(1546464643643436634L));
        assertNull(test.get(1546463213643436633L));
        assertTrue(test.contains(1546464643643436634L));
        assertFalse(test.contains(1546463213643436633L));

        assertEquals(1, test.size());

        long[] keys = test.getKeys();
        assertEquals(1, keys.length);
        assertEquals(1546464643643436634L, keys[0]);

        Collection<String> values = test.getValues();
        assertEquals(1, values.size());
        assertEquals("test_string", values.iterator().next());

        test.clear();

        assertEquals(0, test.size());
        assertFalse(test.contains(1546464643643436634L));
    }

    @Test
    public void testHashMapMerge() {
        LongHashMap<String> test = new LongHashMap<String>();
        assertEquals("First", test.merge(5, "First", (a, b) -> a + b));
        assertEquals("FirstSecond", test.merge(5, "Second", (a, b) -> a + b));
        assertEquals("FirstSecond", test.get(5));
        assertEquals("Other", test.merge(6, "Other", (a, b) -> a + b));
    }

    @Test
    public void testHashMapClone() {
        LongHashMap<String> test = new LongHashMap<String>();
        test.put(1546464643643436634L, "test_string_a");
        test.put(0L, "test_string_b");
        test.put(-1546464643643436634L, "test_string_c");
        assertEquals(3, test.size());

        LongHashMap<String> clone = test.clone();
        assertTrue(clone.contains(1546464643643436634L));
        assertTrue(clone.contains(0L));
        assertTrue(clone.contains(-1546464643643436634L));
        assertEquals("test_string_a", clone.get(1546464643643436634L));
        assertEquals("test_string_b", clone.get(0L));
        assertEquals("test_string_c", clone.get(-1546464643643436634L));
        assertEquals(3, clone.size());
    }
}
