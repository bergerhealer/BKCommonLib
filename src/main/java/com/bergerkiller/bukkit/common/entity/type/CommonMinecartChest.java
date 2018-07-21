package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;

import java.util.Arrays;
import java.util.List;

/**
 * A Common Entity implementation for Minecarts with a Chest
 */
public class CommonMinecartChest extends CommonMinecartInventory<StorageMinecart> {
    private static final Material _COMBINED_ITEM = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            Material.getMaterial("CHEST_MINECART") : Material.getMaterial("STORAGE_MINECART");

    public CommonMinecartChest(StorageMinecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.CHEST, 1));
    }

    @Override
    public Material getCombinedItem() {
        return _COMBINED_ITEM;
    }
}
