package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public class DataWatcherTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testConstruction() {
        DataWatcher dataWatcher = new DataWatcher();
        assertTrue(dataWatcher.isEmpty());
    }

    @Test
    public void testBasicOperation() {
        DataWatcher dataWatcher = new DataWatcher();

        // Do a single test run with a String type, which is quite safe
        assertFalse(dataWatcher.isWatched(EntityHandle.DATA_CUSTOM_NAME));
        dataWatcher.watch(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("original"));
        assertTrue(dataWatcher.isWatched(EntityHandle.DATA_CUSTOM_NAME));
        assertEquals("original", dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME).getMessage());
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("new"));
        assertEquals("new", dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME).getMessage());

        // Now do a run with an Integer type
        dataWatcher.watch(EntityHandle.DATA_AIR_TICKS, 300);
        assertEquals(300, dataWatcher.get(EntityHandle.DATA_AIR_TICKS).intValue());
        dataWatcher.set(EntityHandle.DATA_AIR_TICKS, 200);
        assertEquals(200, dataWatcher.get(EntityHandle.DATA_AIR_TICKS).intValue());

        // This might fail on 1.8.8! Test Boolean type.
        dataWatcher.watch(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, false);
        assertEquals(false, dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME_VISIBLE).booleanValue());
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, true);
        assertEquals(true, dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME_VISIBLE).booleanValue());
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, false);
        assertEquals(false, dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME_VISIBLE).booleanValue());
    }

    @Test
    public void testChanges() {
        DataWatcher dataWatcher = new DataWatcher();
        assertFalse(dataWatcher.isChanged());

        dataWatcher.watch(EntityHandle.DATA_AIR_TICKS, 500);
        assertFalse(dataWatcher.isChanged());

        dataWatcher.set(EntityHandle.DATA_AIR_TICKS, 500);
        assertFalse(dataWatcher.isChanged());

        dataWatcher.set(EntityHandle.DATA_AIR_TICKS, 200);
        assertTrue(dataWatcher.isChanged());

        dataWatcher.watch(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("frank"));

        // debug
        //for (DataWatcher.Item<?> item : dataWatcher.getWatchedItems()) {
        //    System.out.println(item.toString());
        //}
    }
}
