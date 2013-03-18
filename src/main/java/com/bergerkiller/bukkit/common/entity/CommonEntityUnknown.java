package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;

public class CommonEntityUnknown<T extends Entity> extends CommonEntity<T> {

	public CommonEntityUnknown(T base) {
		super(base);
	}

	@Override
	protected Class<? extends NMSEntity> getNMSType() {
		return null;
	}
}
