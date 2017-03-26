package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.reflection.net.minecraft.server.NMSMobSpawnerAbstract;

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
        return NMSMobSpawnerAbstract.mobName.get(getHandle());
    }

    /**
     * Sets the name of the mob type spawned by this mob spawner
     *
     * @param name of mob
     */
    public void setMobName(String name) {
    	NMSMobSpawnerAbstract.mobName.set(getHandle(), name);
    }

    /**
     * Performs the per-tick spawning logic
     */
    public void onTick() {
        NMSMobSpawnerAbstract.onTick.invoke(getHandle());
    }

    /**
     * Gets the current, live-updated spawn delay. When this delay reaches 0,
     * the mob is spawned.
     *
     * @return Mob spawn delay counter
     */
    public int getSpawnDelay() {
        return NMSMobSpawnerAbstract.spawnDelay.get(getHandle());
    }

    /**
     * Sets the current, live-updated spawn delay. When this delay reaches 0,
     * the mob is spawned.
     *
     * @param tickDelay to set to
     */
    public void setSpawnDelay(int tickDelay) {
        NMSMobSpawnerAbstract.spawnDelay.set(getHandle(), tickDelay);
    }

    /**
     * Gets the minimum tick interval between spawns
     *
     * @return minimum tick interval
     */
    public int getMinSpawnDelay() {
        return NMSMobSpawnerAbstract.minSpawnDelay.get(getHandle());
    }

    /**
     * Sets the minimum tick interval between spawns
     *
     * @param tickInterval to set to
     */
    public void setMinSpawnDelay(int tickInterval) {
        NMSMobSpawnerAbstract.minSpawnDelay.set(getHandle(), tickInterval);
    }

    /**
     * Gets the maximum tick interval between spawns
     *
     * @return maximum tick interval
     */
    public int getMaxSpawnDelay() {
        return NMSMobSpawnerAbstract.maxSpawnDelay.get(getHandle());
    }

    /**
     * Sets the maximum tick interval between spawns
     *
     * @param tickInterval to set to
     */
    public void setMaxSpawnDelay(int tickInterval) {
        NMSMobSpawnerAbstract.maxSpawnDelay.set(getHandle(), tickInterval);
    }

    /**
     * Gets the amount of mobs spawned at a time
     *
     * @return mob spawn count
     */
    public int getSpawnCount() {
        return NMSMobSpawnerAbstract.spawnCount.get(getHandle());
    }

    /**
     * Sets the amount of mobs spawned at a time
     *
     * @param mobCount to set to
     */
    public void setSpawnCount(int mobCount) {
        NMSMobSpawnerAbstract.spawnCount.set(getHandle(), mobCount);
    }
}
