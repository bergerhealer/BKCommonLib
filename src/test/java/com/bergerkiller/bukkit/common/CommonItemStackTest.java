package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.inventory.CommonItemMaterials;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getFirst;
import static org.junit.Assert.*;

public class CommonItemStackTest {

    @Test
    public void testMimicAsType() {
        if (!CommonItemStack.canSetItemModel()) {
            return;
        }

        CommonItemStack item = CommonItemStack.create(MaterialUtil.getFirst("DIAMOND_SWORD", "LEGACY_DIAMOND_SWORD"), 1);
        System.out.println(item.getItemModel());
        assertFalse(item.hasItemModel());

        item.mimicAsType(CommonItemMaterials.STICK); // Hits like a wet noodle

        assertEquals(CommonItemMaterials.STICK, item.getType());
        assertEquals("minecraft:diamond_sword", item.getItemModel().toString());
    }

    @Test
    public void testVanillaDisplayName() {
        // Note: display name is a translatable, which we decode into English.
        CommonItemStack item = CommonItemStack.create(MaterialUtil.getFirst("DIAMOND_SWORD", "LEGACY_DIAMOND_SWORD"), 1);
        assertEquals("Diamond sword", item.getDisplayNameMessage());
    }

    @Test
    public void testCustomModelDataLegacy() {
        CommonItemStack item = CommonItemStack.create(MaterialUtil.getFirst("DIAMOND_SWORD", "LEGACY_DIAMOND_SWORD"), 1);
        assertFalse(item.hasCustomModelData());

        // Test legacy API
        item.setCustomModelData(5);
        assertTrue(item.hasCustomModelData());
        assertEquals(5, item.getCustomModelData());
        assertEquals(Collections.singletonList((float) 5), item.getCustomModelDataComponents().floats());

        // Modify using components API, should be seen by legacy API
        item.setCustomModelDataComponents(new CustomModelData().withFloats(Collections.singletonList(20.0f)));
        assertEquals(20, item.getCustomModelData());
        assertEquals(Collections.singletonList((float) 20), item.getCustomModelDataComponents().floats());

        // Verify clearing works
        item.clearCustomModelData();
        assertFalse(item.hasCustomModelData());
        assertEquals(-1, item.getCustomModelData());
    }

    @Test
    public void testCustomModelDataComponents() {
        if (!Common.evaluateMCVersion(">=", "1.21.4")) {
            return; // Not supported, other component fields do nothing
        }

        CommonItemStack item = CommonItemStack.create(MaterialUtil.getFirst("DIAMOND_SWORD", "LEGACY_DIAMOND_SWORD"), 1);
        assertFalse(item.hasCustomModelData());

        // Test modern components API
        CustomModelData cmd = new CustomModelData();

        { /* Floats */
            cmd = cmd.withFloats(Arrays.asList(2.0f, 5.0f, 7.0f));
            item.setCustomModelDataComponents(cmd);
            assertTrue(item.hasCustomModelData());
            assertEquals(Arrays.asList(2.0f, 5.0f, 7.0f), item.getCustomModelDataComponents().floats());
        }

        { /* Flags */
            cmd = cmd.withFlags(Arrays.asList(true, true, false));
            item.setCustomModelDataComponents(cmd);
            assertTrue(item.hasCustomModelData());
            assertEquals(Arrays.asList(true, true, false), item.getCustomModelDataComponents().flags());
        }

        { /* Strings */
            cmd = cmd.withStrings(Arrays.asList("hello", "world"));
            item.setCustomModelDataComponents(cmd);
            assertTrue(item.hasCustomModelData());
            assertEquals(Arrays.asList("hello", "world"), item.getCustomModelDataComponents().strings());
        }

        { /* Colors */
            cmd = cmd.withColors(Arrays.asList(Color.RED.asRGB(), Color.GREEN.asRGB()));
            item.setCustomModelDataComponents(cmd);
            assertTrue(item.hasCustomModelData());
            assertEquals(Arrays.asList(Color.RED.asRGB(), Color.GREEN.asRGB()), item.getCustomModelDataComponents().colors());
        }

        // Sanity check that setting one has not erased the other fields
        {
            assertEquals(Arrays.asList(2.0f, 5.0f, 7.0f), item.getCustomModelDataComponents().floats());
            assertEquals(Arrays.asList(true, true, false), item.getCustomModelDataComponents().flags());
            assertEquals(Arrays.asList("hello", "world"), item.getCustomModelDataComponents().strings());
            assertEquals(Arrays.asList(Color.RED.asRGB(), Color.GREEN.asRGB()), item.getCustomModelDataComponents().colors());
        }

        // Verify clearing works
        item.clearCustomModelData();
        assertFalse(item.hasCustomModelData());
        assertEquals(new CustomModelData(), item.getCustomModelDataComponents());
    }

