package com.bergerkiller.bukkit.common.internal.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.EntityByIdWorldMap;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

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
     * Completely despawns an entity and removes it from a world
     *
     * @param entity
     */
    public final void removeEntity(EntityHandle entity) {
        replace(entity, null);
    }

    /**
     * This should cover the full replacement of an entity in all internal mappings.
     * This includes the chunk, world and network synchronization objects.<br>
     * <br>
     * To remove the entity and not replace it with anything new, specify a null
     * newEntity.
     * 
     * @param oldInstance to replace
     * @param newInstance to replace with, null to remove only
     */
    public abstract void replace(EntityHandle oldEntity, EntityHandle newEntity);

    /**
     * Checks what chunk and vertical chunk slice an entity is in, and moves
     * the entity to the right new chunk or slice if changed.
     *
     * @param entity
     */
    public abstract void moveToChunk(EntityHandle entity);

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

    /**
     * Checks all the entries of a List for an entry matching the same
     * old value, and if found, replaces it with the new value
     *
     * @param list List to check
     * @param oldValue Value to find
     * @param newValue Value to replace it with
     * @return True if the list was modified, False if not
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, Object oldValue, Object newValue) {
        if (list == null) {
            return false;
        }
        boolean changed = false;
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            if (iter.next() == oldValue) {
                if (newValue == null) {
                    iter.remove();
                } else {
                    iter.set(newValue);
                }
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Chunk Entity slice storage handler for Minecraft 1.8 to Minecraft 1.16.5
     */
    protected static class ChunkEntitySliceHandler {
        private final FastField<Object[]> chunkEntitySlicesField = new FastField<Object[]>();
        private final boolean chunkEntitySlicesFieldIsLists;
        private final FastMethod<Object> paperspigotSwapEntityInChunkEntityListMethod = new FastMethod<Object>();

        public ChunkEntitySliceHandler() {
            // Chunk EntitySlice[] field, used when entities need to be swapped, or when removing
            // entities from a chunk/world
            {
                boolean isLists = false;
                try {
                    String fieldName = Resolver.resolveFieldName(ChunkHandle.T.getType(), "entitySlices");
                    java.lang.reflect.Field entitySlicesField = ChunkHandle.T.getType().getDeclaredField(fieldName);
                    Class<?> fieldType = entitySlicesField.getType();
                    isLists = (fieldType == List[].class);
                    if (!fieldType.isArray()) {
                        throw new IllegalArgumentException("Field type is not an array, but is " + fieldType);
                    }
                    this.chunkEntitySlicesField.init(entitySlicesField);
                } catch (Throwable t) {
                    Logging.LOGGER_REFLECTION.log(Level.WARNING, "Chunk entitySlices field not found", t);
                    this.chunkEntitySlicesField.initUnavailable("Chunk entitySlices field not found");
                }
                this.chunkEntitySlicesFieldIsLists = isLists;
            }

            // Paperspigot support: 'entities' field of Chunk
            try {
                Class<?> entityListType = Class.forName("com.destroystokyo.paper.util.maplist.EntityList");
                if (SafeField.contains(ChunkHandle.T.getType(), "entities", entityListType)) {
                    ClassResolver resolver = new ClassResolver();

                    resolver.setDeclaredClassName("net.minecraft.world.level.chunk.Chunk");
                    resolver.addImport("net.minecraft.world.entity.Entity");
                    paperspigotSwapEntityInChunkEntityListMethod.init(new MethodDeclaration(resolver,
                            "public void swap(Entity oldEntity, Entity newEntity) {\n" +
                            "    if (instance.entities.remove(oldEntity)) {\n" +
                            "        if (newEntity != null) {\n" +
                            "            instance.entities.add(newEntity);\n" +
                            "        }\n" +
                            "    }\n" +
                            "}"
                    ));
                }
            } catch (ClassNotFoundException ignore) {}
        }

        /**
         * Refreshes the chunk coordinates an entity is in, and moves the entity
         * to the right chunk to be stored in.
         *
         * @param entity
         */
        public void moveToChunk(EntityHandle entity) {
            final int oldcx = entity.getChunkX();
            final int oldcy = entity.getChunkY();
            final int oldcz = entity.getChunkZ();
            final int newcx = MathUtil.toChunk(entity.getLocX());
            final int newcy = MathUtil.toChunk(entity.getLocY());
            final int newcz = MathUtil.toChunk(entity.getLocZ());
            final org.bukkit.World world = entity.getBukkitWorld();
            final boolean changedChunks = oldcx != newcx || oldcy != newcy || oldcz != newcz;
            boolean isLoaded = entity.isLoadedInWorld();

            // Handle chunk/slice movement
            // Remove from the previous chunk
            if (isLoaded && changedChunks) {
                final org.bukkit.Chunk chunk = WorldUtil.getChunk(world, oldcx, oldcz);
                if (chunk != null) {
                    this.removeFromChunk(chunk, entity);
                }
            }
            // Add to the new chunk
            if (!isLoaded || changedChunks) {
                final org.bukkit.Chunk chunk = WorldUtil.getChunk(world, newcx, newcz);
                if (isLoaded = chunk != null) {
                    this.addToChunk(chunk, entity);
                }
                EntityHandle.T.setLoadedInWorld_pre_1_17.invoke(entity.getRaw(), isLoaded);
            }
        }

        /**
         * Replaces an entity instance with another entity instance in a chunk
         *
         * @param chunkHandle The chunk
         * @param oldEntity Entity to find
         * @param newEntity Replacement, null to remove
         * @return True if the old entity was found and subsequently replaced
         */
        public boolean replace(Object chunkHandle, EntityHandle oldEntity, EntityHandle newEntity) {
            Object[] slices = this.chunkEntitySlicesField.get(chunkHandle);
            int chunkY = newEntity.getChunkY();
            boolean found = false;
            if (replaceInSlice(slices[chunkY], oldEntity, newEntity)) {
                found = true;
            } else {
                for (int y = 0; y < slices.length; y++) {
                    if (y != chunkY && replaceInSlice(slices[y], oldEntity, newEntity)) {
                        found = true;
                        break;
                    }
                }
            }

            if (paperspigotSwapEntityInChunkEntityListMethod.isAvailable()) {
                Object newRaw = (newEntity == null) ? null : newEntity.getRaw();
                paperspigotSwapEntityInChunkEntityListMethod.invoke(chunkHandle, oldEntity.getRaw(), newRaw);
            }

            return found;
        }

        /**
         * Removes an entity from all the fields of a Chunk
         *
         * @param chunk
         * @param entity
         */
        public boolean removeFromChunk(Chunk chunk, EntityHandle entity) {
            return replace(HandleConversion.toChunkHandle(chunk), entity, null);
        }

        /**
         * Adds an entity to the relevant fields of a chunk
         *
         * @param chunkHandle
         * @param entity
         */
        public void addToChunk(Chunk chunk, EntityHandle entity) {
            ChunkHandle.fromBukkit(chunk).addEntity(entity);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private List<Object> sliceToList(Object entitySlice) {
            return this.chunkEntitySlicesFieldIsLists ?
                    (List) entitySlice : HandleConversion.cbEntitySliceToList(entitySlice);
        }

        private boolean replaceInSlice(Object slice, EntityHandle oldEntity, EntityHandle newEntity) {
            Object newRaw = (newEntity == null) ? null : newEntity.getRaw();
            return replaceInList(sliceToList(slice), oldEntity.getRaw(), newRaw);
        }
    }
}
