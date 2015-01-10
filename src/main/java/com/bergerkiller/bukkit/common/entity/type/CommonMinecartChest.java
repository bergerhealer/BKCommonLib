package com.bergerkiller.bukkit.common.entity.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;

/**
 * A Common Entity implementation for Minecarts with a Chest
 */
public class CommonMinecartChest extends CommonMinecartInventory<StorageMinecart> {

	public CommonMinecartChest(StorageMinecart base) {
		super(base);
	}

	@Override
	public List<ItemStack> getBrokenDrops() {
		return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.CHEST, 1));
	}

	@Override
	public Material getCombinedItem() {
		return Material.STORAGE_MINECART;
	}
}
