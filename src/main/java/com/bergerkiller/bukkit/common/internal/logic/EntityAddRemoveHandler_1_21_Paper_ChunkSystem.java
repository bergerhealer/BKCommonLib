package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

/**
 * Since Minecraft 1.21 Paper migrated a lot of chunk system stuff to a new package.
 * The old level callback logic no longer works as it is no longer in use.
 */
class EntityAddRemoveHandler_1_21_Paper_ChunkSystem extends EntityAddRemoveHandler {
    private final AddRemoveHandlerLogic removeHandler;
    private final ChunkEntitiesLoadedHandler chunkEntitiesLoadedHandler;
    private final EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents addRemoveEventHandler;

    public EntityAddRemoveHandler_1_21_Paper_ChunkSystem() {
        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
        this.chunkEntitiesLoadedHandler = new ChunkEntitiesLoadedUsingEventHandler();
        this.addRemoveEventHandler = new EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents(this);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        removeHandler.forceInitialization();
        addRemoveEventHandler.forceInitialization();
    }

    @Override
    public void processEvents() {
        addRemoveEventHandler.processEvents();
    }

    @Override
    public boolean isChunkEntitiesLoaded(World world, int cx, int cz) {
        return removeHandler.isChunkEntitiesLoaded(HandleConversion.toWorldHandle(world), cx, cz);
    }

    @Override
    public boolean isChunkEntitiesLoaded(Chunk chunk) {
        return removeHandler.isChunkEntitiesLoaded(HandleConversion.toWorldHandle(chunk.getWorld()),
                chunk.getX(), chunk.getZ());
    }

    @Override
    public void onEnabled(CommonPlugin plugin) {
        super.onEnabled(plugin);
        this.addRemoveEventHandler.enable(plugin);
        this.chunkEntitiesLoadedHandler.enable(this, plugin);
    }

    @Override
    protected void hook(World world) {
        this.chunkEntitiesLoadedHandler.hook(this, world);
    }

    @Override
    protected void unhook(World world) {
        this.chunkEntitiesLoadedHandler.unhook(this, world);
    }

    /**
     * Makes use of the Paper's EntityAddToWorldEvent and EntityRemoveFromWorldEvent to track
     * when entities get added/removed from a world.
     */
    private static class EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents implements LazyInitializedObject, Listener {
        private final EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler;
        private final Queue<PendingAddEvent> pendingAddEvents = new LinkedList<>();
        private final FastMethod<World> addToWorldGetWorldMethod = new FastMethod<>();
        private final FastMethod<World> removeFromWorldGetWorldMethod = new FastMethod<>();

        public EntityAddRemoveEventHandlerUsingPaperWorldEntityEvents(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler) {
            this.handler = handler;
        }

        public void enable(CommonPlugin plugin) {
            Class<? extends EntityEvent> entitiesLoadEventType = CommonUtil.unsafeCast(CommonUtil.getClass("com.destroystokyo.paper.event.entity.EntityAddToWorldEvent"));
            try {
                addToWorldGetWorldMethod.init(entitiesLoadEventType.getMethod("getWorld"));
            } catch (Throwable t) {
                addToWorldGetWorldMethod.initUnavailable("Method getWorld of EntityAddToWorldEvent not found");
            }
            Bukkit.getPluginManager().registerEvent(entitiesLoadEventType, this, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    onEntityAddedToWorld((EntityEvent) event);
                }
            }, plugin);

