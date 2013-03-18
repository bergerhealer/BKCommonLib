package com.bergerkiller.bukkit.common.entity;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;
import com.bergerkiller.bukkit.common.entity.nms.NMSMinecartRideable;

public class CommonMinecartRideable extends CommonMinecart<RideableMinecart> {

	public CommonMinecartRideable(RideableMinecart base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return NMSMinecartRideable.class;
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
