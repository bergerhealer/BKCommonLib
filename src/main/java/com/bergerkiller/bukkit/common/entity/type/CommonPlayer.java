package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.entity.CommonEntity;

public class CommonPlayer extends CommonEntity<Player> {

	public CommonPlayer(Player entity) {
		super(entity);
	}

	public String getName() {
		return entity.getName();
	}

	public String getCustomName() {
		return entity.getCustomName();
	}

	public void setCustomName(String customName) {
		entity.setCustomName(customName);
	}

	public boolean isCustomNameVisible() {
		return entity.isCustomNameVisible();
	}

	public void setCustomNameVisible(boolean visible) {
		entity.setCustomNameVisible(visible);
	}
}
