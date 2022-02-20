package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;

/**
 * Tests the {@link SortedIdentityCache}
 */
public class SortedIdentityCacheTest {
    public static final TestKey[] KEYS = IntStream.range(0, 100).mapToObj(TestKey::new).toArray(TestKey[]::new);

    public static final class TestKey {
        public final int counter;

        public TestKey(int counter) {
            this.counter = counter;
        }

        @Override
        public String toString() {
            return "Key{" + counter + "}";
        }
    }

    public static final class TestValue {
        public final TestKey key;

        public TestValue(TestKey key) {
            this.key = key;
        }
    }

    @Test
    public void testFillSimple() {
        SortedIdentityCache<TestKey, TestValue> cache = SortedIdentityCache.create(TestValue::new);
        ArrayList<TestKey> keys = new ArrayList<>(Arrays.asList(KEYS));

        // Fill two times, one time in reverse order
        cache.sync(keys);
        verifyCache(cache, keys);

        Collections.reverse(keys);
        cache.sync(keys);
        verifyCache(cache, keys);
    }

    @Test
    public void testFillWithElementAddedAndRemoved() {
        SortedIdentityCache<TestKey, TestValue> cache = SortedIdentityCache.create(TestValue::new);
        ArrayList<TestKey> keys = new ArrayList<>();
        keys.add(KEYS[0]);
        keys.add(KEYS[1]);
        keys.add(KEYS[2]);
        keys.add(KEYS[3]);
        keys.add(KEYS[5]);

        cache.sync(keys);
        verifyCache(cache, keys);

        // Remove value and add new value elsewhere
        // Removes KEYS[2] and puts KEYS[4] after KEYS[3]
        keys.clear();
        keys.add(KEYS[0]);
        keys.add(KEYS[1]);
        keys.add(KEYS[3]);
        keys.add(KEYS[4]);
        keys.add(KEYS[5]);

        cache.sync(keys);
        verifyCache(cache, keys);
    }

    @Test
    public void testAddFirst() {
        SortedIdentityCache<TestKey, TestValue> cache = SortedIdentityCache.create(TestValue::new);
        for (TestKey key : KEYS) {
            cache.addFirst(key);
        }

        // Values should have keys in reverse order
        ArrayList<TestKey> reverseKeys = new ArrayList<>(Arrays.asList(KEYS));
        Collections.reverse(reverseKeys);
        verifyCache(cache, reverseKeys);

        // Adding keys again should not add new values
        for (TestKey key : KEYS) {
            cache.addFirst(key);
        }
        verifyCache(cache, reverseKeys);
    }

    @Test
    public void testAddLast() {
        SortedIdentityCache<TestKey, TestValue> cache = SortedIdentityCache.create(TestValue::new);
        for (TestKey key : KEYS) {
            cache.addLast(key);
        }

        // Values should have keys in same order
        verifyCache(cache, Arrays.asList(KEYS));

        // Adding keys again should not add new values
        for (TestKey key : KEYS) {
            cache.addLast(key);
        }
        verifyCache(cache, Arrays.asList(KEYS));
    }

    @Ignore
    @Test
    public void benchmarkFillInsert() {
        // Create a very large array up-front of values
        ArrayList<TestKey> initial = new ArrayList<TestKey>();
        for (int i = 0; i < 10000; i++) {
            initial.add(new TestKey(i));
        }

        // Create another array which has all values except some removed
        ArrayList<TestKey> holes = new ArrayList<>(initial);
        Random rand = new Random(10000324022L);
        for (int n = 0; n < 1000; n++) {
            holes.remove(rand.nextInt(holes.size()));
        }

        // Create cache, warm it up
        SortedIdentityCache<TestKey, TestValue> cache = SortedIdentityCache.create(TestValue::new);
        cache.sync(initial);
        cache.sync(holes);

        // Repeatedly sync between one and the other
        final int count = 10000;
        long start = System.currentTimeMillis();
        for (int n = 0; n < count; n++) {
            cache.sync(initial);
            cache.sync(holes);
        }
        long end = System.currentTimeMillis();
        System.out.println("TOOK: " + ((double) (end-start) / (double) count) + " millis/cycle");
    }

    private void verifyCache(SortedIdentityCache<TestKey, TestValue> cache, List<TestKey> keys) {
        assertEquals(keys.size(), cache.size());
        List<TestValue> verifyValues = cache.stream().collect(Collectors.toCollection(ArrayList::new));
        assertEquals(keys.size(), verifyValues.size());
        for (int i = 0; i < keys.size(); i++) {
            assertEquals(keys.get(i), verifyValues.get(i).key);
        }
    }
}
