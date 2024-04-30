package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * For {@link MapDisplayProperties#of(ItemStack)}
 */
public class ItemStackMapDisplayProperties extends MapDisplayProperties {
    private final CommonItemStack item;

    public ItemStackMapDisplayProperties(CommonItemStack item) {
        this.item = item;
    }

    @Override
    public CommonItemStack getCommonMapItem() {
        return this.item;
    }
}
