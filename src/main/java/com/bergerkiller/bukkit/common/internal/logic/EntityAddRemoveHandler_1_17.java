package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
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
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ReflectionUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
class EntityAddRemoveHandler_1_17 extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastField<Object> entityManagerField = new FastField<Object>();
    private final FastField<Object> callbacksField = new FastField<Object>();
    private final List<LevelCallbackHandler> hooks = new ArrayList<LevelCallbackHandler>();
    private final AddRemoveHandlerLogic removeHandler;
    private final ChunkEntitiesLoadedHandler chunkEntitiesLoadedHandler;
    private final Class<?> levelCallbackHookType;

    public EntityAddRemoveHandler_1_17() {
        // This is the interface we implement to receive events
        levelCallbackType = Resolver.loadClass("net.minecraft.world.level.entity.LevelCallback", false);
        if (levelCallbackType == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find LevelCallback class");
        }

        // Initialize accessor of PersistentEntitySectionManager WorldServer.entityManager
        Class<?> sectionManagerClass = Resolver.loadClass("net.minecraft.world.level.entity.PersistentEntitySectionManager", false);
        if (sectionManagerClass == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find PersistentEntitySectionManager class");
        }
        try {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entityManager");
            entityManagerField.init(WorldServerHandle.T.getType().getDeclaredField(fieldName));
            if (!sectionManagerClass.isAssignableFrom(entityManagerField.getType())) {
                throw new IllegalStateException("Field not assignable to PersistentEntitySectionManager");
            }
            entityManagerField.forceInitialization();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize WorldServer entityManager field: " + t.getMessage(), t);
            entityManagerField.initUnavailable("entityManager");
        }

        // Initialize the callbacks field of PersistentEntitySectionManager
        try {
            String fieldName = Resolver.resolveFieldName(sectionManagerClass, "callbacks");
            callbacksField.init(sectionManagerClass.getDeclaredField(fieldName));
            if (!levelCallbackType.equals(callbacksField.getType())) {
                throw new IllegalStateException("Field not assignable to LevelCallback");
            }
            callbacksField.forceInitialization();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager callbacks field: " + t.getMessage(), t);
            callbacksField.initUnavailable("callbacks field not found");
        }

        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
        this.levelCallbackHookType = generateLevelCallbackHookType(callbacksField);

        if (CommonUtil.getClass("org.bukkit.event.world.EntitiesLoadEvent") != null) {
            this.chunkEntitiesLoadedHandler = new ChunkEntitiesLoadedUsingEventHandler();
        } else {
            this.chunkEntitiesLoadedHandler = new ChunkEntitiesLoadedUsingHookHandler(sectionManagerClass);
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        entityManagerField.forceInitialization();
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
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
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
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
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
        private final EntityAddRemoveHandler_1_17 handler;
        private final World world;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public LevelCallbackHandler(EntityAddRemoveHandler_1_17 handler, World world) {
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
        removeHandler.replaceInChunkStorage(oldEntity.getRaw(), newEntityRaw);

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
                    .setExactName(EntityAddRemoveHandler_1_17.class.getName() + "$Hook")
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
    @Template.InstanceType("net.minecraft.world.level.entity.PersistentEntitySectionManager")
    public static abstract class AddRemoveHandlerLogic extends Template.Class<Template.Handle> {

        /*
         * <IS_CHUNK_ENTITIES_LOADED>
         * public static boolean isChunkEntitiesLoaded(WorldServer world, int cx, int cz) {
         * #if version >= 1.17.1
         *     #require net.minecraft.server.level.WorldServer public final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;
         * #else
         *     #require net.minecraft.server.level.WorldServer private final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;
         * #endif
         *     PersistentEntitySectionManager manager = world#entityManager;
         * #if version >= 1.18
         *     long key = ChunkCoordIntPair.asLong(cx, cz);
         *     return manager.areEntitiesLoaded(key);
         * #else
         *     long key = ChunkCoordIntPair.pair(cx, cz);
         *     return manager.a(key);
         * #endif
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
         *     #require net.minecraft.server.level.WorldServer private final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;
         *     #require net.minecraft.world.level.entity.PersistentEntitySectionManager private final EntityLookup visibleEntityStorage;
         *     PersistentEntitySectionManager entitySectionManager = world#entityManager;
         *     EntityLookup entityLookup = entitySectionManager#visibleEntityStorage;
         * 
         *     #require net.minecraft.world.level.entity.EntityLookup private final it.unimi.dsi.fastutil.ints.Int2ObjectMap byId;
         *     #require net.minecraft.world.level.entity.EntityLookup private final Map byUUID:byUuid;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap byIdMap = entityLookup#byId;
         *     Map byUUIDMap = entityLookup#byUUID;
         * 
         *     if (byIdMap.get(entityId) == oldEntity) {
         *         if (newEntity == null) {
         *             byIdMap.remove(entityId);
         *         } else {
         *             byIdMap.put(entityId, newEntity);
         *         }
         *     }
         *     if (byUUIDMap.get(entityUUID) == oldEntity) {
         *         if (newEntity == null) {
         *             byUUIDMap.remove(entityUUID);
         *         } else {
         *             byUUIDMap.put(entityUUID, newEntity);
         *         }
         *     }
         * 
         *     #require net.minecraft.server.level.WorldServer final net.minecraft.world.level.entity.EntityTickList entityTickList;
         *     EntityTickList tickList = world#entityTickList;
         * 
         * #if exists net.minecraft.world.level.entity.EntityTickList private final io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;
         *     // Tuinity was later ported to Paper with a new class package
         *     #require net.minecraft.world.level.entity.EntityTickList private final io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;
         *     io.papermc.paper.util.maplist.IteratorSafeOrderedReferenceSet set = tickList#entities;
         *     if (set.remove(oldEntity)) {
         *         if (newEntity != null) {
         *             set.add(newEntity);
         *         }
         *     }
         * #elseif exists net.minecraft.world.level.entity.EntityTickList private final com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;
         *     // Tuinity
         *     #require net.minecraft.world.level.entity.EntityTickList private final com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet<Entity> entities;
         *     com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet set = tickList#entities;
         *     if (set.remove(oldEntity)) {
         *         if (newEntity != null) {
         *             set.add(newEntity);
         *         }
         *     }
         * #else
         *     // Paper/Spigot
         *     #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> active;
         *     #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> passive;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListActive = tickList#active;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListPassive = tickList#passive;
         *     if (tickListActive.get(entityId) == oldEntity) {
         *         if (newEntity == null) {
         *             tickListActive.remove(entityId);
         *         } else {
         *             tickListActive.put(entityId, newEntity);
         *         }
         *     }
         *     if (tickListPassive.get(entityId) == oldEntity) {
         *         if (newEntity == null) {
         *             tickListPassive.remove(entityId);
         *         } else {
         *             tickListPassive.put(entityId, newEntity);
         *         }
         *     }
         * #endif
         * 
         *     // Tuinity, now in paper: entitySliceManager which stores entity lists by region index
         * #if exists net.minecraft.world.level.World protected final io.papermc.paper.world.EntitySliceManager entitySliceManager;
         *     #require net.minecraft.world.level.World protected final io.papermc.paper.world.EntitySliceManager entitySliceManager;
         *     io.papermc.paper.world.EntitySliceManager entitySliceManager = world#entitySliceManager;
         * 
         *     // Spigot bug: if chunk doesn't exist, error occurs
         *     //entitySliceManager.removeEntity(oldEntity);
         *     io.papermc.paper.world.ChunkEntitySlices slices = entitySliceManager.getChunk(oldEntity.sectionX, oldEntity.sectionZ);
         *     if (slices != null) {
         *         slices.removeEntity(oldEntity, oldEntity.sectionY);
         *         if (slices.isEmpty()) {
         *             entitySliceManager.removeChunk(oldEntity.sectionX, oldEntity.sectionZ);
         *         }
         *     }
         * 
         *     // Add new entity (might not be the same chunk)
         *     if (newEntity != null) {
         *         entitySliceManager.addEntity(newEntity);
         *     }
         * #endif
         * }
         */
        @Template.Generated("%REPLACE_IN_WORLD_STORAGE%")
        public abstract void replaceInWorldStorage(Object worldHandle, Object oldEntity, Object newEntity);

        /*
         * <REPLACE_IN_CHUNK_STORAGE>
         * public static void replaceInChunkStorage(Entity oldEntity, Entity newEntity) {
         *     // Paper: added an entities field to Chunk
         * #if exists net.minecraft.world.level.chunk.Chunk public final com.destroystokyo.paper.util.maplist.EntityList entities;
         *   #if version >= 1.18
         *     net.minecraft.core.BlockPosition pos = oldEntity.blockPosition();
         *     net.minecraft.world.level.World world = oldEntity.getLevel();
         *   #else
         *     net.minecraft.core.BlockPosition pos = oldEntity.getChunkCoordinates();
         *     net.minecraft.world.level.World world = oldEntity.getWorld();
         *   #endif
         *     if (world == null || pos == null) {
         *         return;
         *     }
         *     Chunk chunk = ((WorldServer)world).getChunkIfLoaded(pos.getX() >> 4, pos.getZ() >> 4);
         *     if (chunk == null) {
         *         return;
         *     }
         *     if (chunk.entities.remove(oldEntity)) {
         *         if (newEntity != null) {
         *             chunk.entities.add(newEntity);
         *         }
         *     }
         * #endif
         * }
         */
        @Template.Generated("%REPLACE_IN_CHUNK_STORAGE%")
        public abstract void replaceInChunkStorage(Object oldEntity, Object newEntity);

        /*
         * <REPLACE_IN_SECTION_STORAGE>
         * public static void replaceInSectionStorage(Entity oldEntity, Entity newEntity) {
         *     #require net.minecraft.world.entity.Entity private net.minecraft.world.level.entity.EntityInLevelCallback levelCallback;
         *     EntityInLevelCallback callback = oldEntity#levelCallback;
         *     if (callback != EntityInLevelCallback.NULL) {
         *         if (newEntity == null) {
         * #if version >= 1.18
         *             callback.onRemove(net.minecraft.world.entity.Entity$RemovalReason.DISCARDED);
         * #else
         *             callback.a(net.minecraft.world.entity.Entity$RemovalReason.DISCARDED);
         * #endif
         *         } else {
         *             #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private final EntityAccess entity;
         *             if ( callback#entity == oldEntity ) {
         *                 callback#entity = newEntity;
         *             }
         *         }
         * 
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private EntitySection currentSection;
         * #if version <= 1.17.1 && exists net.minecraft.world.level.entity.PersistentEntitySectionManager.a private PersistentEntitySectionManager b;
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private PersistentEntitySectionManager callbackPESM:b;
         * #else
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private PersistentEntitySectionManager callbackPESM:this$0;
         * #endif
         *         EntitySection section = callback#currentSection;
         * 
         *         boolean checkStartTickingNewEntity = false;
         * 
         *         #require net.minecraft.world.level.entity.EntitySection private final net.minecraft.util.EntitySlice storage;
         *         EntitySlice slice = section#storage;
         *         List sliceList = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.cbEntitySliceToList(slice);
         *         java.util.ListIterator iter = sliceList.listIterator();
         *         while (iter.hasNext()) {
         *             if (iter.next() == oldEntity) {
         *                 if (newEntity == null) {
         *                     iter.remove();
         *                 } else {
         *                     iter.set(newEntity);
         *                     checkStartTickingNewEntity = true;
         *                 }
         *             }
         *         }
         * 
         *         // If isAlwaysTicking() of the old and new entity differs, we may have to stop/start ticking ourselves
         *         // This is because of a bug in the persistent entity section manager that, if isAlwaysTicking() is true,
         *         // the updateStatus function does not work anymore to update this state.
         *         if (checkStartTickingNewEntity) {
         * #if version >= 1.18
         *             boolean wasAlwaysTicking = oldEntity.isAlwaysTicking();
         *             boolean isAlwaysTicking = newEntity.isAlwaysTicking();
         *             boolean sectionTicking = section.getStatus().isTicking();
         * #else
         *             boolean wasAlwaysTicking = oldEntity.dn();
         *             boolean isAlwaysTicking = newEntity.dn();
         *             boolean sectionTicking = section.c().a();
         * #endif
         * 
         *             // Start ticking when section is not ticking, and we go from not always ticking
         *             // to always ticking. This is because this 'load' trigger already fired, and so it
         *             // presumes startTicking() was already performed.
         *             if (isAlwaysTicking && !wasAlwaysTicking && !sectionTicking) {
         * #if version >= 1.18
         *                 #require net.minecraft.world.level.entity.PersistentEntitySectionManager void pesmStartTicking:startTicking(T t0);
         * #else
         *                 #require net.minecraft.world.level.entity.PersistentEntitySectionManager void pesmStartTicking:c(T t0);
         * #endif
         *                 PersistentEntitySectionManager pesm = callback#callbackPESM;
         *                 pesm#pesmStartTicking(newEntity);
         *             }
         * 
         *             // Stop ticking when section is not ticking, and we go from always ticking to
         *             // not always ticking. For same reason as before.
         *             // TODO: Implement this? No use case as of right now.
         *             //if (!isAlwaysTicking && wasAlwaysTicking && !sectionTicking) {
         *             //}
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
        void enable(EntityAddRemoveHandler_1_17 handler, CommonPlugin plugin);
        void hook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager);
        void unhook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager);
    }

    /**
     * Injects itself into the chunkLoadStatuses Int2ObjectMap to listen for put events,
     * to detect when a chunk has its entities loaded. Uses standard ChunkUnloadEvent
     * for the unload event to avoid too many issues, as this really is a hack.
     */
    private static class ChunkEntitiesLoadedUsingHookHandler implements ChunkEntitiesLoadedHandler {
        private final FastField<Object> chunkLoadStatusesField = new FastField<Object>();

        public ChunkEntitiesLoadedUsingHookHandler(Class<?> sectionManagerClass) {
            // Initialize the chunkLoadStatuses field of PersistentEntitySectionManager
            try {
                String fieldName = Resolver.resolveFieldName(sectionManagerClass, "chunkLoadStatuses");
                chunkLoadStatusesField.init(sectionManagerClass.getDeclaredField(fieldName));
                if (!CommonUtil.getClass("it.unimi.dsi.fastutil.longs.Long2ObjectMap").equals(chunkLoadStatusesField.getType())) {
                    throw new IllegalStateException("Field not assignable to Long2ObjectMap");
                }
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager chunkLoadStatuses field: " + t.getMessage(), t);
                chunkLoadStatusesField.initUnavailable("chunkLoadStatuses field not found");
            }
        }

        @Override
        public void enable(EntityAddRemoveHandler_1_17 handler, CommonPlugin plugin) {
            plugin.register(new Listener() {
                @EventHandler(priority = EventPriority.LOWEST)
                public void onChunkUnload(ChunkUnloadEvent event) {
                    handler.notifyChunkEntitiesUnloaded(event.getChunk());
                }
            });
        }

        @Override
        public void hook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
            Object chunkLoadStatuses = chunkLoadStatusesField.get(sectionManager);
            if (ClassHook.get(chunkLoadStatuses, ChunkLoadStatusHook.class) == null) {
                ChunkLoadStatusHook hook = new ChunkLoadStatusHook(handler, world);
                Object hooked = hook.hook(chunkLoadStatuses);
                swapValueRecursive(hooked, chunkLoadStatuses, hooked, 4);
                chunkLoadStatusesField.set(sectionManager, hooked);
            }
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
            Object chunkLoadStatuses = chunkLoadStatusesField.get(sectionManager);
            if (ClassHook.get(chunkLoadStatuses, ChunkLoadStatusHook.class) != null) {
                Object base = ClassHook.unhook(chunkLoadStatuses);
                swapValueRecursive(base, chunkLoadStatuses, base, 4);
                chunkLoadStatusesField.set(sectionManager, base);
            }
        }

        /**
         * Performs a reflection deep inspection of a Class object, goes by all fields
         * declared inside, and checks if any of those fields have a value matching
         * the expected. If so, it is set to the replacement instead.
         *
         * @param start Start value in the recursive operation
         * @param expected The value to find
         * @param replacement Value to replace it with
         * @param depthLimit Max recursive depth, to prevent stack overflow (self-referential objects and such)
         */
        private static void swapValueRecursive(Object start, Object expected, Object replacement, int depthLimit) {
            if (depthLimit <= 0) {
                return; // Protect against stack overflow
            }
            ReflectionUtil.getAllClasses(start.getClass())
                .filter(c -> c.getName().contains("it.unimi.dsi.fastutil"))
                .flatMap(c -> Stream.of(c.getDeclaredFields()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> {
                    Class<?> type = f.getType();
                    return !type.isPrimitive() && !type.isArray();
                })
                .map(f -> {
                    f.setAccessible(true);
                    try {
                        Object currentValue = f.get(start);
                        if (currentValue == expected) {
                            f.set(start, replacement);
                        } else {
                            return currentValue;
                        }
                    } catch (Throwable t) {}
                    return null;
                })
                .filter(Objects::nonNull)
                .forEachOrdered(object -> {
                    swapValueRecursive(object, expected, replacement, depthLimit - 1);
                });
        }

        /**
         * Hooks the "chunkLoadStatuses" field in PersistentEntitySectionManager to listen
         * for when a chunk is officially fully loaded with entities
         */
        public static class ChunkLoadStatusHook extends ClassHook<ChunkLoadStatusHook> {
            private final EntityAddRemoveHandler_1_17 handler;
            private final World world;

            public ChunkLoadStatusHook(EntityAddRemoveHandler_1_17 handler, World world) {
                this.handler = handler;
                this.world = world;
            }

            @ClassHook.HookMethod("public V put(long key, V value)")
            public void onPut(Object rawKey, Object value) {
                base.onPut(rawKey, value);

                if (value.toString().equals("LOADED")) {
                    long key = ((Long) rawKey).longValue();
                    int cx = (int) (key & 0xFFFFFFFFL);
                    int cz = (int) (key >>> 32 & 0xFFFFFFFFL);
                    Chunk chunk = WorldUtil.getChunk(world, cx, cz);
                    if (chunk != null) {
                        this.handler.notifyChunkEntitiesLoaded(chunk);
                    }
                }
            }
        }
    }

    /**
     * Listens for the newly added ChunkEntitiesLoaded/Unloaded events in Spigot/Paper to detect
     * when entities are loaded/unloaded
     */
    private static class ChunkEntitiesLoadedUsingEventHandler implements ChunkEntitiesLoadedHandler, Listener {

        @Override
        public void enable(final EntityAddRemoveHandler_1_17 handler, CommonPlugin plugin) {
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
        public void hook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
        }

        @Override
        public void unhook(EntityAddRemoveHandler_1_17 handler, World world, Object sectionManager) {
        }

        private void onEntitiesLoaded(EntityAddRemoveHandler_1_17 handler, ChunkEvent event) {
            handler.notifyChunkEntitiesLoaded(event.getChunk());
        }

        private void onEntitiesUnloaded(EntityAddRemoveHandler_1_17 handler, ChunkEvent event) {
            handler.notifyChunkEntitiesUnloaded(event.getChunk());
        }
    }
}
