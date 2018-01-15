package com.bergerkiller.bukkit.common.events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an Entity is added to a World
 */
public class EntityAddEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private final World world;

    public EntityAddEvent(World world, Entity added) {
        super(added);
        this.world = world;
    }

    /**
     * Gets the world to which the entity was added
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
