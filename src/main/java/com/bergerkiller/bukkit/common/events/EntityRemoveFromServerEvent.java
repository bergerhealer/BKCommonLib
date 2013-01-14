package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

/**
 * Fired when an Entity is removed from the server
 */
public class EntityRemoveFromServerEvent extends EntityRemoveEvent {
	private static final HandlerList handlers = new HandlerList();

	public EntityRemoveFromServerEvent(Entity removed) {
		super(removed);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
