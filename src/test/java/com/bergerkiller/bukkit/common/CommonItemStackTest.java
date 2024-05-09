package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.junit.Test;

import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getFirst;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CommonItemStackTest {

    @Test
    public void testDisplayName() {
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
