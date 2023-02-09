package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.EntityByIdWorldMap;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.ChunkUnloadEntitiesEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

public abstract class EntityAddRemoveHandler implements LazyInitializedObject, LibraryComponent {
    public static final EntityAddRemoveHandler INSTANCE = LibraryComponentSelector.forModule(EntityAddRemoveHandler.class)
            .addVersionOption(null, "1.13.2", EntityAddRemoveHandler_1_8_to_1_13_2::new)
            .addVersionOption("1.14", "1.16.5", EntityAddRemoveHandler_1_14_to_1_16_5::new)
            .addWhen("Paper ChunkSystem EntityAddRemoveHandler", e -> {
                try {
                    Class.forName("io.papermc.paper.chunk.system.entity.EntityLookup");
                    return true;
                } catch (Throwable t) {
                    return false;
                }
            }, EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem::new)
            .addVersionOption("1.17", null, EntityAddRemoveHandler_1_17::new)
            .update();

    private final EntityByIdWorldMap entitiesById = new EntityByIdWorldMap();
    private CommonPlugin plugin = null;
    private Task worldSyncTask = null;

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
     * Gets whether the entities inside the given chunk have been loaded already.
     * The chunk may be loaded, but the entities may not yet, depending on the
     * Minecraft version.<br>
     * <br>
     * If the chunk has no entities, but processing/loading of entities has
     * finished, this method also returns true.
     *
     * @param world World the chunk is in
     * @param cx X-coordinate of the chunk
     * @param cz Z-coordinate of the chunk
     * @return True if the entities of this chunk have been loaded
     */
    public abstract boolean isChunkEntitiesLoaded(World world, int cx, int cz);

    /**
     * Gets whether the entities inside the given chunk have been loaded already.
     * The chunk may be loaded, but the entities may not yet, depending on the
     * Minecraft version.<br>
     * <br>
     * If the chunk has no entities, but processing/loading of entities has
     * finished, this method also returns true.
     *
     * @param chunk Chunk to check
     * @return True if entities in the chunk are loaded
     */
    public abstract boolean isChunkEntitiesLoaded(Chunk chunk);

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
     * @param oldEntity Previous entity to replace
     * @param newEntity The entity to replace the previous one with, null to remove only
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

    protected final void notifyChunkEntitiesLoaded(Chunk chunk) {
        this.processEvents();
        CommonUtil.callEvent(new ChunkLoadEntitiesEvent(chunk));
    }

    protected final void notifyChunkEntitiesUnloaded(Chunk chunk) {
        this.processEvents();
        CommonUtil.callEvent(new ChunkUnloadEntitiesEvent(chunk));
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

        if (newValue == null) {
            return list.remove(oldValue);
        } else if (canMutateListIterator(list)) {
            boolean changed = false;
            ListIterator<Object> iter = list.listIterator();
            while (iter.hasNext()) {
                if (iter.next() == oldValue) {
                    iter.set(newValue);
                    changed = true;
                }
            }
            return changed;
        } else {
            // Note: weird List class returns Integer.MIN_VALUE rather than -1 when not found
            // Check for index within range to cover all cases safely.
            int index = list.indexOf(oldValue);
            if (index >= 0 && index < list.size() && list.get(index) == oldValue) {
                list.remove(oldValue); // by index not always supported
                list.add(index, newValue);
                return true;
            } else {
                return false;
            }
        }
    }

    private static boolean canMutateListIterator(List<?> list) {
        for (Class<?> type : listsWithImmutableListIterator) {
            if (type.isInstance(list)) {
                return false;
            }
        }
        return true;
    }

