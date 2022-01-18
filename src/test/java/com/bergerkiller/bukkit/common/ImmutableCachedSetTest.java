package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.ImmutableCachedSet;

/**
 * Tests the many immutable operations of the ImmutableHashSet. ImmutablePlayerSet uses this same
 * implementation, so as such, it is testing that one too.
 */
public class ImmutableCachedSetTest {
    private final ImmutableCachedSet<String> EMPTY = ImmutableCachedSet.createNew();

    @Test
    public void testAddCached() {
        // Add it, we expect it to work and that EMPTY is unchanged
        ImmutableCachedSet<String> added1 = EMPTY.add("value001");
        assertEquals(1, added1.size());
        assertFalse(added1.isEmpty());
        assertTrue(added1.contains("value001"));
        assertNotEquals(EMPTY, added1);
        // EMPTY should not be modified by this!
        assertEquals(0, EMPTY.size());
        assertTrue(EMPTY.isEmpty());

        // Add it again, it should be the same instance as added1 (cached)
        ImmutableCachedSet<String> added2 = EMPTY.add("value001");
        assertTrue(added1 == added2);

        // Add value to existing set, same value should be returned here as well
        ImmutableCachedSet<String> added3 = added1.add("value001");
        assertTrue(added1 == added3);
    }

    @Test
    public void testRemoveCached() {
        // Start out with a set with a single value
        ImmutableCachedSet<String> start = EMPTY.add("value002");

        // Remove a different value from this set, the same set should be returned
        assertTrue(start == start.remove("value002b"));

        // Remove the value from this set, we expect EMPTY to be returned
        // Make sure start and EMPTY were not modified as a result of this
        assertTrue(EMPTY == start.remove("value002"));
        assertEquals(1, start.size());
        assertFalse(start.isEmpty());
        assertEquals(0, EMPTY.size());
        assertTrue(EMPTY.isEmpty());

        // Remove value from EMPTY, EMPTY should be returned
        assertTrue(EMPTY == EMPTY.remove("value002c"));
    }

    @Test
    public void testAddMultipleCached() {
        // Create a chain of immutable hash sets, each with one value more than the previous
        List<ImmutableCachedSet<String>> values = new ArrayList<>();
        List<Set<String>> expectedValues = new ArrayList<>();
        values.add(EMPTY);
        expectedValues.add(Collections.emptySet());
        for (String value : new String[] { "value003a", "value003b", "value003c", "value003d" }) {
            HashSet<String> exp = new HashSet<String>(expectedValues.get(expectedValues.size()-1));
            exp.add(value);
            expectedValues.add(exp);
            values.add(values.get(values.size()-1).add(value));
        }

        // Verify each intermediate stage has valid results
        for (int i = 0; i < expectedValues.size(); i++) {
            ImmutableCachedSet<String> immutable = values.get(i);
            Set<String> expected = expectedValues.get(i);

            assertEquals(expected.size(), immutable.size());
            assertEquals(expected, immutable.stream().collect(Collectors.toSet()));
        }
    }

    @Test
    public void testReleaseCacheAdd() {
        ImmutableCachedSet<String> value1 = EMPTY.add("value004a");
        ImmutableCachedSet<String> value2 = value1.add("value004b");
        EMPTY.releaseFromCache("value004a");
        assertTrue(value1 != EMPTY.add("value004a"));
        assertTrue(value2 != value1.add("value004b"));
    }

    @Test
    public void testReleaseCacheRemove() {
        ImmutableCachedSet<String> base = EMPTY.add("value004c").add("value004d").add("value004e");
        ImmutableCachedSet<String> value1 = base.add("value004f");
        ImmutableCachedSet<String> value2 = value1.remove("value004d");
        EMPTY.releaseFromCache("value004f");
        assertTrue(value2 != value1.remove("value004d"));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testMultithreadedCacheAccess() {
        final String[] inputs = IntStream.range(0, 50).mapToObj(i -> "value005_" + i).toArray(String[]::new);

        Runnable worker = () -> {
            Random rand = new Random();
            ImmutableCachedSet<String> current = EMPTY;
            for (long l = 0; l < 1000L; l++) {
                String input = inputs[rand.nextInt(inputs.length)];
                if (rand.nextBoolean()) {
                    current = current.add(input);
                    assertTrue(current.contains(input));
                } else {
                    current = current.remove(input);
                    assertFalse(current.contains(input));
                }
            }
        };

        final int numThreads = 8;
        final Executor executor = Executors.newFixedThreadPool(numThreads);

        CompletableFuture[] threads = IntStream.range(0, numThreads).mapToObj(unused -> {
            return CompletableFuture.runAsync(worker, executor);
        }).toArray(CompletableFuture[]::new);

        try {
            CompletableFuture.allOf(threads).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new IllegalStateException("Worker had an error", e);
        }
    }

    @Test
    @Ignore
    public void benchmarkMassAdding() {
        ImmutableCachedSet<String> base = EMPTY.add("value006a");

        ImmutableCachedSet<String> tmp = null;
        for (long n = 0; n < 100000000L; n++) {
            tmp = base.add("value006b");
        }
        tmp.toString();
    }

    @Test
    @Ignore
    public void benchmarkMassRemoving() {
        ImmutableCachedSet<String> base = EMPTY.add("value007a").add("value007b");

        ImmutableCachedSet<String> tmp = null;
        for (long n = 0; n < 100000000L; n++) {
            tmp = base.remove("value007b");
        }
        tmp.toString();
    }
}
