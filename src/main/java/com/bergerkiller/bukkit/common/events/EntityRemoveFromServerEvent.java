package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an Entity is removed from the server
 */
public class EntityRemoveFromServerEvent extends EntityEvent {

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