    // These list types cannot be modified using the listIterator().
    private static final List<Class<?>> listsWithImmutableListIterator;
    static {
        List<Class<?>> lists = new ArrayList<>();
        lists.add(CommonUtil.getClass("io.papermc.paper.util.maplist.ObjectMapList", false));
        listsWithImmutableListIterator = lists.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Chunk Entity slice storage handler for Minecraft 1.8 to Minecraft 1.16.5
     */
    protected static class ChunkEntitySliceHandler {
        private final FastField<Object[]> chunkEntitySlicesField = new FastField<Object[]>();
        private final boolean chunkEntitySlicesFieldIsLists;
        private final HandlerLogic logic;

        public ChunkEntitySliceHandler() {
            // Chunk EntitySlice[] field, used when entities need to be swapped, or when removing
            // entities from a chunk/world
            {
                boolean isLists = false;
                try {
                    String fieldName = Resolver.resolveFieldName(ChunkHandle.T.getType(), "entitySlices");
                    java.lang.reflect.Field entitySlicesField = MPLType.getDeclaredField(ChunkHandle.T.getType(), fieldName);
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

            this.logic = Template.Class.create(HandlerLogic.class, Common.TEMPLATE_RESOLVER);
            this.logic.forceInitialization();
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
            int chunkY = oldEntity.getChunkY();
            if (chunkY < 0) {
                chunkY = 0;
            } else if (chunkY >= slices.length) {
                chunkY = slices.length - 1;
            }

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

            // For changes in forks where entities are stored in more places
            this.logic.replaceInChunkSpecial(chunkHandle, oldEntity.getRaw(), Handle.getRaw(newEntity));

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
         * @param chunk
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

        @Template.Optional
        @Template.Import("net.minecraft.server.level.WorldServer")
        @Template.Import("net.minecraft.server.level.ChunkProviderServer")
        @Template.Import("net.minecraft.world.entity.Entity")
        @Template.Import("net.minecraft.util.EntitySlice")
        @Template.InstanceType("net.minecraft.world.level.chunk.Chunk")
        public static abstract class HandlerLogic extends Template.Class<Template.Handle> {

            /*
             * <REPLACE_IN_CHUNK_SPECIAL>
             * public static void replaceInChunkSpecial(Chunk chunk, Entity oldEntity, Entity newEntity) {
             *     // Paperspigot
             * #if exists net.minecraft.world.level.chunk.Chunk public final com.destroystokyo.paper.util.maplist.EntityList entities;
             *     if (chunk.entities.remove(oldEntity)) {
             *         if (newEntity != null) {
             *             chunk.entities.add(newEntity);
             *         }
             *     }
             * #endif
             * 
             *     // Tuinity
             * #if exists net.minecraft.world.level.chunk.Chunk protected final com.tuinity.tuinity.world.ChunkEntitySlices entitySlicesManager;
             *     #require net.minecraft.world.level.chunk.Chunk protected final com.tuinity.tuinity.world.ChunkEntitySlices entitySlicesManager;
             *     com.tuinity.tuinity.world.ChunkEntitySlices slices = chunk#entitySlicesManager;
             *     synchronized (slices) {
             *         // Locate the old entity inside the "allEntities" slices to figure out if it is stored,
             *         // and at what y-section it is stored. Looks between minSection and maxSection.
             *         #require com.tuinity.tuinity.world.ChunkEntitySlices protected final (Object) ChunkEntitySlices.EntityCollectionBySection allEntities;
             *         #require com.tuinity.tuinity.world.ChunkEntitySlices.EntityCollectionBySection protected final (Object[]) com.tuinity.tuinity.world.ChunkEntitySlices.BasicEntityList[] entitiesBySection;
             *         Object allEntities = slices#allEntities;
             *         Object[] sections = allEntities#entitiesBySection;
             * 
             *         #require com.tuinity.tuinity.world.ChunkEntitySlices.BasicEntityList public boolean has(E extends net.minecraft.world.entity.Entity entity);
             *         boolean found = false;
             *         int relIdxFound = 0;
             *         for (int i = 0; i < sections.length; i++) {
             *             Object section = sections[i];
             *             if (section != null) {
             *                 found = section#has(oldEntity);
             *                 if (found) {
             *                     relIdxFound = i;
             *                     break;
             *                 }
             *             }
             *         }
             *         if (found) {
             *             #require com.tuinity.tuinity.world.ChunkEntitySlices protected final int minSection;
             *             int sectionIdx = relIdxFound + slices#minSection;
             *             slices.removeEntity(oldEntity, sectionIdx);
             *             if (newEntity != null) {
             *                 slices.addEntity(newEntity, sectionIdx);
             *             }
             *         }
             *     }
             * #endif
             * }
             */
            @Template.Generated("%REPLACE_IN_CHUNK_SPECIAL%")
            public abstract void replaceInChunkSpecial(Object chunkHandle, Object oldEntity, Object newEntity);
        }
    }
}
