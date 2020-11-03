package com.bergerkiller.bukkit.common.map.util;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * For {@link MapDisplayProperties#of(ItemStack)}
 */
public class ItemStackMapDisplayProperties extends MapDisplayProperties {
    private final ItemStack item;
    private final CommonTagCompound metadata;

    public ItemStackMapDisplayProperties(ItemStack item, CommonTagCompound metadata) {
        this.item = item;
        this.metadata = metadata;
    }

    @Override
    public ItemStack getMapItem() {
        return this.item;
    }

    @Override
    public CommonTagCompound getMetadata() {
        return this.metadata;
    }
}
