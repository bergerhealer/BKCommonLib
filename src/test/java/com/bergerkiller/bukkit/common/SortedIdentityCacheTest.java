package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;

/**
 * Tests the {@link SortedIdentityCache}
 */
@RunWith(Parameterized.class)
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

    private String name;
    private Supplier<SortedIdentityCache<TestKey, TestValue>> sortedIdentityCacheConstructor;

    public SortedIdentityCacheTest(String name, Supplier<SortedIdentityCache<TestKey, TestValue>> sortedIdentityCacheConstructor) {
        this.name = name;
        this.sortedIdentityCacheConstructor = sortedIdentityCacheConstructor;
    }

    private SortedIdentityCache<TestKey, TestValue> createSortedIdentityCache() {
        return sortedIdentityCacheConstructor.get();
    }

    private static Object[] createTestParameters(String name, Supplier<SortedIdentityCache<TestKey, TestValue>> sortedIdentityCacheConstructor) {
        return new Object[] { name, sortedIdentityCacheConstructor };
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<?> input() {
        return Arrays.asList(createTestParameters("SortedIdentityCacheList", () -> SortedIdentityCache.create(TestValue::new)),
                             createTestParameters("SortedIdentityCacheLinkedList", () -> SortedIdentityCache.createLinked(TestValue::new)));
    }

    @Test
    public void testSyncSimple() {
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();
        ArrayList<TestKey> keys = new ArrayList<>(Arrays.asList(KEYS));

        // Fill two times, one time in reverse order
        cache.sync(keys);
        verifyCache(cache, keys);

        Collections.reverse(keys);
        cache.sync(keys);
        verifyCache(cache, keys);
    }

    @Test
    public void testSyncWithElementAddedAndRemoved() {
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2], KEYS[3], KEYS[5]);

        // Remove value and add new value elsewhere
        // Removes KEYS[2] and puts KEYS[4] after KEYS[3]
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[3], KEYS[4], KEYS[5]);

        // And in reverse
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2], KEYS[3], KEYS[5]);
    }

    @Test
    public void testSyncSwapElementAtBeginning() {
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();

        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2]);
        syncAndVerifyCache(cache, KEYS[3], KEYS[1], KEYS[2]);
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2]);
    }

    @Test
    public void testSyncSwapElementMiddle() {
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();

        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2]);
        syncAndVerifyCache(cache, KEYS[0], KEYS[3], KEYS[2]);
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2]);
    }

    @Test
    public void testSyncSwapElementAtEnd() {
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();

        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2]);
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[3]);
        syncAndVerifyCache(cache, KEYS[0], KEYS[1], KEYS[2]);
    }

    @Test
    public void testAddFirst() {
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();
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
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();
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
        SortedIdentityCache<TestKey, TestValue> cache = createSortedIdentityCache();
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
        System.out.println("[" + name + "] TOOK: " + ((double) (end-start) / (double) count) + " millis/cycle");
    }

    private void syncAndVerifyCache(SortedIdentityCache<TestKey, TestValue> cache, TestKey... keys) {
        List<TestKey> keysList = Arrays.asList(keys);
        cache.sync(keysList);
        verifyCache(cache, keysList);
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
