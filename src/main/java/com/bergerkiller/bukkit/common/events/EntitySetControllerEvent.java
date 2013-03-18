package com.bergerkiller.bukkit.common.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;

/**
 * Fired when an Entity Controller is assigned to an entity
 */
public class EntitySetControllerEvent extends EntityEvent {
	private static final HandlerList handlers = new HandlerList();
	private final CommonEntity<?> centity;
	private final EntityController<?> controller;

	public EntitySetControllerEvent(CommonEntity<?> entity, EntityController<?> controller) {
		super(entity.getEntity());
		this.centity = entity;
		this.controller = controller;
	}

	public CommonEntity<?> getCommonEntity() {
		return centity;
	}

	public EntityController<?> getController() {
		return controller;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
