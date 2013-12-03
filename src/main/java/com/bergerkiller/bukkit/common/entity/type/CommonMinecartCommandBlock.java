package com.bergerkiller.bukkit.common.entity.type;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;

/**
 * A Common Entity implementation for Minecarts with a Command Block
 */
public class CommonMinecartCommandBlock extends CommonMinecart<CommandMinecart> {

	public CommonMinecartCommandBlock(CommandMinecart base) {
		super(base);
	}

	@Override
	public List<ItemStack> getBrokenDrops() {
		return Arrays.asList(new ItemStack(Material.MINECART, 1), new ItemStack(Material.COMMAND, 1));
	}

	@Override
	public Material getCombinedItem() {
		return Material.COMMAND_MINECART;
	}
}
