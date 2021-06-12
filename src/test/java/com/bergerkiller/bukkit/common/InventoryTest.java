package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.IInventoryHandle;

/**
 * Tests whether the base implementation for Bukkit inventories works correctly
 */
public class InventoryTest {

    @Test
    public void testEmptyItemConstant() {
        if (CommonCapabilities.ITEMSTACK_EMPTY_STATE) {
            assertNotNull(ItemStackHandle.EMPTY_ITEM.getRaw());
        }
    }

    @Test
    public void testItemStackConversion() {
        ItemStack item = new ItemStack(Material.GRASS, 1);
        Object nmsHandle = HandleConversion.toItemStackHandle(item);
        assertNotNull(nmsHandle);
        ItemStack itemBackConv = WrapperConversion.toItemStack(nmsHandle);
        assertItemEquals(item, itemBackConv);
    }

    @Test
    public void testBaseInventory() {
        InventoryBaseImpl inventory = new InventoryBaseImpl(9 * 3);
        assertEquals(9 * 3, inventory.getSize());
        for (int i = 0; i < inventory.getSize(); i++) {
            assertNull(inventory.getItem(i));
        }

        // Verify that the IInventory handle returns non-null items (uses EMPTY constant)
        IInventoryHandle handle = IInventoryHandle.createHandle(HandleConversion.toIInventoryHandle(inventory));
        assertEquals(9 * 3, handle.getSize());
        for (int i = 0; i < handle.getSize(); i++) {
            assertTrue(ItemStackHandle.EMPTY_ITEM.equals(handle.getItem(i)));
        }
        List<ItemStackHandle> handleContents = handle.getContents();
        assertEquals(9 * 3, handleContents.size());
        for (int i = 0; i < handleContents.size(); i++) {
            assertTrue(ItemStackHandle.EMPTY_ITEM.equals(handle.getItem(i)));
        }

        ItemStack testItem1 = new ItemStack(Material.GRASS, 1);
        inventory.setItem(2, testItem1);
        assertItemEquals(testItem1, inventory.getItem(2));

        ItemStack testItem2 = new ItemStack(Material.STONE, 16);
        int cnt = 0;
        for (int n = 0; n < 4; n++) {
            cnt += 16;
            inventory.addItem(testItem2);
            assertItemEquals(new ItemStack(Material.STONE, cnt), inventory.getItem(0));
        }
        inventory.addItem(testItem2);
        assertItemEquals(new ItemStack(Material.STONE, 64), inventory.getItem(0));
        assertItemEquals(new ItemStack(Material.STONE, 16), inventory.getItem(1));
    }

    private void assertItemEquals(ItemStack expected, ItemStack actual) {
        if (expected == actual) {
            return;
        }
        if (expected == null) {
            fail("Item is not null (expected null, actual=" + actual.toString() + ")");
        }
        if (actual == null) {
            fail("Item is null (expected=" + expected.toString() + ", actual null)");
        }
        if (expected.getType() != actual.getType()) {
            fail("Item type mismatch (expected.type=" + expected.getType() + ", actual.type=" + actual.getType() + ")");
        }
        if (expected.getAmount() != actual.getAmount()) {
            fail("Item amount mismatch (expected.amount=" + expected.getAmount() + ", actual.amount=" + actual.getAmount() + ")");
        }
    }
}
