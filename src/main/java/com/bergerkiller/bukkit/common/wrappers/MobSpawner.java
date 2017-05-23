package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.server.MobSpawnerAbstractHandle;
import com.bergerkiller.reflection.net.minecraft.server.NMSMobSpawnerAbstract;

public class MobSpawner extends BasicWrapper<MobSpawnerAbstractHandle> {

    public MobSpawner(Object mobSpawnerHandle) {
        setHandle(MobSpawnerAbstractHandle.createHandle(mobSpawnerHandle));
    }

    /**
     * Gets the name of the mob type spawned by this Mob Spawner
     *
     * @return mob name
     */
    public String getMobName() {
        return NMSMobSpawnerAbstract.mobName.get(getRawHandle());
    }

    /**
     * Sets the name of the mob type spawned by this mob spawner
     *
     * @param name of mob
     */
    public void setMobName(String name) {
    	NMSMobSpawnerAbstract.mobName.set(getRawHandle(), name);
    }

    /**
     * Performs the per-tick spawning logic
     */
    public void onTick() {
        handle.onTick();
    }

    /**
     * Gets the current, live-updated spawn delay. When this delay reaches 0,
     * the mob is spawned.
     *
     * @return Mob spawn delay counter
     */
    public int getSpawnDelay() {
        return handle.getSpawnDelay();
    }

    /**
     * Sets the current, live-updated spawn delay. When this delay reaches 0,
     * the mob is spawned.
     *
     * @param tickDelay to set to
     */
    public void setSpawnDelay(int tickDelay) {
        handle.setSpawnDelay(tickDelay);
    }

    /**
     * Gets the minimum tick interval between spawns
     *
     * @return minimum tick interval
     */
    public int getMinSpawnDelay() {
        return handle.getMinSpawnDelay();
    }

    /**
     * Sets the minimum tick interval between spawns
     *
     * @param tickInterval to set to
     */
    public void setMinSpawnDelay(int tickInterval) {
        handle.setMinSpawnDelay(tickInterval);
    }

    /**
     * Gets the maximum tick interval between spawns
     *
     * @return maximum tick interval
     */
    public int getMaxSpawnDelay() {
        return handle.getMaxSpawnDelay();
    }

    /**
     * Sets the maximum tick interval between spawns
     *
     * @param tickInterval to set to
     */
    public void setMaxSpawnDelay(int tickInterval) {
        handle.setMaxSpawnDelay(tickInterval);
    }

    /**
     * Gets the amount of mobs spawned at a time
     *
     * @return mob spawn count
     */
    public int getSpawnCount() {
        return handle.getSpawnCount();
    }

    /**
     * Sets the amount of mobs spawned at a time
     *
     * @param mobCount to set to
     */
    public void setSpawnCount(int mobCount) {
        handle.setSpawnCount(mobCount);
    }
}
