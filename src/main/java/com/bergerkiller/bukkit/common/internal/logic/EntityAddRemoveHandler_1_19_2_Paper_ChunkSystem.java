package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.plugin.EventExecutor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
class EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastMethod<Object> getEntityLookupMethod = new FastMethod<Object>();
    private final FastField<Object> callbacksField = new FastField<Object>();
    private final List<LevelCallbackHandler> hooks = new ArrayList<LevelCallbackHandler>();
    private final AddRemoveHandlerLogic removeHandler;
    private final ChunkEntitiesLoadedHandler chunkEntitiesLoadedHandler;
    private final Class<?> levelCallbackHookType;

    public EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem() {
        // This is the interface we implement to receive events
        levelCallbackType = Resolver.loadClass("net.minecraft.world.level.entity.LevelCallback", false);
        if (levelCallbackType == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find LevelCallback class");
        }

        // Initialize accessor of PersistentEntitySectionManager WorldServer.entityManager
        Class<?> entityLookupClass = Resolver.loadClass("io.papermc.paper.chunk.system.entity.EntityLookup", false);
        if (entityLookupClass == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find EntityLookup class");
        }
        try {
            getEntityLookupMethod.init(WorldServerHandle.T.getType().getDeclaredMethod("getEntityLookup"));
            if (!entityLookupClass.isAssignableFrom(getEntityLookupMethod.getMethod().getReturnType())) {
                throw new IllegalStateException("Return type of getEntityLookup not assignable to EntityLookup");
            }
            getEntityLookupMethod.forceInitialization();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize WorldServer getEntityLookup method: " + t.getMessage(), t);
            getEntityLookupMethod.initUnavailable("getEntityLookup");
        }

        // Initialize the callbacks field of PersistentEntitySectionManager
        try {
            callbacksField.init(entityLookupClass.getDeclaredField("worldCallback"));
            if (!levelCallbackType.equals(callbacksField.getType())) {
                throw new IllegalStateException("Field not assignable to LevelCallback");
            }
            callbacksField.forceInitialization();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize EntityLookup worldCallback field: " + t.getMessage(), t);
            callbacksField.initUnavailable("worldCallback field not found");
        }

        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
        this.levelCallbackHookType = generateLevelCallbackHookType(callbacksField);
        this.chunkEntitiesLoadedHandler = new ChunkEntitiesLoadedUsingEventHandler();
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        getEntityLookupMethod.forceInitialization();
        callbacksField.forceInitialization();
        removeHandler.forceInitialization();
    }

    @Override
    public void processEvents() {
        for (LevelCallbackHandler hook : hooks) {
            hook.processEvents();
        }
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
        this.chunkEntitiesLoadedHandler.enable(this, plugin);
    }

    @Override
    protected void hook(World world) {
        Object sectionManager = getEntityLookupMethod.invoke(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        if (callbacks != null && callbacks.getClass() != this.levelCallbackHookType) {
            try {
                LevelCallbackHandler handler = new LevelCallbackHandler(this, world);
                Object hook = this.levelCallbackHookType.getConstructors()[0].newInstance(callbacks, handler);
                callbacksField.set(sectionManager, hook);
                hooks.add(handler);
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to instantiate a level hook callback", t);
            }
        }

        this.chunkEntitiesLoadedHandler.hook(this, world, sectionManager);
    }

    @Override
    protected void unhook(World world) {
        Object sectionManager = getEntityLookupMethod.invoke(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        if (callbacks != null && callbacks.getClass() == this.levelCallbackHookType) {
            // De-register the handler
            LevelCallbackHandler handler = SafeField.get(callbacks, "callback", LevelCallbackHandler.class);
            if (handler != null) {
                hooks.remove(handler);
            }

            // Retrieve the value of the 'base' field and restore the field value to that
            Object base = SafeField.get(callbacks, "base", callbacksField.getType());
            if (base != null) {
                callbacksField.set(sectionManager, base);
            }
        }

        this.chunkEntitiesLoadedHandler.unhook(this, world, sectionManager);
    }

    /**
     * WorldServer.entityManager -> PersistentEntitySectionManager.callbacks is hooked by a
     * runtime-generated hook that calls callbacks on this class type instance
     * to be notified of entities being added or removed
     */
    public static class LevelCallbackHandler {
        private final EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler;
        private final World world;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public LevelCallbackHandler(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world) {
            this.handler = handler;
            this.world = world;
        }

        public void onEntityRemoved(Object entityHandle) {
            Entity entity = WrapperConversion.toEntity(entityHandle);
            pendingAddEvents.remove(entity);
            handler.notifyRemoved(world, entity);
        }

        public void onEntityAdded(Object entityHandle) {
            Entity entity = WrapperConversion.toEntity(entityHandle);
            pendingAddEvents.add(entity);
            handler.notifyAddedEarly(world, entity);
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(world, pendingAddEvents.poll());
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

    private static Class<?> generateLevelCallbackHookType(FastField<Object> callbacksField) {
        try {
            Class<?> levelCallbackType = callbacksField.getType();
            String levelCallbackDesc = MPLType.getDescriptor(levelCallbackType);
            String bkcCallbackDesc = MPLType.getDescriptor(LevelCallbackHandler.class);

            ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(levelCallbackType)
                    .setFlags(ClassWriter.COMPUTE_MAXS)
                    .setExactName(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem.class.getName() + "$Hook")
                    .build();

            FieldVisitor fv;
            MethodVisitor mv;

            // Add field with the base instance
            {
                fv = cw.visitField(ACC_PUBLIC | ACC_FINAL, "base", levelCallbackDesc, null, null);
                fv.visitEnd();
            }

            // Add field with the callback instance
            {
                fv = cw.visitField(ACC_PUBLIC | ACC_FINAL, "callback", bkcCallbackDesc, null, null);
                fv.visitEnd();
            }

            // Add constructor initializing the base instance and the callback handler instance
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + levelCallbackDesc + bkcCallbackDesc + ")V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, cw.getInternalName(), "base", levelCallbackDesc);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitFieldInsn(PUTFIELD, cw.getInternalName(), "callback", bkcCallbackDesc);
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // Ask resolver what the method names are that we wish to implement
            final String entityRemovedMethodName, entityAddedMethodName;
            if (CommonBootstrap.evaluateMCVersion(">=", "1.18")) {
                entityRemovedMethodName = Resolver.resolveMethodName(levelCallbackType, "onTrackingEnd", new Class<?>[] { Object.class });
                entityAddedMethodName = Resolver.resolveMethodName(levelCallbackType, "onTrackingStart", new Class<?>[] { Object.class });
            } else {
                entityRemovedMethodName = Resolver.resolveMethodName(levelCallbackType, "a", new Class<?>[] { Object.class });
                entityAddedMethodName = Resolver.resolveMethodName(levelCallbackType, "b", new Class<?>[] { Object.class });
            }

            // Override all methods defined by the interface that must be implemented
            for (Method method : levelCallbackType.getMethods()) {
                String name = MPLType.getName(method);
                Class<?>[] params = method.getParameterTypes();
                final String callbackMethodName;
                if (name.equals(entityAddedMethodName) && params.length == 1 && params[0] == Object.class) {
                    callbackMethodName = "onEntityAdded";
                } else if (name.equals(entityRemovedMethodName) && params.length == 1 && params[0] == Object.class) {
                    callbackMethodName = "onEntityRemoved";
                } else {
                    callbackMethodName = null;
                }

                mv = cw.visitMethod(ACC_PUBLIC, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
                mv.visitCode();

                if (callbackMethodName != null) {
                    // Locate the Method instance in the callback class we're going to be calling
                    // This shouldn't ever fail.
                    Method callbackMethod = Stream.of(LevelCallbackHandler.class.getMethods())
                            .filter(m -> m.getName().equals(callbackMethodName))
                            .findFirst().get();

                    // Call the base method, store any return value in a temporary value on the stack
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", levelCallbackDesc);
                    int reg = MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, levelCallbackType, method);
                    if (method.getReturnType() != void.class) {
                        mv.visitVarInsn(MPLType.getOpcode(method.getReturnType(), ISTORE), reg);
                    }

                    // Then call our callback hook with the input arguments, discard return value
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "callback", bkcCallbackDesc);
                    MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, LevelCallbackHandler.class, callbackMethod);

                    // Finally, return the original return value of the base method (if any)
                    if (method.getReturnType() != void.class) {
                        mv.visitVarInsn(MPLType.getOpcode(method.getReturnType(), ILOAD), reg);
                    }
                } else {
                    // Call base method directly
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", levelCallbackDesc);
                    MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, levelCallbackType, method);
                }
                mv.visitInsn(MPLType.getOpcode(method.getReturnType(), IRETURN));
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // Generate!
            return cw.generate();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize level hook callback proxy class", t);
            return null;
        }
    }

    @Template.Optional
    @Template.Import("net.minecraft.server.level.WorldServer")
    @Template.Import("net.minecraft.world.level.chunk.Chunk")
    @Template.Import("net.minecraft.server.level.ChunkProviderServer")
    @Template.Import("net.minecraft.world.entity.Entity")
    @Template.Import("net.minecraft.util.EntitySlice")
    @Template.Import("net.minecraft.world.level.ChunkCoordIntPair")
    @Template.Import("net.minecraft.core.BlockPosition")
    @Template.Import("java.util.concurrent.locks.StampedLock")
    @Template.Import("it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap")
    @Template.Import("it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap")
    @Template.Import("it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap")
    @Template.Import("net.minecraft.world.level.entity.EntityTickList")
    @Template.InstanceType("io.papermc.paper.chunk.system.entity.EntityLookup")
    public static abstract class AddRemoveHandlerLogic extends Template.Class<Template.Handle> {

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
         *     EntityLookup entityLookup = world.getEntityLookup();
         * 
         *     // Make sure we have the entityByLock lock locked while modifying this data
         *     // Swap the entity in the by-id and by-uuid maps
         *     #require EntityLookup private final StampedLock entityByLock;
         *     StampedLock entityByLock = entityLookup#entityByLock;
         *     entityByLock.writeLock();
         *     try {
         *         #require EntityLookup private final Int2ReferenceOpenHashMap<Entity> entityById;
         *         Int2ReferenceOpenHashMap byIdMap = entityLookup#entityById;
         *         if (byIdMap.get(entityId) == oldEntity) {
         *             if (newEntity == null) {
         *                 byIdMap.remove(entityId);
         *             } else {
         *                 byIdMap.put(entityId, newEntity);
         *             }
         *         }
         * 
         *         #require EntityLookup private final Object2ReferenceOpenHashMap<UUID, Entity> entityByUUID;
         *         Object2ReferenceOpenHashMap byUUIDMap = entityLookup#entityByUUID;
         *         if (byUUIDMap.get(entityUUID) == oldEntity) {
         *             if (newEntity == null) {
         *                 byUUIDMap.remove(entityUUID);
         *             } else {
         *                 byUUIDMap.put(entityUUID, newEntity);
         *             }
         *         }
         *     } finally {
         *         entityByLock.tryUnlockWrite();
         *     }
         * 
         *     #require net.minecraft.server.level.WorldServer final net.minecraft.world.level.entity.EntityTickList entityTickList;
         *     net.minecraft.world.level.entity.EntityTickList tickList = world#entityTickList;
         * 
         *     #require net.minecraft.world.level.entity.EntityTickList private final io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;
         *     io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet set = tickList#entities;
         *     if (set.remove(oldEntity)) {
         *         if (newEntity != null) {
         *             set.add(newEntity);
         *         }
         *     }
         * 
         *     // bug: if chunk doesn't exist, error occurs
         *     //entitySliceManager.removeEntity(oldEntity);
         *     io.papermc.paper.world.ChunkEntitySlices slices = entityLookup.getChunk(oldEntity.sectionX, oldEntity.sectionZ);
         *     if (slices != null) {
         *         slices.removeEntity(oldEntity, oldEntity.sectionY);
         *         if (slices.isEmpty()) {
         *             //TODO: Not done in the server now. Bug?
         *             //entityLookup.removeChunk(oldEntity.sectionX, oldEntity.sectionZ);
         *         }
         *     }
         * 
         *     // Add new entity (might not be the same chunk)
         *     // Note: we cannot call addEntity as this initializes the tracker/other logic/events
         *     if (newEntity != null) {
         *         #require EntityLookup private final int minSection;
         *         #require EntityLookup private final int maxSection;
         *         final int minSection = entityLookup#minSection;
         *         final int maxSection = entityLookup#maxSection;
         * 
         *         final BlockPosition pos = newEntity.blockPosition();
         *         final int sectionX = pos.getX() >> 4;
         *         final int sectionY = net.minecraft.util.MathHelper.clamp(pos.getY() >> 4, minSection, maxSection);
         *         final int sectionZ = pos.getZ() >> 4;
         * 
         *         // Runs just the chunk-adding logic. ID/UUID is done earlier.
         *         newEntity.sectionX = sectionX;
         *         newEntity.sectionY = sectionY;
         *         newEntity.sectionZ = sectionZ;
         *         entityLookup.getOrCreateChunk(sectionX, sectionZ).addEntity(newEntity, sectionY);
         *     }
         * 
         *     // Update the "all entities" list
         *     #require EntityLookup private final com.destroystokyo.paper.util.maplist.EntityList accessibleEntities;
         *     com.destroystokyo.paper.util.maplist.EntityList lookupAccEntities = entityLookup#accessibleEntities;
         *     if (newEntity == null) {
         *         lookupAccEntities.remove(oldEntity);
         *     } else {
         *         // Swap the Entity in the internal array
         *         #require com.destroystokyo.paper.util.maplist.EntityList protected final Int2IntOpenHashMap entityListEntityToIndex:entityToIndex;
         *         #require com.destroystokyo.paper.util.maplist.EntityList protected Entity[] entityListEntities:entities;
         *         Int2IntOpenHashMap el_entityToIndex = lookupAccEntities#entityListEntityToIndex;
         *         Entity[] el_entities = lookupAccEntities#entityListEntities;
         *         int index = el_entityToIndex.get(newEntity.getId());
         *         if (index >= 0 && index < el_entities.length) {
         *             el_entities[index] = newEntity;
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
        void enable(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, CommonPlugin plugin);
        void hook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world, Object sectionManager);
        void unhook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world, Object sectionManager);
    }

    /**
     * Listens for the newly added ChunkEntitiesLoaded/Unloaded events in Spigot/Paper to detect
     * when entities are loaded/unloaded
     */
    private static class ChunkEntitiesLoadedUsingEventHandler implements ChunkEntitiesLoadedHandler, Listener {

        @Override
        public void enable(final EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, CommonPlugin plugin) {
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
        public void hook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world, Object sectionManager) {
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, World world, Object sectionManager) {
        }

        private void onEntitiesLoaded(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesLoaded(event.getChunk());
        }

        private void onEntitiesUnloaded(EntityAddRemoveHandler_1_19_2_Paper_ChunkSystem handler, ChunkEvent event) {
            handler.notifyChunkEntitiesUnloaded(event.getChunk());
        }
    }
}
