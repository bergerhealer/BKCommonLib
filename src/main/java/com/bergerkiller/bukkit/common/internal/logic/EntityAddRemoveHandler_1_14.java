package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.IntHashMapHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.FastField;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
public class EntityAddRemoveHandler_1_14 extends EntityAddRemoveHandler {
    private final FastField<?> entitiesByIdField = new FastField<Object>();
    private final SafeField<Queue<Object>> entitiesToAddField;
    private final List<EntitiesByUUIDMapHook> hooks = new ArrayList<EntitiesByUUIDMapHook>();

    //Field 'entitiesById' in class net.minecraft.server.v1_15_R1.WorldServer is of type Int2ObjectLinkedOpenHashMap while we expect type Int2ObjectMap
    public EntityAddRemoveHandler_1_14() {
        try {
            entitiesByIdField.init(WorldServerHandle.T.getType().getDeclaredField("entitiesById"));
            if (!IntHashMapHandle.T.isAssignableFrom(entitiesByIdField.getType())) {
                throw new IllegalStateException("Field not assignable to IntHashmap");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize WorldServer entitiesById field: " + t.getMessage(), t);
            entitiesByIdField.initUnavailable("entitiesById");
        }
        this.entitiesToAddField = CommonUtil.unsafeCast(SafeField.create(WorldServerHandle.T.getType(), "entitiesToAdd", Queue.class));
    }

    @Override
    public void processEvents() {
        for (EntitiesByUUIDMapHook hook : hooks) {
            hook.processEvents();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void hook(World world) {
        Object nmsWorldHandle = WorldHandle.fromBukkit(world).getRaw();
        Map<UUID, Object> base = (Map<UUID, Object>) WorldServerHandle.T.entitiesByUUID.raw.get(nmsWorldHandle);
        if (!(base instanceof EntitiesByUUIDMapHook)) {
            EntitiesByUUIDMapHook hook = new EntitiesByUUIDMapHook(world, base);
            WorldServerHandle.T.entitiesByUUID.raw.set(nmsWorldHandle, hook);
            hooks.add(hook);
        }
    }

    @Override
    public void unhook(World world) {
        Object nmsWorldHandle = WorldHandle.fromBukkit(world).getRaw();
        Object value = WorldServerHandle.T.entitiesByUUID.raw.get(nmsWorldHandle);
        if (value instanceof EntitiesByUUIDMapHook) {
            WorldServerHandle.T.entitiesByUUID.raw.set(nmsWorldHandle, ((EntitiesByUUIDMapHook) value).getBase());
            hooks.remove(value);
        }
    }

    /**
     * This replaces the entitiesByUUID field in WorldServer
     * 
     * TODO: keySet(), values() and entrySet() are not hooked and removing from it
     *       is not listened to!
     */
    private static final class EntitiesByUUIDMapHook implements Map<UUID, Object> {
        private final World world;
        private final Map<UUID, Object> base;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public EntitiesByUUIDMapHook(World world, Map<UUID, Object> base) {
            this.world = world;
            this.base = base;
        }

        public Map<UUID, Object> getBase() {
            return this.base;
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(world, pendingAddEvents.poll());
            }
        }

        private void onAdded(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            CommonPlugin.getInstance().notifyAddedEarly(world, bEntity);
            pendingAddEvents.add(bEntity);
        }

        private void onRemoved(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            CommonPlugin.getInstance().notifyRemoved(this.world, bEntity);
        }

        @Override
        public int size() {
            return this.base.size();
        }

        @Override
        public boolean isEmpty() {
            return this.base.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.base.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.base.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return this.base.get(key);
        }

        @Override
        public Object put(UUID key, Object value) {
            Object rval = this.base.put(key, value);
            if (value != rval) {
                if (rval != null) {
                    this.onRemoved(rval);
                }
                if (value != null) {
                    this.onAdded(value);
                }
            }
            return rval;
        }

        @Override
        public Object remove(Object key) {
            Object removed = this.base.remove(key);
            if (removed != null) {
                this.onRemoved(removed);
            }
            return removed;
        }

        @Override
        public void putAll(Map<? extends UUID, ? extends Object> m) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                this.put((UUID) entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            if (this.base.isEmpty()) {
                return;
            }
            ArrayList<Object> old_values = new ArrayList<Object>(this.values());
            this.base.clear();
            for (Object removed : old_values) {
                this.onRemoved(removed);
            }
        }

        @Override
        public Set<UUID> keySet() {
            return this.base.keySet();
        }

        @Override
        public Collection<Object> values() {
            return this.base.values();
        }

        @Override
        public Set<java.util.Map.Entry<UUID, Object>> entrySet() {
            return this.base.entrySet();
        }
    }

    @Override
    public void replace(World world, EntityHandle oldEntity, EntityHandle newEntity) {
        // *** Remove from the entities to add queue ***
        Queue<Object> entitiesToAdd = this.entitiesToAddField.get(oldEntity.getWorld().getRaw());
        entitiesToAdd.remove(oldEntity.getRaw());

        // *** Entities By UUID Map ***
        final Map<UUID, EntityHandle> entitiesByUUID = WorldServerHandle.T.entitiesByUUID.get(oldEntity.getWorld().getRaw());
        if (!newEntity.equals(entitiesByUUID.get(newEntity.getUniqueID()))) {
            entitiesByUUID.put(newEntity.getUniqueID(), newEntity);
        }

        // *** Entities by Id Map ***
        IntHashMapHandle entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(oldEntity.getWorld().getRaw()));
        if (entitiesById.get(oldEntity.getId()) != newEntity.getRaw()) {
            entitiesById.put(oldEntity.getId(), newEntity.getRaw());
        }

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

        // *** Entity Current Chunk ***
        final int chunkX = newEntity.getChunkX();
        final int chunkY = newEntity.getChunkY();
        final int chunkZ = newEntity.getChunkZ();
        Object chunkHandle = HandleConversion.toChunkHandle(WorldUtil.getChunk(newEntity.getWorld().getWorld(), chunkX, chunkZ));
        if (chunkHandle != null) {
            final List<Object>[] entitySlices = ChunkHandle.T.entitySlices.get(chunkHandle);
            if (!replaceInList(entitySlices[chunkY], newEntity)) {
                for (int y = 0; y < entitySlices.length; y++) {
                    if (y != chunkY && replaceInList(entitySlices[y], newEntity)) {
                        break;
                    }
                }
            }
        }

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        // com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldEntity.getRaw());
    }

    private static void replaceInEntityTracker(int entityId, EntityHandle newInstance) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newInstance.getWorld().getWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entityId);
        if (entry != null) {

            EntityHandle tracker = entry.getEntity();
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
