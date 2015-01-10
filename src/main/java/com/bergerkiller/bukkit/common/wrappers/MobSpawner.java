package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.reflection.classes.MobSpawnerAbstractRef;

public class MobSpawner extends BasicWrapper {

	public MobSpawner(Object mobSpawnerHandle) {
		setHandle(mobSpawnerHandle);
	}

	/**
	 * Gets the name of the mob type spawned by this Mob Spawner
	 * 
	 * @return mob name
	 */
	public String getMobName() {
		return MobSpawnerAbstractRef.getMobName.invoke(getHandle());
	}
	
	/**
	 * Sets the name of the mob type spawned by this mob spawner
	 * 
	 * @param name of mob
	 */
	public void setMobName(String name) {
		MobSpawnerAbstractRef.setMobName.invoke(getHandle(), name);
	}

	/**
	 * Performs the per-tick spawning logic
	 */
	public void onTick() {
		MobSpawnerAbstractRef.onTick.invoke(getHandle());
	}

	/**
	 * Gets the current, live-updated spawn delay. When this delay reaches
	 * 0, the mob is spawned.
	 * 
	 * @return Mob spawn delay counter
	 */
	public int getSpawnDelay() {
		return MobSpawnerAbstractRef.spawnDelay.get(getHandle());
	}

	/**
	 * Sets the current, live-updated spawn delay. When this delay reaches
	 * 0, the mob is spawned.
	 * 
	 * @param tickDelay to set to
	 */
	public void setSpawnDelay(int tickDelay) {
		MobSpawnerAbstractRef.spawnDelay.set(getHandle(), tickDelay);
	}

	/**
	 * Gets the minimum tick interval between spawns
	 * 
	 * @return minimum tick interval
	 */
	public int getMinSpawnDelay() {
		return MobSpawnerAbstractRef.minSpawnDelay.get(getHandle());
	}

	/**
	 * Sets the minimum tick interval between spawns
	 * 
	 * @param tickInterval to set to
	 */
	public void setMinSpawnDelay(int tickInterval) {
		MobSpawnerAbstractRef.minSpawnDelay.set(getHandle(), tickInterval);
	}

	/**
	 * Gets the maximum tick interval between spawns
	 * 
	 * @return maximum tick interval
	 */
	public int getMaxSpawnDelay() {
		return MobSpawnerAbstractRef.maxSpawnDelay.get(getHandle());
	}

	/**
	 * Sets the maximum tick interval between spawns
	 * 
	 * @param tickInterval to set to
	 */
	public void setMaxSpawnDelay(int tickInterval) {
		MobSpawnerAbstractRef.maxSpawnDelay.set(getHandle(), tickInterval);
	}

	/**
	 * Gets the amount of mobs spawned at a time
	 * 
	 * @return mob spawn count
	 */
	public int getSpawnCount() {
		return MobSpawnerAbstractRef.spawnCount.get(getHandle());
	}

	/**
	 * Sets the amount of mobs spawned at a time
	 * 
	 * @param mobCount to set to
	 */
	public void setSpawnCount(int mobCount) {
		MobSpawnerAbstractRef.spawnCount.set(getHandle(), mobCount);
	}
}