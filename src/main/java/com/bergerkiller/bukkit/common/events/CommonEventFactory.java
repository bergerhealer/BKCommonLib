package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.collections.InstanceBuffer;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle.BiomeMetaHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes server happenings and raises events accordingly
 */
public class CommonEventFactory {

    private final EntityMoveEvent entityMoveEvent = new EntityMoveEvent();
    private final List<EntityHandle> entityMoveEntities = new ArrayList<EntityHandle>();
    private final CreaturePreSpawnEvent creaturePreSpawnEvent = new CreaturePreSpawnEvent();

    private final InstanceBuffer<BiomeMetaHandle> creaturePreSpawnMobs = new InstanceBuffer<BiomeMetaHandle>() {
        @Override
        public BiomeMetaHandle createElement() {
            return BiomeMetaHandle.createNew(null, 0, 0, 0);
        }
    };

    /**
     * Fires Entity Move events for all entities that moved on the server
     */
    public void handleEntityMove() {
        if (!CommonUtil.hasHandlers(EntityMoveEvent.getHandlerList())) {
            return;
        }
        for (World world : WorldUtil.getWorlds()) {
            entityMoveEntities.addAll(WorldHandle.fromBukkit(world).getEntityList());
        }
        for (EntityHandle entity : entityMoveEntities) {
            if (entity.getLocX() != entity.getLastX() || entity.getLocY() != entity.getLastY() || entity.getLocZ() != entity.getLastZ()
                    || entity.getYaw() != entity.getLastYaw() || entity.getPitch() != entity.getLastPitch()) {

                entityMoveEvent.setEntity(entity);
                CommonUtil.callEvent(entityMoveEvent);
            }
        }
        entityMoveEntities.clear();
    }

    /**
     * Handles the spawning of creatures on the server
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param inputTypes to process and fire events for
     * @return a list of mobs to spawn
     */
    public List<BiomeMetaHandle> handleCreaturePreSpawn(World world, int x, int y, int z, List<BiomeMetaHandle> inputTypes) {
        // Shortcuts
        if (LogicUtil.nullOrEmpty(inputTypes) || !CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
            return inputTypes;
        }

        // Start processing the elements
        creaturePreSpawnMobs.clear();
        for (BiomeMetaHandle inputMeta : inputTypes) {
            final EntityType oldEntityType = CommonEntityType.byNMSEntityClass(inputMeta.getEntityClass()).entityType;

            // Set up the event
            creaturePreSpawnEvent.cancelled = false;
            creaturePreSpawnEvent.spawnLocation.setWorld(world);
            creaturePreSpawnEvent.spawnLocation.setX(x);
            creaturePreSpawnEvent.spawnLocation.setY(y);
            creaturePreSpawnEvent.spawnLocation.setZ(z);
            creaturePreSpawnEvent.spawnLocation.setYaw(0.0f);
            creaturePreSpawnEvent.spawnLocation.setPitch(0.0f);
            creaturePreSpawnEvent.entityType = oldEntityType;
            creaturePreSpawnEvent.minSpawnCount = inputMeta.getMinSpawnCount();
            creaturePreSpawnEvent.maxSpawnCount = inputMeta.getMaxSpawnCount();

            // Raise it and handle spawn cancel
            if (CommonUtil.callEvent(creaturePreSpawnEvent).isCancelled() || (creaturePreSpawnEvent.minSpawnCount == 0 && creaturePreSpawnEvent.maxSpawnCount == 0)) {
                continue;
            }

            // Handle a possibly changed entity type
            final Class<?> entityClass;
            if (oldEntityType == creaturePreSpawnEvent.entityType) {
                entityClass = inputMeta.getEntityClass();
            } else {
                entityClass = CommonEntityType.byEntityType(creaturePreSpawnEvent.entityType).nmsType.getType();
            }

            // Unknown or unsupported Entity Type - ignore spawning
            if (entityClass == null) {
                continue;
            }

            // Add element to buffer
            final BiomeMetaHandle outputMeta = creaturePreSpawnMobs.add();
            outputMeta.setEntityClass(entityClass);
            outputMeta.setMinSpawnCount(creaturePreSpawnEvent.minSpawnCount);
            outputMeta.setMaxSpawnCount(creaturePreSpawnEvent.maxSpawnCount);
            outputMeta.setChance(inputMeta.getChance());
        }
        return creaturePreSpawnMobs;
    }
}
