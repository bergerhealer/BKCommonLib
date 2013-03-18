package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.minecart.SpawnerMinecart;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;

public class CommonMinecartMobSpawner extends CommonMinecart<SpawnerMinecart> {

	public CommonMinecartMobSpawner(SpawnerMinecart base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return null;
	}
}
