package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.EntityByIdWorldMap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

public abstract class EntityAddRemoveHandler {
    public static final EntityAddRemoveHandler INSTANCE;
    private final EntityByIdWorldMap entitiesById = new EntityByIdWorldMap();
    private CommonPlugin plugin = null;
    private Task worldSyncTask = null;

    static {
        if (Common.evaluateMCVersion(">=", "1.17")) {
            INSTANCE = new EntityAddRemoveHandler_1_17();
        } else if (Common.evaluateMCVersion(">=", "1.14")) {
            INSTANCE = new EntityAddRemoveHandler_1_14_to_1_16_5();
        } else {
            INSTANCE = new EntityAddRemoveHandler_1_8_to_1_13_2();
        }
    }

    /**
     * Gets an entity added to a world, by its entity id. This method
     * is multithread-safe.
     *
     * @param world World the entity is on
     * @param entityId ID of the entity
     * @return Entity on the world by the ID if found, otherwise null
     */
    public final Entity getEntityById(World world, int entityId) {
        return this.entitiesById.get(world, entityId);
    }

    /**
     * Called when BKCommonLib enables itself. Use this place
     * to register listeners and/or start any tasks.
     *
     * @param plugin
     */
    public void onEnabled(CommonPlugin plugin) {
        this.plugin = plugin;
        this.worldSyncTask = new WorldEntityByIdSyncTask(plugin);
        this.worldSyncTask.start(20*60, 20*60); // every 60 seconds corrects any errors
    }

    /**
     * Called when BKCommonLib disables itself. Use this place
     * to deregister listeners and/or stop any tasks.
     */
    public void onDisabled() {
        Task.stop(this.worldSyncTask);
        this.worldSyncTask = null;
    }

    /**
     * Called when a new world is added/loaded on the server
     *
     * @param world
     */
    public final void onWorldEnabled(World world) {
        this.entitiesById.sync(world);
        this.hook(world);
    }

    /**
     * Called on shutdown, and when worlds are unloaded on the server
     *
     * @param world
     */
    public final void onWorldDisabled(World world) {
        this.unhook(world);
        this.entitiesById.clear(world);
    }

    /**
     * Processes pending events at a time where this is safe to do
     */
    public abstract void processEvents();

    /**
     * This should cover the full replacement of an entity in all internal mappings.
     * This includes the chunk, world and network synchronization objects.
     * 
     * @param oldInstance to replace
     * @param newInstance to replace with
     */
    public abstract void replace(World world, EntityHandle oldEntity, EntityHandle newEntity);

    protected abstract void hook(World world);

    protected abstract void unhook(World world);

    protected final void notifyRemoved(World world, Entity entity) {
        this.entitiesById.remove(world, entity);
        this.plugin.notifyRemoved(world, entity);
    }

    protected final void notifyAddedEarly(World world, Entity entity) {
        this.entitiesById.add(world, entity);
        this.plugin.notifyAddedEarly(world, entity);
    }

    private final class WorldEntityByIdSyncTask extends Task {
        private final Queue<World> worldQueue = new LinkedList<>();

        public WorldEntityByIdSyncTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            if (this.worldQueue.isEmpty()) {
                this.worldQueue.addAll(WorldUtil.getWorlds());
            }

            // Find the next world to sync, in sequence
            // This slows down the process to one world at a time, avoiding
            // lag caused by potentially millions of entities on many worlds.
            World world = this.worldQueue.poll();
            while (true) {
                if (world == null) {
                    return; // wut?
                } else if (Bukkit.getWorld(world.getUID()) == world) {
                    break; // Found the next one
                } else {
                    // Out of sync or world was unloaded/reloaded
                    world = this.worldQueue.poll(); // Skip, try next
                }
            }

            // Sync 'em
            entitiesById.sync(world);
        }
    }
}
