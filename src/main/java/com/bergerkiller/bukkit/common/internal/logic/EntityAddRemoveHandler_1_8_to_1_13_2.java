package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.util.IntHashMapHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;

/**
 * From MC 1.8 to MC 1.13.2 there was an IWorldAccess listener list we could subscribe to.
 * We add our own custom listener hook to that list, to listen to entity add/remove events.
 */
public class EntityAddRemoveHandler_1_8_to_1_13_2 extends EntityAddRemoveHandler {
    private final Class<?> iWorldAccessType;
    private final SafeField<?> entitiesByIdField;
    private final FastField<Map<UUID, Object>> entitiesByUUIDField = new FastField<Map<UUID, Object>>();
    private final FastField<List<Object>> entityListField;
    private final SafeField<List<Object>> accessListField;
    private final SafeField<Collection<Object>> entityRemoveQueue;
    private final ChunkEntitySliceHandler chunkEntitySliceHandler;

    public EntityAddRemoveHandler_1_8_to_1_13_2() {
        this.iWorldAccessType = CommonUtil.getClass("net.minecraft.world.level.IWorldAccess");
        this.entitiesByIdField = SafeField.create(WorldHandle.T.getType(), "entitiesById", IntHashMapHandle.T.getType());
        this.entityListField = new FastField<List<Object>>();
        try {
            this.entityListField.init(WorldHandle.T.getType().getDeclaredField("entityList"));
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }

        // EntitiesByUUID is a Map on MC 1.13.2 and before
        try {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entitiesByUUID");
            entitiesByUUIDField.init(MPLType.getDeclaredField(WorldServerHandle.T.getType(), fieldName));
            if (!Map.class.isAssignableFrom(entitiesByUUIDField.getType())) {
                throw new IllegalStateException("Field not assignable to Map");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize WorldServer entitiesByUUID field: " + t.getMessage(), t);
            entitiesByUUIDField.initUnavailable("entitiesByUUID");
        }

        if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
            accessListField = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "v", List.class));
        } else {
            accessListField = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "u", List.class));
        }

        {
            java.lang.reflect.Field entityRemoveQueueField = null;
            try {
                if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                    entityRemoveQueueField = WorldHandle.T.getType().getDeclaredField("g");
                } else if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
                    entityRemoveQueueField = WorldHandle.T.getType().getDeclaredField("f");
                } else {
                    entityRemoveQueueField = WorldHandle.T.getType().getDeclaredField("g");
                }
                if (!Collection.class.isAssignableFrom(entityRemoveQueueField.getType())) {
                    Logging.LOGGER_REFLECTION.warning("Entity remove queue field " + entityRemoveQueueField.toString() + " is of incompatible type");
                    entityRemoveQueueField = null;
                }
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.warning("Entity remove queue field not found");
            }
            if (entityRemoveQueueField == null) {
                this.entityRemoveQueue = SafeField.createNull("World Entity Remove Queue");
            } else {
                this.entityRemoveQueue = new SafeField<Collection<Object>>(entityRemoveQueueField);
            }
        }

        // Chunk EntitySlice[] field, used when entities need to be swapped, or when removing
        // entities from a chunk/world
        this.chunkEntitySliceHandler = new ChunkEntitySliceHandler();
    }

    @Override
    public void onEnabled(CommonPlugin plugin) {
        super.onEnabled(plugin);
        plugin.register(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onChunkLoad(ChunkLoadEvent event) {
                notifyChunkEntitiesLoaded(event.getChunk());
            }
        });
    }

    @Override
    protected void hook(World world) {
        if (!this.accessListField.isValid()) {
            Logging.LOGGER_REFLECTION.warning("Failed to hook world " + world.getName()
                + " with entity listener hook, Entity Add/Remove events will not work");
            return;
        }

        List<Object> accessList = this.accessListField.get(Conversion.toWorldHandle.convert(world));
        if (accessList == null) {
            Logging.LOGGER_REFLECTION.warning("Failed to hook world " + world.getName()
                + " with entity listener hook (null), Entity Add/Remove events will not work");
            return;
        }

        for (Object o : accessList) {
            if (WorldListenerHook.get(o, WorldListenerHook.class) != null) {
                return; // Already hooked
            }
        }

        // Create a listener hook and add
        accessList.add(new WorldListenerHook(this, world).createInstance(this.iWorldAccessType));
    }

    @Override
    protected void unhook(World world) {
        if (!this.accessListField.isValid()) {
            return;
        }

        List<Object> accessList = this.accessListField.get(Conversion.toWorldHandle.convert(world));
        if (accessList != null) {
            Iterator<Object> iter = accessList.iterator();
            while (iter.hasNext()) {
                if (WorldListenerHook.get(iter.next(), WorldListenerHook.class) != null) {
                    iter.remove();
                }
            }
        }
    }

    @Override
    public void processEvents() {
        // Unused
    }

    @Override
    public void replace(EntityHandle oldEntity, EntityHandle newEntity) {
        WorldServerHandle world = oldEntity.getWorldServer();
        if (newEntity == null) {
            if (world != null) {
                world.removeEntity(oldEntity);
                world.getEntityTracker().stopTracking(oldEntity.getBukkitEntity());
            }
            return; // Works fine, no need to clean up any more
        }

        Object worldHandle = world.getRaw();

        // *** Entities By UUID Map ***
        {
            Map<UUID, Object> entitiesByUUID = entitiesByUUIDField.get(worldHandle);
            Object storedEntityHandle = entitiesByUUID.get(oldEntity.getUniqueID());
            if (storedEntityHandle != null && storedEntityHandle != newEntity.getRaw()) {
                if (!oldEntity.getUniqueID().equals(newEntity.getUniqueID())) {
                    entitiesByUUID.remove(oldEntity.getUniqueID());
                }
                entitiesByUUID.put(newEntity.getUniqueID(), newEntity.getRaw());
            }
        }

        // *** Entities by Id Map ***
        {
            IntHashMapHandle entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(worldHandle));
            Object storedEntityHandle = entitiesById.get(oldEntity.getId());
            if (storedEntityHandle != null && storedEntityHandle != newEntity.getRaw()) {
                if (oldEntity.getId() != newEntity.getId()) {
                    entitiesById.remove(oldEntity.getId());
                }
                entitiesById.put(newEntity.getId(), newEntity.getRaw());
            }
        }

        // *** Entities By UUID Map ***
        final Map<UUID, Object> entitiesByUUID =entitiesByUUIDField.get(worldHandle);
        entitiesByUUID.put(newEntity.getUniqueID(), newEntity.getRaw());

        // *** Entities by Id Map ***
        IntHashMapHandle entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(worldHandle));
        entitiesById.put(newEntity.getId(), newEntity.getRaw());

        // *** EntityTrackerEntry ***
        replaceInEntityTracker(newEntity.getId(), newEntity);
        if (newEntity.getVehicle() != null) {
            replaceInEntityTracker(newEntity.getVehicle().getId(), newEntity);
        }
        if (newEntity.getPassengers() != null) {
            for (EntityHandle passenger : newEntity.getPassengers()) {
                replaceInEntityTracker(passenger.getId(), newEntity);
            }
        }

        // *** World ***
        replaceInList(entityListField.get(worldHandle), newEntity);
        // Fixes for PaperSpigot
        // if (!Common.IS_PAPERSPIGOT_SERVER) {
        //     replaceInList(WorldRef.entityRemovalList.get(oldInstance.world), newInstance);
        // }

        // *** Entity Current Chunk ***
        final int chunkX = newEntity.getChunkX();
        final int chunkZ = newEntity.getChunkZ();
        Object chunkHandle = HandleConversion.toChunkHandle(WorldUtil.getChunk(newEntity.getWorld().getWorld(), chunkX, chunkZ));
        if (chunkHandle != null) {
            this.chunkEntitySliceHandler.replace(chunkHandle, oldEntity, newEntity);
        }

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        //com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldInstance.getRaw());
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
        this.chunkEntitySliceHandler.moveToChunk(entity);
    }

    /**
     * Handles all the method calls coming from a WorldListener instance that is hooked.
     * Most of it is ignored and discarded. We need it for Entity Add/Remove event handling.
     */
    @ClassHook.HookPackage("net.minecraft.server")
    public static class WorldListenerHook extends ClassHook<WorldListenerHook> {
        private final EntityAddRemoveHandler_1_8_to_1_13_2 handler;
        private final World world;

        public WorldListenerHook(EntityAddRemoveHandler_1_8_to_1_13_2 handler, World world) {
            this.handler = handler;
            this.world = world;
        }

        @Override
        protected Invoker<?> getCallback(Method method) {
            // First check if this method is hooked
            Invoker<?> result = super.getCallback(method);
            if (result != null) {
                return result;
            }

            // Allow methods declared in Object through
            if (method.getDeclaringClass().equals(Object.class)) {
                return null;
            }

            // All others are ignored
            return new NullInvoker<Object>(method.getReturnType());
        }

        @HookMethod("public void onEntityAdded:a(Entity entity)")
        public void onEntityAdded(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            handler.notifyAddedEarly(world, bEntity);
            CommonPlugin.getInstance().notifyAdded(world, bEntity);
        }

        @HookMethod("public void onEntityRemoved:b(Entity entity)")
        public void onEntityRemoved(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            handler.notifyRemoved(world, bEntity);

            // Fire remove from server event right away when the entity was removed using the remove queue (chunk unload logic)
            // Note: this is disabled on Paperspigot, because it caused a concurrent modification exception at runtime
            if (this.handler.entityRemoveQueue.isValid() && !Common.IS_PAPERSPIGOT_SERVER) {
                Collection<?> removeQueue = this.handler.entityRemoveQueue.get(HandleConversion.toWorldHandle(world));
                if (removeQueue != null && removeQueue.contains(entity)) {
                    CommonPlugin.getInstance().notifyRemovedFromServer(world, bEntity, true);
                }
            }
        }
    }

    private static void replaceInEntityTracker(int entityId, EntityHandle newInstance) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newInstance.getWorld().getWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entityId);
        if (entry != null) {

            EntityHandle tracker = entry.getState().getEntity();
            if (tracker != null && tracker.getId() == newInstance.getId()) {
                entry.setEntity(newInstance);
            }

            List<EntityHandle> passengers = new ArrayList<EntityHandle>(tracker.getPassengers());
            replaceInList(passengers, newInstance);
            tracker.setPassengers(passengers);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, EntityHandle entity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle) obj;
                if (obj_e.getIdField() == entity.getIdField()) {
                    iter.set(entity);
                }
            } else if (EntityHandle.T.isAssignableFrom(obj)) {
                int obj_id = EntityHandle.T.idField.getInteger(obj);
                if (obj_id == entity.getIdField()) {
                    iter.set(entity.getRaw());
                }
            }
        }
        return false;
    }
}
