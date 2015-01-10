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
	protected int minSpawnCount, maxSpawnCount;
	protected final Location spawnLocation = new Location(null, 0, 0, 0);

	protected CreaturePreSpawnEvent() {
	}

	/**
	 * Sets the Entity Type being spawned
	 * 
	 * @param entityType to set to
	 */
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
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
	 * Gets the minimum amount of times the Entity is spawned
	 * 
	 * @return spawn couny
	 */
	public int getMinSpawnCount() {
		return this.minSpawnCount;
	}

	/**
	 * Sets the minimum amount of times the Entity is spawned
	 * 
	 * @param minSpawnCount to set to
	 */
	public void setMinSpawnCount(int minSpawnCount) {
		this.minSpawnCount = minSpawnCount;
	}

	/**
	 * Gets the maximum amount of times the Entity is spawned
	 * 
	 * @return spawn couny
	 */
	public int getMaxSpawnCount() {
		return this.maxSpawnCount;
	}

	/**
	 * Sets the maximum amount of times the Entity is spawned
	 * 
	 * @param maxSpawnCount to set to
	 */
	public void setMaxSpawnCount(int maxSpawnCount) {
		this.maxSpawnCount = maxSpawnCount;
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
