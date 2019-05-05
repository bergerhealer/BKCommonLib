package com.bergerkiller.bukkit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.bergerkiller.bukkit.common.wrappers.LongHashSet;

public class LongHashSetTest {

    @Test
    public void testHashSet() {
        LongHashSet test = new LongHashSet();
        assertTrue(test.isEmpty());
        test.add(1546464643643436634L);
        assertFalse(test.isEmpty());
        assertTrue(test.contains(1546464643643436634L));
        assertFalse(test.contains(1546463213643436633L));

        assertEquals(1, test.size());

        long[] keys = test.toArray();
        assertEquals(1, keys.length);
        assertEquals(1546464643643436634L, keys[0]);

        LongHashSet.LongIterator iter = test.longIterator();
        assertTrue(iter.hasNext());
        assertEquals(1546464643643436634L, iter.next());
        assertFalse(iter.hasNext());

        test.clear();

        assertEquals(0, test.size());
        assertTrue(test.isEmpty());
        assertFalse(test.contains(1546464643643436634L));

        test.add(50, 20);
        assertTrue(test.contains(50, 20));
        assertTrue(test.remove(50, 20));
        assertFalse(test.remove(50, 20));
        assertFalse(test.contains(50, 20));

        test.add(5002323);
        assertEquals(5002323, test.popFirst());
        assertEquals(0, test.size());
        assertTrue(test.isEmpty());

        try {
            test.popFirst();
            fail("popFirst() on empty HashSet does not throw exception");
        } catch (NoSuchElementException ex) {
            // ok
        }

        for (int i = 0; i < 50; i++) {
            test.add(i);
        }
        assertAllValuesExist(test.toArray(), 50);
        assertAllValuesExist(test.popAll(), 50);
        assertTrue(test.isEmpty());
        assertEquals(0, test.size());

        // <= 1.13.2 uses some strange code, check that this actually works
        if (Common.evaluateMCVersion(">=", "1.14")) {
            test.add(Long.MIN_VALUE);
            test.add(0);
            test.add(Long.MAX_VALUE);
            assertTrue(test.contains(Long.MIN_VALUE));
            assertTrue(test.contains(0));
            assertTrue(test.contains(Long.MAX_VALUE));
            assertEquals(3, test.size());
        } else {
            System.out.println("WARNING: LONGHASHSET IS BUGGED AND CANNOT STORE 0");
        }
    }

    private static void assertAllValuesExist(long[] values, int num) {
        assertEquals(values.length, num);
        for (int i = 0; i < num; i++) {
            boolean found = false;
            for (int j = 0; j < num; j++) {
                if (values[j] == i) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }
}
