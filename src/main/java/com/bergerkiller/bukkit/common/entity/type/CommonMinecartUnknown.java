package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Used for Minecart types that we do not support yet
 */
public class CommonMinecartUnknown extends CommonMinecart<Minecart> {

    public CommonMinecartUnknown(Minecart base) {
        super(base);
    }

    @Override
    public List<ItemStack> getBrokenDrops() {
        return Arrays.asList(new ItemStack(Material.MINECART, 1));
    }

    @Override
    public Material getCombinedItem() {
        return Material.MINECART;
    }
}
