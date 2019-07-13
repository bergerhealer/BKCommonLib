package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil.ItemSynchronizer;

public class LogicUtilTest {
    private static enum TestMode {
        ORDERED_LIST, UNORDERED_SET
    }

    private static final List<Collection<Integer>> demo_sync_lists = Arrays.asList(
            Arrays.asList(1, 2, 3, 4, 5),
            Arrays.asList(1, 2, 3, 4, 5, 6),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8),
            Arrays.asList(1, 2, 3, 5, 6, 7, 8),
            Arrays.asList(1, 2, 7, 8),
            Arrays.asList(1, 2, 4, 7, 8),
            Arrays.asList(1, 2, 5, 7, 8),
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8),
            Collections.<Integer>emptyList()
    );
    private static final List<Collection<Integer>> demo_sync_sets;
    static {
        demo_sync_sets = new ArrayList<Collection<Integer>>(demo_sync_lists.size());
        for (Collection<Integer> coll : demo_sync_lists) {
            demo_sync_sets.add(new HashSet<Integer>(coll));
        }
    }
    private static final ItemSynchronizer<Integer, Integer> synchronizer = new ItemSynchronizer<Integer, Integer>() {
        @Override
        public boolean isItem(Integer item, Integer value) {
            return item.equals(value);
        }

        @Override
        public Integer onAdded(Integer value) {
            //System.out.println("Add: " + value);
            return value;
        }

        @Override
        public void onRemoved(Integer item) {
            //System.out.println("Removed: " + item);
        }
    };

    private void testListSynchronizer(List<Integer> sync, TestMode mode, boolean validate, Collection<Integer> values) {
        LogicUtil.synchronizeList(sync, values, synchronizer);
        if (validate) {
            assertEquals(sync, values);
        }
    }

    private void testSetSynchronizer(Set<Integer> sync, TestMode mode, boolean validate, Collection<Integer> values) {
        LogicUtil.synchronizeUnordered(sync, values, synchronizer);

        if (validate) {
            assertTrue(sync.containsAll(values));
            assertTrue(values.containsAll(sync));
        }
    }

    // Performs test, returns amount of microseconds per test cycle
    private double runTest(TestMode mode, int count) {
        long time_a, time_b;
        if (mode == TestMode.ORDERED_LIST) {
            List<Integer> sync = new ArrayList<Integer>();

            time_a = System.nanoTime();
            for (int i = 0; i < count; i++) {
                for (Collection<Integer> compare : demo_sync_lists) {
                    testListSynchronizer(sync, mode, i==0, compare);
                }
            }
            time_b = System.nanoTime();
        } else {
            Set<Integer> sync = new HashSet<Integer>();

            time_a = System.nanoTime();
            for (int i = 0; i < count; i++) {
                for (Collection<Integer> compare : demo_sync_sets) {
                    testSetSynchronizer(sync, mode, i==0, compare);
                }
            }
            time_b = System.nanoTime();
        }

        return (double) ((time_b - time_a)) / (double) (count*1000);
    }

    // Performs test with only the first synchronized list, returns amount of microseconds per test cycle
    // This tests the performance when the list and sync collections are the same
    private double runTestUnchanging(TestMode mode, int count) {
        long time_a, time_b;
        if (mode == TestMode.ORDERED_LIST) {
            List<Integer> sync = new ArrayList<Integer>();
            Collection<Integer> compare = demo_sync_lists.get(0);

            time_a = System.nanoTime();
            for (int i = 0; i < count; i++) {
                testListSynchronizer(sync, mode, i==0, compare);
            }
            time_b = System.nanoTime();
        } else {
            Set<Integer> sync = new HashSet<Integer>();
            Collection<Integer> compare = demo_sync_sets.get(0);

            time_a = System.nanoTime();
            for (int i = 0; i < count; i++) {
                testSetSynchronizer(sync, mode, i==0, compare);
            }
            time_b = System.nanoTime();
        }

        return (double) ((time_b - time_a)) / (double) (count*1000);
    }

    @Test
    public void testSynchronizedList() {
        runTest(TestMode.ORDERED_LIST, 1);
    }

    @Test
    public void testSynchronizedUnorderedSet() {
        runTest(TestMode.UNORDERED_SET, 1);
    }

    @Test
    @Ignore
    public void testTimings() {
        for (TestMode mode : TestMode.values()) {
            String name = mode.toString();
            while (name.length() < 22) {
                name += " ";
            }

            double time_per_cycle = runTest(mode, 1000000);
            double time_per_unchanged_cycle = runTestUnchanging(mode, 1000000);

            String col1 = "changed=" + String.format("%.4f", time_per_cycle) + " us";
            while (col1.length() < 20) {
                col1 += " ";
            }

            String col2 = "unchanged=" + String.format("%.4f", time_per_unchanged_cycle) + " us";
            while (col2.length() < 20) {
                col2 += " ";
            }
            
            System.out.println("Sync " + name + " " + col1 + " | " + col2);
        }
    }

    @Test
    public void testClone() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("world");
        ArrayList<String> list_clone = LogicUtil.clone(list);
        assertEquals(list, list_clone);
    }
}
