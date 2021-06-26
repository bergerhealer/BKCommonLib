package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
public class EntityAddRemoveHandler_1_17 extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastField<Object> entityManagerField = new FastField<Object>();
    private final FastField<Object> callbacksField = new FastField<Object>();
    private final List<LevelCallbackInterceptor> hooks = new ArrayList<LevelCallbackInterceptor>();
    private final AddRemoveHandlerLogic removeHandler;

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
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager callbacks field: " + t.getMessage(), t);
            callbacksField.initUnavailable("callbacks");
        }

        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);
    }

    @Override
    public void processEvents() {
        for (LevelCallbackInterceptor hook : hooks) {
            hook.processEvents();
        }
    }

    @Override
    protected void hook(World world) {
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        if (ClassInterceptor.get(callbacks, LevelCallbackInterceptor.class) == null) {
            LevelCallbackInterceptor interceptor = new LevelCallbackInterceptor(this, callbacks, world);
            callbacksField.set(sectionManager, interceptor.createInstance(levelCallbackType));
            hooks.add(interceptor);
        }
    }

    @Override
    protected void unhook(World world) {
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        LevelCallbackInterceptor interceptor = ClassInterceptor.get(callbacks, LevelCallbackInterceptor.class);
        if (interceptor != null) {
            callbacksField.set(sectionManager, interceptor.base);
            hooks.remove(interceptor);
        }
    }

    /**
     * WorldServer.entityManager -> PersistentEntitySectionManager.callbacks is hooked by this
     * class to be notified of entities being added or removed
     */
    public static class LevelCallbackInterceptor extends ClassInterceptor {
        private final EntityAddRemoveHandler_1_17 handler;
        private final Object base;
        private final World world;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public LevelCallbackInterceptor(EntityAddRemoveHandler_1_17 handler, Object base, World world) {
            this.handler = handler;
            this.base = base;
            this.world = world;
        }

        @Override
        protected Invoker<?> getCallback(Method ref_method) {
            // Create a default invoker that calls this same method on the original callback
            final FastMethod<Object> method = new FastMethod<Object>();
            method.init(ref_method);
            final Invoker<Object> baseInvoker = new Invoker<Object>() {
                @Override
                public Object invokeVA(Object instance, Object... args) {
                    return method.invokeVA(base, args);
                }
            };

            // Override certain methods
            String name = MPLType.getName(ref_method);
            Class<?>[] params = ref_method.getParameterTypes();

            // onCreated:f is fired when new entities are spawned, but not when existing
            // entities are loaded when chunks are loaded. For our purposes, it is not the right
            // callback event.

            // Overrides onTrackingStart:b - it is here we want to fire entity add events
            if (name.equals("b") && params.length == 1 && params[0] == Object.class) {
                return new Invoker<Object>() {
                    @Override
                    public Object invokeVA(Object instance, Object... args) {
                        // Callback
                        onEntityAdded(WrapperConversion.toEntity(args[0]));

                        // Execute normal invoking logic
                        return baseInvoker.invokeVA(base, args);
                    }
                };
            }

            // Overrides onTrackingEnd:a - it is here we want to fire entity remove events
            // The onDestroy callback only fires when entities are permanently removed
            if (name.equals("a") && params.length == 1 && params[0] == Object.class) {
                return new Invoker<Object>() {
                    @Override
                    public Object invokeVA(Object instance, Object... args) {
                        // Callback
                        onEntityRemoved(WrapperConversion.toEntity(args[0]));

                        // Execute normal invoking logic
                        return baseInvoker.invokeVA(base, args);
                    }
                };
            }
            
            return baseInvoker;
        }

        public void onEntityRemoved(Entity entity) {
            pendingAddEvents.remove(entity);
            handler.notifyRemoved(world, entity);
        }

        public void onEntityAdded(Entity entity) {
            pendingAddEvents.add(entity);
            handler.notifyAddedEarly(world, entity);
            
            if (DebugUtil.getBooleanValue("a", false)) {
                CommonUtil.nextTick(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        DebugUtil.logInstances(HandleConversion.toEntityHandle(entity));
                    }
                    
                });
            }
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(world, pendingAddEvents.poll());
            }
        }
    }

    @Override
    public void replace(World world, EntityHandle oldEntity, EntityHandle newEntity) {
        // *** EntityTrackerEntry ***
        replaceInEntityTracker(oldEntity, oldEntity, newEntity);
        if (oldEntity.getVehicle() != null) {
            replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
        }
        if (oldEntity.getPassengers() != null) {
            for (EntityHandle passenger : oldEntity.getPassengers()) {
                replaceInEntityTracker(passenger, oldEntity, newEntity);
            }
        }

        removeHandler.replaceInWorldStorage(HandleConversion.toWorldHandle(world), oldEntity.getRaw(), newEntity.getRaw());
        removeHandler.replaceInSectionStorage(oldEntity.getRaw(), newEntity.getRaw());

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldEntity.getRaw());
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
    @Template.Import("net.minecraft.server.level.ChunkProviderServer")
    @Template.Import("net.minecraft.world.entity.Entity")
    @Template.Import("net.minecraft.util.EntitySlice")
    @Template.InstanceType("net.minecraft.world.level.entity.PersistentEntitySectionManager")
    public static abstract class AddRemoveHandlerLogic extends Template.Class<Template.Handle> {

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
         *     EntityLookup entityLookup = world#entityManager#visibleEntityStorage;
         * 
         *     #require net.minecraft.world.level.entity.EntityLookup private final it.unimi.dsi.fastutil.ints.Int2ObjectMap byId;
         *     #require net.minecraft.world.level.entity.EntityLookup private final Map byUUID:byUuid;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap byIdMap = entityLookup#byId;
         *     Map byUUIDMap = entityLookup#byUUID;
         * 
         *     if (byIdMap.get(entityId) == oldEntity) {
         *         byIdMap.put(entityId, newEntity);
         *     }
         *     if (byUUIDMap.get(entityUUID) == oldEntity) {
         *         byUUIDMap.put(entityUUID, newEntity);
         *     }
         * 
         *     #require net.minecraft.server.level.WorldServer final net.minecraft.world.level.entity.EntityTickList entityTickList;
         *     #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> active;
         *     #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> passive;
         *     EntityTickList tickList = world#entityTickList;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListActive = tickList#active;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListPassive = tickList#passive;
         *     if (tickListActive.get(entityId) == oldEntity) {
         *         tickListActive.put(entityId, newEntity);
         *     }
         *     if (tickListPassive.get(entityId) == oldEntity) {
         *         tickListPassive.put(entityId, newEntity);
         *     }
         * }
         */
        @Template.Generated("%REPLACE_IN_WORLD_STORAGE%")
        public abstract void replaceInWorldStorage(Object worldHandle, Object oldEntity, Object newEntity);

        /*
         * <REPLACE_IN_SECTION_STORAGE>
         * public static void replaceInStorage(Entity oldEntity, Entity newEntity) {
         *     #require net.minecraft.world.entity.Entity private net.minecraft.world.level.entity.EntityInLevelCallback levelCallback;
         *     EntityInLevelCallback callback = oldEntity#levelCallback;
         *     if (callback != EntityInLevelCallback.NULL) {
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private final EntityAccess entity;
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private EntitySection currentSection;
         *         if ( callback#entity == oldEntity ) {
         *             callback#entity = newEntity;
         *         }
         *         EntitySection section = callback#currentSection;
         * 
         *         #require net.minecraft.world.level.entity.EntitySection private final net.minecraft.util.EntitySlice storage;
         *         EntitySlice slice = section#storage;
         *         List sliceList = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.cbEntitySliceToList(slice);
         *         java.util.ListIterator iter = sliceList.listIterator();
         *         while (iter.hasNext()) {
         *             if (iter.next() == oldEntity) {
         *                 iter.set(newEntity);
         *             }
         *         }
         *     }
         * }
         */
        @Template.Generated("%REPLACE_IN_SECTION_STORAGE%")
        public abstract void replaceInSectionStorage(Object oldEntity, Object newEntity);
    }
}
