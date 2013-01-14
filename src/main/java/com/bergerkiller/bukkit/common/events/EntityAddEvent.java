package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an Entity is added to a World
 */
public class EntityAddEvent extends EntityEvent {
	private static final HandlerList handlers = new HandlerList();

	public EntityAddEvent(Entity added) {
		super(added);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
