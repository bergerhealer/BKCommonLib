package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.minecart.RideableMinecart;

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
}
