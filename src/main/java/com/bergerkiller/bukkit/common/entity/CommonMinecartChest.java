package com.bergerkiller.bukkit.common.entity;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartChest extends CommonMinecart<StorageMinecart> {

	public CommonMinecartChest(StorageMinecart base) {
		super(base);
	}

	public Inventory getInventory() {
		return entity.getInventory();
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
