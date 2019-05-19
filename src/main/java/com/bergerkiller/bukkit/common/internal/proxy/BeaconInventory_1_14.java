package com.bergerkiller.bukkit.common.internal.proxy;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.inventory.InventoryBase;

/**
 * Since MC 1.14 the itemstack set in a beacon is no longer an actual inventory slot.
 * As a result it simply does not exist without an open window. Any attempt to set
 * this item fails, so to return something of use, we return this proxy instead.
 * It is just a 1x1 inventory.
 * 
 * TODO: Make it actually set items in the beacon
 */
public class BeaconInventory_1_14 extends InventoryBase implements org.bukkit.inventory.BeaconInventory {
    private final Object nmsTileEntityBeacon;
    private ItemStack item = null;

    public BeaconInventory_1_14(Object nmsTileEntityBeacon) {
        this.nmsTileEntityBeacon = nmsTileEntityBeacon;
        this.setMaxStackSize(0);
    }

    @Override
    public ItemStack getItem() {
        return this.getItem(0);
    }

    @Override
    public void setItem(ItemStack arg0) {
        this.setItem(0, arg0);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.item;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        this.item = item;
    }
}
