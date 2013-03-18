package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.minecart.HopperMinecart;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;

public class CommonMinecartHopper extends CommonMinecart<HopperMinecart> {

	public CommonMinecartHopper(HopperMinecart base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return null;
	}
}
