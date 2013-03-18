package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.minecart.PoweredMinecart;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;

public class CommonMinecartFurnace extends CommonMinecart<PoweredMinecart> {

	public CommonMinecartFurnace(PoweredMinecart base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return null;
	}
}
