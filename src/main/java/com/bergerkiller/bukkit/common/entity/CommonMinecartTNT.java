package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.minecart.ExplosiveMinecart;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;

public class CommonMinecartTNT extends CommonMinecart<ExplosiveMinecart> {

	public CommonMinecartTNT(ExplosiveMinecart base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return null;
	}
}
