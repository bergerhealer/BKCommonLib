package com.bergerkiller.bukkit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
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
        assertTrue(test.add(20));
        assertFalse(test.add(20));
        assertTrue(test.add(Long.MIN_VALUE));
        assertFalse(test.add(Long.MIN_VALUE));
        assertTrue(test.add(0));
        assertFalse(test.add(0));
        assertTrue(test.add(Long.MAX_VALUE));
        assertFalse(test.add(Long.MAX_VALUE));
        assertTrue(test.contains(Long.MIN_VALUE));
        assertTrue(test.contains(0));
        assertTrue(test.contains(Long.MAX_VALUE));
        assertEquals(4, test.size());

        // Verify iterator
        HashSet<Long> verifySet = new HashSet<Long>();
        iter = test.longIterator();
        while (iter.hasNext()) {
            verifySet.add(iter.next());
        }
        verifySpecialTest(verifySet);

        // Verify toArray()
        verifySet.clear();
        for (long value : test.toArray()) {
            verifySet.add(value);
        }
        verifySpecialTest(verifySet);

        // Verify iterator remove()
        verifySet.clear();
        iter = test.longIterator();
        while (iter.hasNext()) {
            long value = iter.next();
            iter.remove();
            assertFalse(test.contains(value));
            verifySet.add(value);
        }
        verifySpecialTest(verifySet);

        // Add again and verify popFirst()
        verifySet.clear();
        assertTrue(test.add(20));
        assertFalse(test.add(20));
        assertTrue(test.add(Long.MIN_VALUE));
        assertFalse(test.add(Long.MIN_VALUE));
        assertTrue(test.add(0));
        assertFalse(test.add(0));
        assertTrue(test.add(Long.MAX_VALUE));
        assertFalse(test.add(Long.MAX_VALUE));
        while (!test.isEmpty()) {
            long value = test.popFirst();
            assertFalse(test.contains(value));
            verifySet.add(value);
        }
        verifySpecialTest(verifySet);

        // Verify removal
        assertTrue(test.add(20));
        assertFalse(test.add(20));
        assertTrue(test.add(Long.MIN_VALUE));
        assertFalse(test.add(Long.MIN_VALUE));
        assertTrue(test.add(0));
        assertFalse(test.add(0));
        assertTrue(test.add(Long.MAX_VALUE));
        assertFalse(test.add(Long.MAX_VALUE));
        assertTrue(test.remove(20));
        assertFalse(test.remove(20));
        assertTrue(test.remove(Long.MIN_VALUE));
        assertFalse(test.remove(Long.MIN_VALUE));
        assertTrue(test.remove(0));
        assertFalse(test.remove(0));
        assertTrue(test.remove(Long.MAX_VALUE));
        assertFalse(test.remove(Long.MAX_VALUE));
        assertTrue(test.isEmpty());
        assertEquals(test.size(), 0);
    }

    @Test
    public void testHashSetClone() {
        LongHashSet test = new LongHashSet();
        assertTrue(test.add(20));
        assertTrue(test.add(Long.MIN_VALUE));
        assertTrue(test.add(0));
        assertTrue(test.add(Long.MAX_VALUE));
        assertEquals(4, test.size());

        LongHashSet clone = test.clone();
        assertTrue(clone.contains(20));
        assertTrue(clone.contains(Long.MIN_VALUE));
        assertTrue(clone.contains(0));
        assertTrue(clone.contains(Long.MAX_VALUE));
        assertEquals(4, clone.size());
    }

    private static void verifySpecialTest(HashSet<Long> set) {
        assertEquals(4, set.size());
        assertTrue(set.contains(Long.valueOf(20)));
        assertTrue(set.contains(Long.valueOf(Long.MIN_VALUE)));
        assertTrue(set.contains(Long.valueOf(0)));
        assertTrue(set.contains(Long.valueOf(Long.MAX_VALUE)));
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
