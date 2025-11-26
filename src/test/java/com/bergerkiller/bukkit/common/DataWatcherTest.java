package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.EntityPose;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.generated.net.minecraft.world.entity.DisplayHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityItemFrameHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.EntityMinecartAbstractHandle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.Test;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystalHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.monster.EntityShulkerHandle;

public class DataWatcherTest {

    @Test
    public void testEntityPose() {
        if (!CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            return; // Not available
        }

        DataWatcher dataWatcher = new DataWatcher();
        dataWatcher.set(EntityHandle.DATA_POSE, EntityPose.CROUCHING);
        assertEquals(EntityPose.CROUCHING, dataWatcher.get(EntityHandle.DATA_POSE));
    }

    @Test
    public void testRepeatedClientDefault() {
        DataWatcher dataWatcher = new DataWatcher();

        // Set default, should be set as the initial value.
        // On version 1.19.3+ it should no longer be included in packNonDefaults
        dataWatcher.setClientByteDefault(EntityHandle.DATA_FLAGS, 1);
        assertEquals(1, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.3")) {
            assertEquals(0, dataWatcher.packNonDefaults().size());
        }

        // Update the value to be non-default
        // On version 1.19.3+ it should now be included in packNonDefaults
        dataWatcher.setByte(EntityHandle.DATA_FLAGS, 2);
        assertEquals(2, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.3")) {
            assertEquals(1, dataWatcher.packNonDefaults().size());
        }

        // Set a new default. This should not change the value.
        dataWatcher.setClientByteDefault(EntityHandle.DATA_FLAGS, 8);
        assertEquals(2, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.3")) {
            assertEquals(1, dataWatcher.packNonDefaults().size());
        }

        // Set the default equal to the current value
        // On version 1.19.3+ it should now no longer be included in packNonDefaults, because it's equal
        dataWatcher.setClientByteDefault(EntityHandle.DATA_FLAGS, 2);
        assertEquals(2, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
        if (CommonBootstrap.evaluateMCVersion(">=", "1.19.3")) {
            assertEquals(0, dataWatcher.packNonDefaults().size());
        }
    }

    @Test
    public void testPrototype() {
        // Create a Prototype configuration of flags and no_gravity
        final DataWatcher.Prototype myPrototype = DataWatcher.Prototype.build()
                .setClientByteDefault(EntityHandle.DATA_FLAGS, 0)
                .setFlag(EntityHandle.DATA_FLAGS, EntityHandle.DATA_FLAG_INVISIBLE, true)
                .set(EntityHandle.DATA_NO_GRAVITY, true)
                .create();

        // Create a new instance and verify all is correct inside
        {
            DataWatcher dataWatcher = myPrototype.create();
            assertFalse(dataWatcher.isChanged());
            assertFalse(dataWatcher.isEmpty());
            assertEquals(EntityHandle.DATA_FLAG_INVISIBLE, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
            assertEquals(true, dataWatcher.get(EntityHandle.DATA_NO_GRAVITY));
            assertFalse(dataWatcher.getItem(EntityHandle.DATA_FLAGS).isChanged());
            assertFalse(dataWatcher.getItem(EntityHandle.DATA_NO_GRAVITY).isChanged());

            // Should include both keys as non-defaults, since the flag was changed from the 0 client default
            assertEquals(2, dataWatcher.packNonDefaults().size());
        }

        // Modify the prototype, changing flags to 0 so it is the same as defaults
        final DataWatcher.Prototype myUpdatedPrototype = myPrototype.modify()
                .setByte(EntityHandle.DATA_FLAGS, 0)
                .create();

        // Create a new instance and verify all is correct inside
        {
            DataWatcher dataWatcher = myUpdatedPrototype.create();
            assertFalse(dataWatcher.isChanged());
            assertFalse(dataWatcher.isEmpty());
            assertEquals(0, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
            assertEquals(true, dataWatcher.get(EntityHandle.DATA_NO_GRAVITY));
            assertFalse(dataWatcher.getItem(EntityHandle.DATA_FLAGS).isChanged());
            assertFalse(dataWatcher.getItem(EntityHandle.DATA_NO_GRAVITY).isChanged());

            // Should include only no_gravity as non-defaults, since the flag is 0
            // This only applies on MC 1.19.3 and beyond when this default logic was added
            if (CommonBootstrap.evaluateMCVersion(">=", "1.19.3")) {
                assertEquals(1, dataWatcher.packNonDefaults().size());
            } else {
                assertEquals(2, dataWatcher.packNonDefaults().size());
            }
        }

        // Quick check that the original prototype was not modified
        {
            DataWatcher dataWatcher = myPrototype.create();
            assertFalse(dataWatcher.isChanged());
            assertFalse(dataWatcher.isEmpty());
            assertEquals(EntityHandle.DATA_FLAG_INVISIBLE, dataWatcher.getByte(EntityHandle.DATA_FLAGS));
            assertEquals(true, dataWatcher.get(EntityHandle.DATA_NO_GRAVITY));
            assertFalse(dataWatcher.getItem(EntityHandle.DATA_FLAGS).isChanged());
            assertFalse(dataWatcher.getItem(EntityHandle.DATA_NO_GRAVITY).isChanged());

            // Should include both keys as non-defaults, since the flag was changed from the 0 client default
            assertEquals(2, dataWatcher.packNonDefaults().size());
        }
    }

    @Test
    public void testClone() {
        DataWatcher dataWatcher = new DataWatcher();
        assertTrue(dataWatcher.isEmpty());
        assertFalse(dataWatcher.isChanged());

        dataWatcher.setClientDefault(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("original"));
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("custom"));
        assertFalse(dataWatcher.isEmpty());
        assertTrue(dataWatcher.isChanged());
        assertTrue(dataWatcher.getItem(EntityHandle.DATA_CUSTOM_NAME).isChanged());

        // Verify copy has all states copied, INCLUDING changed states
        DataWatcher copy = dataWatcher.clone();
        assertFalse(copy.isEmpty());
        assertTrue(copy.isChanged());
        assertEquals(ChatText.fromMessage("custom"), copy.get(EntityHandle.DATA_CUSTOM_NAME));
        assertTrue(copy.getItem(EntityHandle.DATA_CUSTOM_NAME).isChanged());

        // Modify original, verify the copy does not change
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("changed_again"));
        dataWatcher.packChanges();

        assertFalse(copy.isEmpty());
        assertTrue(copy.isChanged());
        assertEquals(ChatText.fromMessage("custom"), copy.get(EntityHandle.DATA_CUSTOM_NAME));
        assertTrue(copy.getItem(EntityHandle.DATA_CUSTOM_NAME).isChanged());
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
        dataWatcher.setClientDefault(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("original"));
        assertTrue(dataWatcher.isWatched(EntityHandle.DATA_CUSTOM_NAME));
        assertEquals("original", dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME).getMessage());
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("new"));
        assertEquals("new", dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME).getMessage());

        // Verify that when setting to null, it sets to Optional.empty correctly (MC 1.13)
        if (Common.evaluateMCVersion(">=", "1.13")) {
            // Using converter
            Object raw = EntityHandle.DATA_CUSTOM_NAME.getType().getConverter().convertReverse(null);
            checkCustomNameOptional(raw);

            // Set and get
            dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME, null);
            raw = DataWatcher.Item.getRawValue(dataWatcher.getItem(EntityHandle.DATA_CUSTOM_NAME));
            checkCustomNameOptional(raw);

            // Reset DW and watch using entry from old DW
            DataWatcher dataWatcher_copy = new DataWatcher();
            for (DataWatcher.Item<?> old_item : dataWatcher.getWatchedItems()) {
                //System.out.println("TYPE: " + old_item.getKey().getType());
                //System.out.println("VALUE: " + old_item.getValue());
                //System.out.println("VALUE_FIX: " + old_item.getKey().getType().getConverter().convertReverse(old_item.getValue()));
                dataWatcher_copy.watch(old_item);
            }
            raw = DataWatcher.Item.getRawValue(dataWatcher_copy.getItem(EntityHandle.DATA_CUSTOM_NAME));
            checkCustomNameOptional(raw);
        }

        // Now do a run with an Integer type
        dataWatcher.watch(EntityHandle.DATA_AIR_TICKS, 300);
        assertEquals(300, dataWatcher.get(EntityHandle.DATA_AIR_TICKS).intValue());
        dataWatcher.set(EntityHandle.DATA_AIR_TICKS, 200);
        assertEquals(200, dataWatcher.get(EntityHandle.DATA_AIR_TICKS).intValue());

        // This might fail on 1.8.9! Test Boolean type.
        dataWatcher.watch(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, false);
        assertEquals(false, dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME_VISIBLE).booleanValue());
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, true);
        assertEquals(true, dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME_VISIBLE).booleanValue());
        dataWatcher.set(EntityHandle.DATA_CUSTOM_NAME_VISIBLE, false);
        assertEquals(false, dataWatcher.get(EntityHandle.DATA_CUSTOM_NAME_VISIBLE).booleanValue());
    }

    @Test
    public void testShulkerPeek() {
        if (!EntityShulkerHandle.T.isAvailable()) {
            return;
        }

        DataWatcher dataWatcher = new DataWatcher();

        // DATA_PEEK
        dataWatcher.set(EntityShulkerHandle.DATA_PEEK, (byte) 5);
        assertEquals((byte) 5, dataWatcher.get(EntityShulkerHandle.DATA_PEEK).byteValue());

        // DATA_AP (attached point)
        //dataWatcher.set(EntityShulkerHandle.DATA_AP, new IntVector3(5, 6, 7));
        //assertEquals(new IntVector3(5, 6, 7), dataWatcher.get(EntityShulkerHandle.DATA_AP));
    }

    @Test
    public void testEnderCrystalBeamTarget() {
        if (!EntityEnderCrystalHandle.T.isAvailable()) {
            return;
        }

        DataWatcher dataWatcher = new DataWatcher();

        // DATA_BEAM_TARGET
        dataWatcher.set(EntityEnderCrystalHandle.DATA_BEAM_TARGET, new IntVector3(5, 6, 7));
        assertEquals(new IntVector3(5, 6, 7), dataWatcher.get(EntityEnderCrystalHandle.DATA_BEAM_TARGET));
    }
    
    private static void checkCustomNameOptional(Object raw) {
        if (raw == null) {
            System.err.println(EntityHandle.DATA_CUSTOM_NAME);
            fail("Internal stored type for DATA_CUSTOM_NAME is null instead of Optional.empty when setting to null");
        }
        if (!CommonNMS.isDWROptionalType(raw.getClass())) {
            fail("Internal stored type for DATA_CUSTOM_NAME is not Optional: " + raw);
        }
    }

    @Test
    public void testChanges() {
        DataWatcher dataWatcher = new DataWatcher();
        assertFalse(dataWatcher.isChanged());

        dataWatcher.setClientDefault(EntityHandle.DATA_AIR_TICKS, 500);
        assertFalse(dataWatcher.isChanged());

        dataWatcher.set(EntityHandle.DATA_AIR_TICKS, 500);
        assertFalse(dataWatcher.isChanged());

        dataWatcher.set(EntityHandle.DATA_AIR_TICKS, 200);
        assertTrue(dataWatcher.isChanged());

        dataWatcher.setClientDefault(EntityHandle.DATA_CUSTOM_NAME, ChatText.fromMessage("frank"));

        // debug
        //for (DataWatcher.Item<?> item : dataWatcher.getWatchedItems()) {
        //    System.out.println(item.toString());
        //}
    }

    @Test
    public void testForceSet() {
        DataWatcher dataWatcher = new DataWatcher();

        // Start watching, but because we used forceSet, changed is true!
        dataWatcher.forceSet(EntityHandle.DATA_AIR_TICKS, 500);
        assertTrue(dataWatcher.isChanged());
        assertTrue(dataWatcher.getItem(EntityHandle.DATA_AIR_TICKS).isChanged());

        // Pack changes, which should reset changed back to false
        dataWatcher.packChanges();
        assertFalse(dataWatcher.isChanged());
        assertFalse(dataWatcher.getItem(EntityHandle.DATA_AIR_TICKS).isChanged());

        // Set to the same value again, but because it's forced, it should mark as changed
        dataWatcher.forceSet(EntityHandle.DATA_AIR_TICKS, 500);
        assertTrue(dataWatcher.isChanged());
        assertTrue(dataWatcher.getItem(EntityHandle.DATA_AIR_TICKS).isChanged());
    }

    @Test
    public void testItemItemStack() {
        Material itemType = MaterialUtil.getFirst("STONE", "LEGACY_STONE");
        DataWatcher metadata = new DataWatcher();
        metadata.set(EntityItemHandle.DATA_ITEM, new ItemStack(itemType, 1));
        ItemStack stored = metadata.get(EntityItemHandle.DATA_ITEM);
        assertNotNull(stored);
        assertEquals(itemType, stored.getType());
    }

    @Test
    public void testItemFrameMeta() {
        CommonItemStack item = CommonItemStack.create(MaterialUtil.getFirst("OAK_LOG", "LEGACY_LOG"), 2)
                        .updateCustomData(metadata -> {
                            metadata.putValue("UniqueKey", "UniqueValue123");
                        });

        DataWatcher metadata = new DataWatcher();
        metadata.set(EntityItemFrameHandle.DATA_ITEM, item.toBukkit());

        CommonItemStack read = CommonItemStack.of(metadata.get(EntityItemFrameHandle.DATA_ITEM));
        assertEquals(item, read);
        assertEquals(item.toBukkit(), read.toBukkit());
        assertEquals("UniqueValue123", read.getCustomData().getValue("UniqueKey"));
    }

    @Test
    public void testDisplayEntityMeta() {
        if (!CommonBootstrap.evaluateMCVersion(">=", "1.19.4")) {
            return;
        }

        Quaternion leftRotation = new Quaternion(1.0, 0.5, 0.3, 0.6);
        Quaternion rightRotation = new Quaternion(0.9, 0.2, -0.3, 0.3);
        Vector scale = new Vector(1.0, 2.0, 3.0);
        Vector translation = new Vector(0.0, 50.0, 70.0);

        DataWatcher metadata = new DataWatcher();
        metadata.set(DisplayHandle.DATA_LEFT_ROTATION, leftRotation);
        metadata.set(DisplayHandle.DATA_RIGHT_ROTATION, rightRotation);
        metadata.set(DisplayHandle.DATA_SCALE, scale);
        metadata.set(DisplayHandle.DATA_TRANSLATION, translation);

        // Test we read the same. Has floating point precision so it won't be exactly equal.
        MathUtilTest.testQuaternionsEqual(leftRotation, metadata.get(DisplayHandle.DATA_LEFT_ROTATION), 0.01);
        MathUtilTest.testQuaternionsEqual(rightRotation, metadata.get(DisplayHandle.DATA_RIGHT_ROTATION), 0.01);
        MathUtilTest.testVectorsEqual(scale, metadata.get(DisplayHandle.DATA_SCALE), 0.001);
        MathUtilTest.testVectorsEqual(translation, metadata.get(DisplayHandle.DATA_TRANSLATION), 0.001);
    }

    @Test
    public void testTextDisplayEntityMeta() {
        if (!CommonBootstrap.evaluateMCVersion(">=", "1.19.4")) {
            return;
        }

        ChatText text = ChatText.fromMessage(ChatColor.GREEN + "Hello, " + ChatColor.RED + " world!");
        DataWatcher metadata = new DataWatcher();
        metadata.set(DisplayHandle.TextDisplayHandle.DATA_TEXT, text);
        assertEquals(text, metadata.get(DisplayHandle.TextDisplayHandle.DATA_TEXT));
    }

    @Test
    public void testBlockDisplayEntityMeta() {
        if (!CommonBootstrap.evaluateMCVersion(">=", "1.19.4")) {
            return;
        }

        BlockData blockData = BlockData.fromMaterial(MaterialUtil.getMaterial("ACACIA_LOG"));
        DataWatcher metadata = new DataWatcher();
        metadata.set(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE, blockData);
        assertEquals(blockData, metadata.get(DisplayHandle.BlockDisplayHandle.DATA_BLOCK_STATE));
    }

    @Test
    public void testItemDisplayEntityMeta() {
        if (!CommonBootstrap.evaluateMCVersion(">=", "1.19.4")) {
            return;
        }

        CommonBootstrap.initServer();

        CommonItemStack item = CommonItemStack.create(MaterialUtil.getMaterial("ACACIA_LOG"), 1)
                        .updateCustomData(metadata -> {
                            metadata.putValue("UniqueKey", "UniqueValue123");
                        });

        DataWatcher metadata = new DataWatcher();
        {
            metadata.set(DisplayHandle.ItemDisplayHandle.DATA_ITEM_STACK, item.toBukkit());
            CommonItemStack read = CommonItemStack.of(metadata.get(DisplayHandle.ItemDisplayHandle.DATA_ITEM_STACK));
            assertEquals(item, read);
            assertEquals(item.toBukkit(), read.toBukkit());
            assertEquals("UniqueValue123", read.getCustomData().getValue("UniqueKey"));
        }

        for (ItemDisplayMode mode : ItemDisplayMode.values()) {
            assertEquals(mode, ItemDisplayMode.byId(ItemDisplayMode.getId(mode)));

            metadata.set(DisplayHandle.ItemDisplayHandle.DATA_ITEM_DISPLAY_MODE, mode);
            assertEquals(mode, metadata.get(DisplayHandle.ItemDisplayHandle.DATA_ITEM_DISPLAY_MODE));
        }
    }

    @Test
    public void testMinecartDisplayedBlock() {
        DataWatcher metadata = new DataWatcher();

        BlockData testData = BlockData.fromMaterial(MaterialUtil.getFirst("DIAMOND_BLOCK", "LEGACY_DIAMOND_BLOCK"));

        if (CommonCapabilities.IS_MINECART_BLOCK_COMBINED_KEY) {
            metadata.set(EntityMinecartAbstractHandle.DATA_CUSTOM_DISPLAY_BLOCK, testData);
            assertEquals(testData, metadata.get(EntityMinecartAbstractHandle.DATA_CUSTOM_DISPLAY_BLOCK));
        } else {
            metadata.set(EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE, true);
            assertEquals(true, metadata.get(EntityMinecartAbstractHandle.DATA_BLOCK_VISIBLE));

            metadata.set(EntityMinecartAbstractHandle.DATA_BLOCK_TYPE, testData.getCombinedId());
            assertEquals(testData, BlockData.fromCombinedId(metadata.get(EntityMinecartAbstractHandle.DATA_BLOCK_TYPE)));
        }
    }
}
