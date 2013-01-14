package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an Entity is removed from a World
 */
public class EntityRemoveEvent extends EntityEvent {
	private static final HandlerList handlers = new HandlerList();

	public EntityRemoveEvent(Entity removed) {
		super(removed);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
