package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.server.PaperSpigotServer;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.IWorldAccessHandle;
import com.bergerkiller.generated.net.minecraft.server.IntHashMapHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.Invokable;
import com.bergerkiller.mountiplex.reflection.SafeField;

/**
 * From MC 1.8 to MC 1.13.2 there was an IWorldAccess listener list we could subscribe to.
 * We add our own custom listener hook to that list, to listen to entity add/remove events.
 */
public class EntityAddRemoveHandler_1_8_to_1_13_2 extends EntityAddRemoveHandler {
    private final SafeField<?> entitiesByIdField;
    private final SafeField<List<Object>> accessListField;
    private final SafeField<Collection<Object>> entityRemoveQueue;

    public EntityAddRemoveHandler_1_8_to_1_13_2() {
        this.entitiesByIdField = SafeField.create(WorldHandle.T.getType(), "entitiesById", IntHashMapHandle.T.getType());
        if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
            accessListField = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "v", List.class));
        } else {
            accessListField = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "u", List.class));
        }

        if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            Class<?> type;
            if (CommonBootstrap.getCommonServer() instanceof PaperSpigotServer) {
                type = Set.class;
            } else {
                type = List.class;
            }
            if (CommonBootstrap.evaluateMCVersion(">=", "1.13")) {
                this.entityRemoveQueue = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "g", type));
            } else {
                this.entityRemoveQueue = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "f", type));
            }
        } else {
            this.entityRemoveQueue = CommonUtil.unsafeCast(SafeField.create(WorldHandle.T.getType(), "g", List.class));
        }
    }

    @Override
    public void hook(World world) {
        List<Object> accessList = this.accessListField.get(Conversion.toWorldHandle.convert(world));
        for (Object o : accessList) {
            if (WorldListenerHook.get(o, WorldListenerHook.class) != null) {
                return; // Already hooked
            }
        }

        // Create a listener hook and add
        accessList.add(new WorldListenerHook(this, world).createInstance(IWorldAccessHandle.T.getType()));
    }

    @Override
    public void unhook(World world) {
        Iterator<Object> iter = this.accessListField.get(Conversion.toWorldHandle.convert(world)).iterator();
        while (iter.hasNext()) {
            if (WorldListenerHook.get(iter.next(), WorldListenerHook.class) != null) {
                iter.remove();
            }
        }
    }

    /**
     * Handles all the method calls coming from a WorldListener instance that is hooked.
     * Most of it is ignored and discarded. We need it for Entity Add/Remove event handling.
     */
    private static class WorldListenerHook extends ClassHook<WorldListenerHook> {
        private final EntityAddRemoveHandler_1_8_to_1_13_2 handler;
        private final World world;

        public WorldListenerHook(EntityAddRemoveHandler_1_8_to_1_13_2 handler, World world) {
            this.handler = handler;
            this.world = world;
        }

        @Override
        protected Invokable getCallback(Method method) {
            // First check if this method is hooked
            Invokable result = super.getCallback(method);
            if (result != null) {
                return result;
            }

            // Allow methods declared in Object through
            if (method.getDeclaringClass().equals(Object.class)) {
                return null;
            }

            // All others are ignored
            return new NullInvokable(method);
        }

        @HookMethod("public void onEntityAdded:???(Entity entity)")
        public void onEntityAdded(Object entity) {
            CommonPlugin.getInstance().notifyAdded(world, WrapperConversion.toEntity(entity));
        }

        @HookMethod("public void onEntityRemoved:???(Entity entity)")
        public void onEntityRemoved(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            CommonPlugin.getInstance().notifyRemoved(world, bEntity);

            // Fire remove from server event right away when the entity was removed using the remove queue (chunk unload logic)
            Collection<?> removeQueue = this.handler.entityRemoveQueue.get(HandleConversion.toWorldHandle(world));
            if (removeQueue != null && removeQueue.contains(entity)) {
                CommonPlugin.getInstance().notifyRemovedFromServer(world, bEntity, true);
            }
        }
    }

    @Override
    public void replace(World world, EntityHandle oldInstance, EntityHandle newInstance) {
        // *** Entities By UUID Map ***
        final Map<UUID, EntityHandle> entitiesByUUID = WorldServerHandle.T.entitiesByUUID.get(oldInstance.getWorld().getRaw());
        entitiesByUUID.put(newInstance.getUniqueID(), newInstance);

        // *** Entities by Id Map ***
        IntHashMapHandle entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(oldInstance.getWorld().getRaw()));
        entitiesById.put(newInstance.getId(), newInstance.getRaw());

        // *** EntityTrackerEntry ***
        replaceInEntityTracker(newInstance.getId(), newInstance);
        if (newInstance.getVehicle() != null) {
            replaceInEntityTracker(newInstance.getVehicle().getId(), newInstance);
        }
        if (newInstance.getPassengers() != null) {
            for (EntityHandle passenger : newInstance.getPassengers()) {
                replaceInEntityTracker(passenger.getId(), newInstance);
            }
        }

        // *** World ***
        replaceInList(newInstance.getWorldServer().getEntityList(), newInstance);
        // Fixes for PaperSpigot
        // if (!Common.IS_PAPERSPIGOT_SERVER) {
        //     replaceInList(WorldRef.entityRemovalList.get(oldInstance.world), newInstance);
        // }

        // *** Entity Current Chunk ***
        final int chunkX = newInstance.getChunkX();
        final int chunkY = newInstance.getChunkY();
        final int chunkZ = newInstance.getChunkZ();
        Object chunkHandle = HandleConversion.toChunkHandle(WorldUtil.getChunk(newInstance.getWorld().getWorld(), chunkX, chunkZ));
        if (chunkHandle != null) {
            final List<Object>[] entitySlices = ChunkHandle.T.entitySlices.get(chunkHandle);
            if (!replaceInList(entitySlices[chunkY], newInstance)) {
                for (int y = 0; y < entitySlices.length; y++) {
                    if (y != chunkY && replaceInList(entitySlices[y], newInstance)) {
                        break;
                    }
                }
            }
        }

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        //com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldInstance.getRaw());
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
