package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.collections.InstanceBuffer;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSBiomeMeta;
import com.bergerkiller.server.CommonNMS;

import net.minecraft.server.v1_11_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_11_R1.Entity;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes server happenings and raises events accordingly
 */
public class CommonEventFactory {

    private final EntityMoveEvent entityMoveEvent = new EntityMoveEvent();
    private final List<Entity> entityMoveEntities = new ArrayList<Entity>();
    private final CreaturePreSpawnEvent creaturePreSpawnEvent = new CreaturePreSpawnEvent();

    private final InstanceBuffer<BiomeMeta> creaturePreSpawnMobs = new InstanceBuffer<BiomeMeta>() {
        @Override
        public BiomeMeta createElement() {
            return new BiomeMeta(null, 0, 0, 0);
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
            entityMoveEntities.addAll(CommonNMS.getEntities(world));
        }
        for (Entity entity : entityMoveEntities) {
            if (entity.locX != entity.lastX || entity.locY != entity.lastY || entity.locZ != entity.lastZ
                    || entity.yaw != entity.lastYaw || entity.pitch != entity.lastPitch) {

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
    public List<BiomeMeta> handleCreaturePreSpawn(World world, int x, int y, int z, List<BiomeMeta> inputTypes) {
        // Shortcuts
        if (LogicUtil.nullOrEmpty(inputTypes) || !CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
            return inputTypes;
        }

        // Start processing the elements
        creaturePreSpawnMobs.clear();
        for (BiomeMeta inputMeta : inputTypes) {
            final EntityType oldEntityType = CommonEntityType.byNMSEntityClass(inputMeta.b).entityType;

            // Set up the event
            creaturePreSpawnEvent.cancelled = false;
            creaturePreSpawnEvent.spawnLocation.setWorld(world);
            creaturePreSpawnEvent.spawnLocation.setX(x);
            creaturePreSpawnEvent.spawnLocation.setY(y);
            creaturePreSpawnEvent.spawnLocation.setZ(z);
            creaturePreSpawnEvent.spawnLocation.setYaw(0.0f);
            creaturePreSpawnEvent.spawnLocation.setPitch(0.0f);
            creaturePreSpawnEvent.entityType = oldEntityType;
            creaturePreSpawnEvent.minSpawnCount = NMSBiomeMeta.minSpawnCount.get(inputMeta);
            creaturePreSpawnEvent.maxSpawnCount = NMSBiomeMeta.maxSpawnCount.get(inputMeta);

            // Raise it and handle spawn cancel
            if (CommonUtil.callEvent(creaturePreSpawnEvent).isCancelled() || (creaturePreSpawnEvent.minSpawnCount == 0 && creaturePreSpawnEvent.maxSpawnCount == 0)) {
                continue;
            }

            // Handle a possibly changed entity type
            final Class<?> entityClass;
            if (oldEntityType == creaturePreSpawnEvent.entityType) {
                entityClass = NMSBiomeMeta.entity.get(inputMeta);
            } else {
                entityClass = CommonEntityType.byEntityType(creaturePreSpawnEvent.entityType).nmsType.getType();
            }

            // Unknown or unsupported Entity Type - ignore spawning
            if (entityClass == null) {
                continue;
            }

            // Add element to buffer
            final Object outputMeta = creaturePreSpawnMobs.add();
            NMSBiomeMeta.entity.set(outputMeta, entityClass);
            NMSBiomeMeta.minSpawnCount.set(outputMeta, creaturePreSpawnEvent.minSpawnCount);
            NMSBiomeMeta.maxSpawnCount.set(outputMeta, creaturePreSpawnEvent.maxSpawnCount);
            NMSBiomeMeta.chance.transfer(inputMeta, outputMeta);
        }
        return creaturePreSpawnMobs;
    }
}
