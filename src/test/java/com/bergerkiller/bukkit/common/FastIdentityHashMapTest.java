package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.FastIdentityHashMap;

public class FastIdentityHashMapTest {

    @Test
    public void testGetPutRemove() {
        FastIdentityHashMap<String, String> map = new FastIdentityHashMap<String, String>();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        final String key1a = new String("Key1");
        final String key1b = new String("Key1");
        final String key1c = new String("Key1");

        // Put key1 in various ways
        assertNull(map.put(key1a, "Value1_a"));
        assertEquals("Value1_a", map.put(key1b, "Value1_b"));
        assertEquals("Value1_b", map.put(key1c, "Value1_c"));
        assertEquals("Value1_c", map.get(key1a));
        assertEquals("Value1_c", map.get(key1b));
        assertEquals("Value1_c", map.get(key1c));

        // Check size is 1 exactly
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEquals(new HashSet<String>(Arrays.asList("Key1")), map.keySet());
        assertEquals(Arrays.asList("Value1_c"), new ArrayList<String>(map.values()));

        final String key2a = new String("Key2");
        final String key2b = new String("Key2");

        // Put key2 in various ways
        assertNull(map.put(key2a, "Value2_a"));
        assertEquals("Value2_a", map.put(key2b, "Value2_b"));
        assertEquals("Value2_b", map.get(key2a));
        assertEquals("Value2_b", map.get(key2b));

        // Check size is 2 exactly
        assertFalse(map.isEmpty());
        assertEquals(2, map.size());
        assertEquals(new HashSet<String>(Arrays.asList("Key1", "Key2")), map.keySet());
        assertEquals(Arrays.asList("Value1_c", "Value2_b"), new ArrayList<String>(map.values()));

        // Remove key 1 again
        assertEquals("Value1_c", map.remove(new String("Key1")));

        // Check is removed
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEquals(new HashSet<String>(Arrays.asList("Key2")), map.keySet());
        assertEquals(Arrays.asList("Value2_b"), new ArrayList<String>(map.values()));

        // Check get by key 2 still works right
        assertEquals("Value2_b", map.get(key2a));
        assertEquals("Value2_b", map.get(key2b));
        assertEquals("Value2_b", map.get(new String("Key2")));

        // Clearing
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEquals(Collections.emptySet(), map.keySet());
        assertEquals(Collections.emptyList(), new ArrayList<String>(map.values()));
    }
}
