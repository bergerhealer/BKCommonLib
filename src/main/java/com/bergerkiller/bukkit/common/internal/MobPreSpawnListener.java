package com.bergerkiller.bukkit.common.internal;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * Calls canSpawn right before spawning (and initializing) new entities
 */
public interface MobPreSpawnListener {
	/**
	 * Gets whether a given Entity Type is allowed to be spawned
	 * 
	 * @param world in which to spawn
	 * @param x - coordinate where to spawn at
	 * @param y - coordinate where to spawn at
	 * @param z - coordinate where to spawn at
	 * @param entityType to spawn
	 * @return True if spawning is allowed, False if not
	 */
	public boolean canSpawn(World world, int x, int y, int z, EntityType entityType);
}
