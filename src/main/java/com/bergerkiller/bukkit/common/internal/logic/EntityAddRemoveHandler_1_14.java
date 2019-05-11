package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
public class EntityAddRemoveHandler_1_14 extends EntityAddRemoveHandler {

    @Override
    @SuppressWarnings("unchecked")
    public void hook(World world) {
        Object nmsWorldHandle = WorldHandle.fromBukkit(world).getRaw();
        Map<UUID, Object> base = (Map<UUID, Object>) WorldServerHandle.T.entitiesByUUID.raw.get(nmsWorldHandle);
        if (!(base instanceof EntitiesByUUIDMapHook)) {
            WorldServerHandle.T.entitiesByUUID.raw.set(nmsWorldHandle, new EntitiesByUUIDMapHook(world, base));
        }
    }

    @Override
    public void unhook(World world) {
        Object nmsWorldHandle = WorldHandle.fromBukkit(world).getRaw();
        Object value = WorldServerHandle.T.entitiesByUUID.raw.get(nmsWorldHandle);
        if (value instanceof EntitiesByUUIDMapHook) {
            WorldServerHandle.T.entitiesByUUID.raw.set(nmsWorldHandle, ((EntitiesByUUIDMapHook) value).getBase());
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

        public EntitiesByUUIDMapHook(World world, Map<UUID, Object> base) {
            this.world = world;
            this.base = base;
        }

        public Map<UUID, Object> getBase() {
            return this.base;
        }

        private void onAdded(Object entity) {
            CommonPlugin.getInstance().notifyAdded(this.world, WrapperConversion.toEntity(entity));
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
            if (value != null) {
                this.onAdded(value);
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
        // *** Entities By UUID Map ***
        final Map<UUID, EntityHandle> entitiesByUUID = WorldServerHandle.T.entitiesByUUID.get(oldEntity.getWorld().getRaw());
        entitiesByUUID.put(newEntity.getUniqueID(), newEntity);

        // *** Entities by Id Map ***
        final IntHashMap<Object> entitiesById = NMSWorld.entitiesById.get(oldEntity.getWorld().getRaw());
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
        replaceInList(newEntity.getWorldServer().getEntityList(), newEntity);
        // Fixes for PaperSpigot
        // if (!Common.IS_PAPERSPIGOT_SERVER) {
        //     replaceInList(WorldRef.entityRemovalList.get(oldInstance.world), newInstance);
        // }

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
        //com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldInstance.getRaw());
    }

    private static void replaceInEntityTracker(int entityId, EntityHandle newInstance) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newInstance.getWorld().getWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entityId);
        if (entry != null) {

            EntityHandle tracker = entry.getTracker();
            if (tracker != null && tracker.getId() == newInstance.getId()) {
                entry.setTracker(newInstance);
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