    @Test
    public void testTransferToExistingItem() {
        final Material coal = MaterialUtil.getFirst("COAL", "LEGACY_COAL");
        CommonItemStack emptyItem = CommonItemStack.create(coal, 3);
        Inventory from = new InventoryBaseImpl(5);
        from.setItem(0, CommonItemStack.create(coal, 5).toBukkit());
        int amount = CommonItemStack.transfer(from, emptyItem, item -> item.getType() == coal, 10000);
        assertEquals(5, amount);
        assertEquals(coal, emptyItem.getType());
        assertEquals(8, emptyItem.getAmount());
        assertTrue(CommonItemStack.of(from.getItem(0)).isEmpty());
    }

    @Test
    public void testTransferToEmptyItem() {
        final Material coal = MaterialUtil.getFirst("COAL", "LEGACY_COAL");
        CommonItemStack emptyItem = CommonItemStack.empty();
        Inventory from = new InventoryBaseImpl(5);
        from.setItem(0, CommonItemStack.create(coal, 5).toBukkit());
        int amount = CommonItemStack.transfer(from, emptyItem, item -> item.getType() == coal, 10000);
        assertEquals(5, amount);
        assertEquals(coal, emptyItem.getType());
        assertEquals(5, emptyItem.getAmount());
        assertTrue(CommonItemStack.of(from.getItem(0)).isEmpty());
    }

    @Test
    public void testDisplayName() {
        CommonItemStack item = CommonItemStack.create(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 1);
        assertEquals("Oak planks", item.getDisplayNameMessage());

        item.setCustomNameMessage("Fake planks");
        assertEquals("Fake planks", item.getDisplayNameMessage());

        item.setCustomNameMessage(null);
        assertEquals("Oak planks", item.getDisplayNameMessage());
    }

    @Test
    public void testCustomName() {
        CommonItemStack item = CommonItemStack.create(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 1);
        String old_name = item.getCustomNameMessage();
        item.setCustomNameMessage("COOLNAME");
        assertEquals("COOLNAME", item.getCustomNameMessage());
        item.setCustomNameMessage(null);
        assertEquals(old_name, item.getCustomNameMessage());
    }

    @Test
    public void testVerifyItemFlagsAllHide() {
        // This is important for functioning of CommonItemStack.hideAllAttributes()
        for (ItemFlag flag : ItemFlag.values()) {
            assertTrue(flag.name().startsWith("HIDE_"));
        }
    }

    @Test
    public void testItemCustomData() {
        CommonItemStack item = CommonItemStack.create(getFirst("OAK_PLANKS", "LEGACY_WOOD"), 1);
        assertFalse(item.hasCustomData());
        assertTrue(item.getCustomData().isEmpty());
        assertTrue(item.getCustomDataCopy().isEmpty());

        item.updateCustomData(metadata -> {
            metadata.putValue("test", "awesome!");
        });

        assertTrue(item.hasCustomData());
        assertFalse(item.getCustomData().isEmpty());
        assertEquals("awesome!", item.getCustomData().getValue("test"));
        assertEquals("awesome!", item.getCustomDataCopy().getValue("test"));

        CommonTagCompound meta = item.getCustomDataCopy();
        meta.putValue("otherTest", "cool!");
        item.setCustomData(meta);

        // Verify this is updated too
        assertEquals("cool!", item.getCustomData().getValue("otherTest"));
        assertEquals("cool!", item.getCustomDataCopy().getValue("otherTest"));
        assertEquals("awesome!", item.getCustomData().getValue("test"));
        assertEquals("awesome!", item.getCustomDataCopy().getValue("test"));
    }

    @Test
    public void testEqualsIgnoreAmount() {
        CommonItemStack item = CommonItemStack.create(Material.IRON_DOOR, 1);
        CommonItemStack copy = item.clone();
        assertEquals(item, copy);
        item.setAmount(1);
        copy.setAmount(2);
        assertNotEquals(item, copy);
        assertTrue(item.equalsIgnoreAmount(copy));
    }
}
