package com.bergerkiller.bukkit.common.events;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired right before a group of entities is spawned in a world
 */
public final class CreaturePreSpawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancelled;
    protected EntityType entityType;
    protected final Location spawnLocation = new Location(null, 0, 0, 0);

    protected CreaturePreSpawnEvent() {
    }

    /**
     * Gets the Entity Type being spawned
     *
     * @return spawned entity type
     */
    public EntityType getEntityType() {
        return this.entityType;
    }

    /**
     * Gets the Location at which the Entity is spawned (around)
     *
     * @return entity spawn location
     */
    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
