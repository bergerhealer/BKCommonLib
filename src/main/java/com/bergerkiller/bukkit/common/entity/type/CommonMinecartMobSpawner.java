package com.bergerkiller.bukkit.common.entity.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.inventory.ItemStack;

public class CommonMinecartMobSpawner extends CommonMinecart<SpawnerMinecart> {

	public CommonMinecartMobSpawner(SpawnerMinecart base) {
		super(base);
	}

	@Override
	public List<ItemStack> getBrokenDrops() {
		return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.MOB_SPAWNER, 1));
	}

	@Override
	public Material getCombinedItem() {
		return Material.MINECART; //TODO: Missing!
	}
}
