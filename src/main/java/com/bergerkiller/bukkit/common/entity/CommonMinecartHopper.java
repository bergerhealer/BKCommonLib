package com.bergerkiller.bukkit.common.entity;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartHopper extends CommonMinecart<HopperMinecart> {

	public CommonMinecartHopper(HopperMinecart base) {
		super(base);
	}

	@Override
	public List<ItemStack> getBrokenDrops() {
		return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.HOPPER, 1));
	}

	@Override
	public Material getCombinedItem() {
		return Material.HOPPER_MINECART;
	}
}
