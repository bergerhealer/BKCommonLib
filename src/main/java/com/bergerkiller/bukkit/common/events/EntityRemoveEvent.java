package com.bergerkiller.bukkit.common.events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an Entity is removed from a World
 */
public class EntityRemoveEvent extends EntityEvent {
    private final World world;
    private static final HandlerList handlers = new HandlerList();

    public EntityRemoveEvent(World world, Entity removed) {
        super(removed);
        this.world = world;
    }

    /**
     * Gets the world from which the entity was removed
     * 
     * @return world
     */
    public World getWorld() {
        return this.world;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