            Class<? extends EntityEvent> entitiesUnloadEventType = CommonUtil.unsafeCast(CommonUtil.getClass("com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent"));
            try {
                removeFromWorldGetWorldMethod.init(entitiesUnloadEventType.getMethod("getWorld"));
            } catch (Throwable t) {
                removeFromWorldGetWorldMethod.initUnavailable("Method getWorld of EntityRemoveFromWorldEvent not found");
            }
            Bukkit.getPluginManager().registerEvent(entitiesUnloadEventType, this, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    onEntityRemovedFromWorld((EntityEvent) event);
                }
            }, plugin);
        }

        private void onEntityAddedToWorld(EntityEvent event) {
            Entity entity = event.getEntity();
            World world = addToWorldGetWorldMethod.invoke(event);

            pendingAddEvents.add(new PendingAddEvent(world, entity));
            handler.notifyAddedEarly(world, entity);
        }

        private void onEntityRemovedFromWorld(EntityEvent event) {
            Entity entity = event.getEntity();
            World world = removeFromWorldGetWorldMethod.invoke(event);

            pendingAddEvents.removeIf(e -> e.entity == entity);
            handler.notifyRemoved(world, entity);
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                PendingAddEvent pending = pendingAddEvents.poll();
                CommonPlugin.getInstance().notifyAdded(pending.world ,pending.entity);
            }
        }

        @Override
        public void forceInitialization() {
            addToWorldGetWorldMethod.forceInitialization();
            removeFromWorldGetWorldMethod.forceInitialization();
        }

        private static class PendingAddEvent {
            public final World world;
            public final Entity entity;

            public PendingAddEvent(World world, Entity entity) {
                this.world = world;
                this.entity = entity;
            }
        }
    }

    @Override
    public void replace(EntityHandle oldEntity, EntityHandle newEntity) {
        WorldServerHandle world = oldEntity.getWorldServer();
        if (newEntity == null) {
            if (world != null) {
                world.removeEntity(oldEntity);
                world.getEntityTracker().stopTracking(oldEntity.getBukkitEntity());
            }
        }

        // *** EntityTrackerEntry ***
        if (newEntity == null) {
            // If still tracked despite removal, wipe the tracker
            world.getEntityTracker().removeEntry(oldEntity.getIdField());
        } else {
            // Replacement
            replaceInEntityTracker(oldEntity, oldEntity, newEntity);
            if (oldEntity.getVehicle() != null) {
                replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
            }
            if (oldEntity.getPassengers() != null) {
                for (EntityHandle passenger : oldEntity.getPassengers()) {
                    replaceInEntityTracker(passenger, oldEntity, newEntity);
                }
            }
        }

        Object newEntityRaw = Handle.getRaw(newEntity);
        if (world != null) {
            removeHandler.replaceInWorldStorage(world.getRaw(), oldEntity.getRaw(), newEntityRaw);
        }

        removeHandler.replaceInSectionStorage(oldEntity.getRaw(), newEntityRaw);

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        // com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldEntity.getRaw());
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
        // There appears to be nothing to do, since all of this is now done
        // inside setPositionRaw - so right away.
    }

    @SuppressWarnings("unchecked")
    private static void replaceInEntityTracker(EntityHandle entity, EntityHandle oldEntity, EntityHandle newEntity) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newEntity.getBukkitWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entity.getIdField());
        if (entry != null) {
            // PlayerChunkMap$EntityTracker entity
            EntityHandle entryEntity = entry.getEntity();
            if (entryEntity != null && entryEntity.getIdField() == oldEntity.getIdField()) {
                entry.setEntity(newEntity);
            }

            // EntityTrackerEntry 'tracker' entity
            EntityTrackerEntryStateHandle stateHandle = entry.getState();
            EntityHandle stateEntity = stateHandle.getEntity();
            if (stateEntity != null && stateEntity.getIdField() == oldEntity.getIdField() && stateEntity.getRaw() != newEntity.getRaw()) {
                stateHandle.setEntity(newEntity);
            }

            // EntityTrackerEntry List of passengers
            List<Object> statePassengers = (List<Object>) EntityTrackerEntryStateHandle.T.opt_passengers.raw.get(stateHandle.getRaw());
            replaceInList(statePassengers, oldEntity, newEntity);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, EntityHandle oldEntity, EntityHandle newEntity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle) obj;
                if (obj_e.getIdField() == oldEntity.getIdField()) {
                    iter.set(newEntity);
                }
            } else if (EntityHandle.T.isAssignableFrom(obj)) {
                int obj_id = EntityHandle.T.idField.getInteger(obj);
                if (obj_id == oldEntity.getIdField()) {
                    iter.set(newEntity.getRaw());
                }
            }
        }
        return false;
    }

    @Template.Optional
    @Template.Import("net.minecraft.server.level.WorldServer")
    @Template.Import("net.minecraft.world.level.chunk.Chunk")
    @Template.Import("net.minecraft.server.level.ChunkProviderServer")
    @Template.Import("net.minecraft.world.entity.Entity")
    @Template.Import("net.minecraft.world.level.entity.Visibility")
    @Template.Import("net.minecraft.util.EntitySlice")
    @Template.Import("net.minecraft.world.level.ChunkCoordIntPair")
    @Template.Import("net.minecraft.core.BlockPosition")
    @Template.Import("net.minecraft.world.entity.Visibility")
    @Template.Import("java.util.concurrent.locks.StampedLock")
    @Template.Import("it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap")
    @Template.Import("it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap")
    @Template.Import("it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap")
    @Template.Import("net.minecraft.world.level.entity.EntityTickList")
    @Template.Import("ca.spottedleaf.moonrise.patches.chunk_system.level.entity.ChunkEntitySlices")
    @Template.InstanceType("ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup")
    public static abstract class AddRemoveHandlerLogic extends Template.Class<Handle> {

        /*
         * <IS_CHUNK_ENTITIES_LOADED>
         * public static boolean isChunkEntitiesLoaded(WorldServer world, int cx, int cz) {
         *     // Just checks that the chunk is loaded, really
         *     return world.areEntitiesLoaded(ChunkCoordIntPair.asLong(cx, cz));
         * }
         */
        @Template.Generated("%IS_CHUNK_ENTITIES_LOADED%")
        public abstract boolean isChunkEntitiesLoaded(Object worldHandle, int cx, int cz);

        /*
         * <REPLACE_IN_WORLD_STORAGE>
         * public static void replaceInWorldStorage(WorldServer world, Entity oldEntity, Entity newEntity) {
         *     #require net.minecraft.world.entity.Entity private int entityId:id;
         *     #require net.minecraft.world.entity.Entity protected UUID entityUUID:uuid;
         *     int entityId = oldEntity#entityId;
         *     UUID entityUUID = oldEntity#entityUUID;
         * 
         *     EntityLookup entityLookup = world.moonrise$getEntityLookup();
         *
         *     #require net.minecraft.server.level.WorldServer final net.minecraft.world.level.entity.EntityTickList entityTickList;
         *     EntityTickList tickList = world#entityTickList;
         *
         *     #require net.minecraft.world.level.entity.EntityTickList private final ca.spottedleaf.moonrise.common.list.IteratorSafeOrderedReferenceSet<Entity> entities;
         *     ca.spottedleaf.moonrise.common.list.IteratorSafeOrderedReferenceSet set = tickList#entities;
         *     if (set.remove(oldEntity)) {
         *         if (newEntity != null) {
         *             set.add(newEntity);
         *         }
         *     }
         * 
         *     #require EntityLookup private final int minSection;
         *     #require EntityLookup private final int maxSection;
         *     final int minSection = entityLookup#minSection;
         *     final int maxSection = entityLookup#maxSection;
         * 
         *     // First check whether the new entity is already stored. If so, no ticking mode changes
         *     boolean isNewEntityStored = false;
         *     if (newEntity != null) {
         *         final BlockPosition pos = newEntity.blockPosition();
         *         final int sectionX = pos.getX() >> 4;
         *         final int sectionY = net.minecraft.util.MathHelper.clamp(pos.getY() >> 4, minSection, maxSection);
         *         final int sectionZ = pos.getZ() >> 4;
         * 
         *         // Runs just the chunk-adding logic. ID/UUID is done earlier.
         *         newEntity.moonrise$setSectionX(sectionX);
         *         newEntity.moonrise$setSectionY(sectionY);
         *         newEntity.moonrise$setSectionZ(sectionZ);
         * 
         *         ChunkEntitySlices slices = entityLookup.getChunk(sectionX, sectionZ);
         *         if (slices != null) {
         *             #require ca.spottedleaf.moonrise.patches.chunk_system.level.entity.ChunkEntitySlices private java.util.List<net.minecraft.world.entity.Entity> getAllEntities();
         *             java.util.List allEntities = slices#getAllEntities();
         *             java.util.Iterator iter = allEntities.iterator();
         *             while (iter.hasNext()) {
         *                 if (iter.next() == newEntity) {
         *                     isNewEntityStored = true;
         *                     break;
         *                 }
         *             }
         *         }
         *     }
         * 
         *     // bug: if chunk doesn't exist, error occurs
         *     //entitySliceManager.removeEntity(oldEntity);
         *     ChunkEntitySlices slices = entityLookup.getChunk(oldEntity.moonrise$getSectionX(), oldEntity.moonrise$getSectionZ());
         *     if (slices != null) {
         *         int oldEntitySectionY = oldEntity.moonrise$getSectionY();
         *         slices.removeEntity(oldEntity, oldEntitySectionY);
         *         if (slices.isEmpty()) {
         *             //TODO: Not done in the server now. Bug?
         *             //entityLookup.removeChunk(oldEntity.moonrise$getSectionX(), oldEntity.moonrise$getSectionZ());
         *         }
         *     }
         * 
         *     // Add new entity (might not be the same chunk)
         *     // Note: we cannot call addEntity as this initializes the tracker/other logic/events
         *     ChunkEntitySlices sectionOfEntity = null;
         *     if (newEntity != null) {
         *         final BlockPosition pos = newEntity.blockPosition();
         *         final int sectionX = pos.getX() >> 4;
         *         final int sectionY = net.minecraft.util.MathHelper.clamp(pos.getY() >> 4, minSection, maxSection);
         *         final int sectionZ = pos.getZ() >> 4;
         * 
         *         // Runs just the chunk-adding logic. ID/UUID is done earlier.
         *         newEntity.moonrise$setSectionX(sectionX);
         *         newEntity.moonrise$setSectionY(sectionY);
         *         newEntity.moonrise$setSectionZ(sectionZ);
         * 
         *         sectionOfEntity = entityLookup.getOrCreateChunk(sectionX, sectionZ);
         *         sectionOfEntity.addEntity(newEntity, sectionY);
         *     }
         * 
         *     // Update the "all entities" list
         *     #require EntityLookup private final ca.spottedleaf.moonrise.common.list.EntityList accessibleEntities;
         *     ca.spottedleaf.moonrise.common.list.EntityList lookupAccEntities = entityLookup#accessibleEntities;
         *     if (newEntity == null) {
         *         lookupAccEntities.remove(oldEntity);
         *     } else {
         *         // Swap the Entity in the internal array
         *         #require ca.spottedleaf.moonrise.common.list.EntityList protected final Int2IntOpenHashMap entityListEntityToIndex:entityToIndex;
         *         #require ca.spottedleaf.moonrise.common.list.EntityList protected Entity[] entityListEntities:entities;
         *         Int2IntOpenHashMap el_entityToIndex = lookupAccEntities#entityListEntityToIndex;
         *         Entity[] el_entities = lookupAccEntities#entityListEntities;
         *         int index = el_entityToIndex.get(newEntity.getId());
         *         if (index >= 0 && index < el_entities.length) {
         *             el_entities[index] = newEntity;
         *         }
         *     }
         * 
         *     // If isAlwaysTicking() of the old and new entity differs, we may have to stop/start ticking ourselves
         *     // This is because of a bug in the persistent entity section manager that, if isAlwaysTicking() is true,
         *     // the updateStatus function does not work anymore to update this state.
         *     // Only do this when the entity is first replaced. Not the second time around.
         *     if (!isNewEntityStored && sectionOfEntity != null) {
         *         boolean wasTicking = EntityLookup.getEntityStatus(oldEntity).isTicking();
         *         boolean isAlwaysTicking = newEntity.isAlwaysTicking();
         * 
         *         // Start ticking when section is not ticking, and we go from not always ticking
         *         // to always ticking. This is because this 'load' trigger already fired, and so it
         *         // presumes startTicking() was already performed.
         *         if (isAlwaysTicking && !wasTicking) {
         *             // Force a status change from TRACKED to TICKING, which starts ticking the entity
         *             // Do not cause a change from a visibility below TRACKED, that will break things
         *             entityLookup.entityStatusChange(newEntity, sectionOfEntity, Visibility.TRACKED, Visibility.TICKING,
         *                      false, true, false);
         *         }
         *     }
         * }
         */
        @Template.Generated("%REPLACE_IN_WORLD_STORAGE%")
        public abstract void replaceInWorldStorage(Object worldHandle, Object oldEntity, Object newEntity);

        /*
         * <REPLACE_IN_SECTION_STORAGE>
         * public static void replaceInSectionStorage(Entity oldEntity, Entity newEntity) {
         *     #require net.minecraft.world.entity.Entity private net.minecraft.world.level.entity.EntityInLevelCallback levelCallback;
         *     net.minecraft.world.level.entity.EntityInLevelCallback callback = oldEntity#levelCallback;
         *     if (callback != net.minecraft.world.level.entity.EntityInLevelCallback.NULL) {
         *         if (newEntity == null) {
         *             callback.onRemove(net.minecraft.world.entity.Entity$RemovalReason.DISCARDED);
         *         } else {
         *             #require EntityLookup.EntityCallback public final Entity entity;
         *             if ( callback#entity == oldEntity ) {
         *                 callback#entity = newEntity;
         *             }
         *         }
         *     }
         * }
         */
        @Template.Generated("%REPLACE_IN_SECTION_STORAGE%")
        public abstract void replaceInSectionStorage(Object oldEntity, Object newEntity);
    }

    /**
     * Handler for detecting when a chunk of entities is loaded
     */
    private static interface ChunkEntitiesLoadedHandler {
        void enable(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, CommonPlugin plugin);
        void hook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, World world);
        void unhook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, World world);
    }

    /**
     * Listens for the newly added ChunkEntitiesLoaded/Unloaded events in Spigot/Paper to detect
     * when entities are loaded/unloaded
     */
    private static class ChunkEntitiesLoadedUsingEventHandler implements ChunkEntitiesLoadedHandler, Listener {

        @Override
        public void enable(final EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, CommonPlugin plugin) {
            Class<? extends Event> entitiesLoadEventType = CommonUtil.unsafeCast(CommonUtil.getClass("org.bukkit.event.world.EntitiesLoadEvent"));
            Bukkit.getPluginManager().registerEvent(entitiesLoadEventType, this, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    onEntitiesLoaded(handler, (ChunkEvent) event);
                }
            }, plugin);

            Class<? extends Event> entitiesUnloadEventType = CommonUtil.unsafeCast(CommonUtil.getClass("org.bukkit.event.world.EntitiesUnloadEvent"));
            Bukkit.getPluginManager().registerEvent(entitiesUnloadEventType, this, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event event) throws EventException {
                    onEntitiesUnloaded(handler, (ChunkEvent) event);
                }
            }, plugin);
        }

        @Override
        public void hook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, World world) {
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, World world) {
        }

        private void onEntitiesLoaded(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesLoaded(event.getChunk());
        }

        private void onEntitiesUnloaded(EntityAddRemoveHandler_1_21_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesUnloaded(event.getChunk());
        }
    }
}
