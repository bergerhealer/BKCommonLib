package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.collections.SortedIdentityCache;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Processes server happenings and raises events accordingly
 */
public class CommonEventFactory {
    private final EntityMoveEvent entityMoveEvent = new EntityMoveEvent();
    private final SortedIdentityCache<Object, EntityHandle> entityMoveEntities = SortedIdentityCache.create(EntityHandle::createHandle);
    private final CreaturePreSpawnEvent creaturePreSpawnEvent = new CreaturePreSpawnEvent();

    /**
     * Fires Entity Move events for all entities that moved on the server
     */
    @SuppressWarnings("unchecked")
    public void handleEntityMove() {
        if (!CommonUtil.hasHandlers(EntityMoveEvent.getHandlerList())) {
            this.entityMoveEntities.clear();
            return;
        }

        // Keeps a list of all raw entity handles synchronized with EntityHandle wrappers
        this.entityMoveEntities.sync(consumer -> {
            for (World world : WorldUtil.getWorlds()) {
                Object worldHandle = WorldServerHandle.fromBukkit(world).getRaw();
                Iterable<Object> worldEntities = (Iterable<Object>) WorldServerHandle.T.getEntities.raw.invoke(worldHandle);
                worldEntities.forEach(consumer);
            }
        });

        // Fire all events
        this.entityMoveEntities.forEach(entity -> {
            if (entity.isLastAndCurrentPositionDifferent()) {
                entityMoveEvent.setEntity(entity);
                CommonUtil.callEvent(entityMoveEvent);
            }
        });
    }

    /**
     * Fires a CreaturePreSpawnEvent for one or more entities at/around a given block location
     * on a world.
     *
     * @param world World where the entity or group of entities are spawned
     * @param x X-coordinate of the block near which is spawned
     * @param y Y-coordinate of the block near which is spawned
     * @param z Z-coordinate of the block near which is spawned
     * @param entityType Type of entity being spawned
     * @param reason Reason for spawning the entity
     * @return True if spawning is allowed, False if it should be cancelled
     */
    public boolean handleCreaturePreSpawn(World world, int x, int y, int z, EntityType entityType, CreatureSpawnEvent.SpawnReason reason) {
        creaturePreSpawnEvent.cancelled = false;
        creaturePreSpawnEvent.spawnLocation.setWorld(world);
        creaturePreSpawnEvent.spawnLocation.setX(x);
        creaturePreSpawnEvent.spawnLocation.setY(y);
        creaturePreSpawnEvent.spawnLocation.setZ(z);
        creaturePreSpawnEvent.spawnLocation.setYaw(0.0f);
        creaturePreSpawnEvent.spawnLocation.setPitch(0.0f);
        creaturePreSpawnEvent.entityType = entityType;
        creaturePreSpawnEvent.reason = reason;
        return !CommonUtil.callEvent(creaturePreSpawnEvent).isCancelled();
    }

    /**
     * Fires a CreaturePreSpawnEvent for one or more entities at/around a given location
     * on a world.
     *
     * @param at The location near or at which is spawned
     * @param entityType Type of entity being spawned
     * @param reason Reason for spawning the entity
     * @return True if spawning is allowed, False if it should be cancelled
     */
    public boolean handleCreaturePreSpawn(Location at, EntityType entityType, CreatureSpawnEvent.SpawnReason reason) {
        creaturePreSpawnEvent.cancelled = false;
        creaturePreSpawnEvent.spawnLocation.setWorld(at.getWorld());
        creaturePreSpawnEvent.spawnLocation.setX(at.getX());
        creaturePreSpawnEvent.spawnLocation.setY(at.getY());
        creaturePreSpawnEvent.spawnLocation.setZ(at.getZ());
        creaturePreSpawnEvent.spawnLocation.setYaw(at.getYaw());
        creaturePreSpawnEvent.spawnLocation.setPitch(at.getPitch());
        creaturePreSpawnEvent.entityType = entityType;
        creaturePreSpawnEvent.reason = reason;
        return !CommonUtil.callEvent(creaturePreSpawnEvent).isCancelled();
    }
}
