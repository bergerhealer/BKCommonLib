package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.minecart.StorageMinecart;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;

public class CommonMinecartChest extends CommonMinecart<StorageMinecart> {

	public CommonMinecartChest(StorageMinecart base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return null;
	}
}
