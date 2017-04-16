package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil.ItemSynchronizer;

public class LogicUtilTest {

    private void testSynchronizer(List<Integer> sync, Collection<Integer> values) {
        ItemSynchronizer<Integer, Integer> synchronizer = new ItemSynchronizer<Integer, Integer>() {
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

        LogicUtil.synchronizeList(sync, values, synchronizer);

        assertEquals(sync, values);
    }

    @Test
    public void testSynchronizedList() {
        List<Integer> sync = new ArrayList<Integer>();
        testSynchronizer(sync, Arrays.asList(1, 2, 3, 4, 5));
        testSynchronizer(sync, Arrays.asList(1, 2, 3, 4, 5, 6));
        testSynchronizer(sync, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        testSynchronizer(sync, Arrays.asList(1, 2, 3, 5, 6, 7, 8));
        testSynchronizer(sync, Arrays.asList(1, 2, 7, 8));
        testSynchronizer(sync, Arrays.asList(1, 2, 4, 7, 8));
        testSynchronizer(sync, Arrays.asList(1, 2, 5, 7, 8));
        testSynchronizer(sync, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        testSynchronizer(sync, Collections.<Integer>emptyList());
    }
}
