package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * <b>Deprecated: </b>Please move on to using {@link EntityTrackerHandle} instead
 */
@Deprecated
public class NMSEntityTracker {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EntityTracker");

    public static final FieldAccessor<World> world = EntityTrackerHandle.T.world.toFieldAccessor();
    public static final FieldAccessor<Set<Object>> trackerSet = CommonUtil.unsafeCast(EntityTrackerHandle.T.entries.raw.toFieldAccessor());
    public static final FieldAccessor<IntHashMap<Object>> trackedEntities = EntityTrackerHandle.T.trackedEntities.toFieldAccessor();
    public static final FieldAccessor<Integer> trackerViewDistance = EntityTrackerHandle.T.viewDistance.toFieldAccessor();

    private static final MethodAccessor<Void> track = T.selectMethod("public void track(Entity entity)");
    private static final MethodAccessor<Void> untrack = T.selectMethod("public void untrackEntity(Entity entity)");
    private static final MethodAccessor<Void> untrackPlayer = T.selectMethod("public void untrackPlayer(EntityPlayer entityplayer)");
    private static final MethodAccessor<Void> spawnEntities = T.selectMethod("public void a(EntityPlayer entityplayer, Chunk chunk)");

    public static void sendPacket(Object entityTrackerInstance, Entity entity, Object packet) {
        EntityTrackerHandle.T.sendPacketToEntity.raw.invoke(entity, Conversion.toEntityHandle.convert(entity), packet);
    }

    public static void spawnEntities(Object entityTrackerInstance, Player player, Chunk chunk) {
        spawnEntities.invoke(entityTrackerInstance, Conversion.toEntityHandle.convert(player), Conversion.toChunkHandle.convert(chunk));
    }

    public static void removeViewer(Object entityTrackerInstance, Player player) {
        untrackPlayer.invoke(entityTrackerInstance, Conversion.toEntityHandle.convert(player));
    }

    public static void startTracking(Object entityTrackerInstance, Entity entity) {
        track.invoke(entityTrackerInstance, Conversion.toEntityHandle.convert(entity));
    }

    public static void stopTracking(Object entityTrackerInstance, Entity entity) {
        untrack.invoke(entityTrackerInstance, Conversion.toEntityHandle.convert(entity));
    }

    public static Object getEntry(Object entityTrackerInstance, Entity entity) {
        return EntityTrackerHandle.createHandle(entityTrackerInstance).getTrackedEntities().get(entity.getEntityId());
    }

    public static Object getEntry(Object entityTrackerInstance, int id) {
        return EntityTrackerHandle.createHandle(entityTrackerInstance).getTrackedEntities().get(id);
    }

    public static void updatePlayer(Object entityTrackerInstance, Player player) {
        for (Object entry : trackerSet.get(entityTrackerInstance)) {
            if (NMSEntityTrackerEntry.tracker.get(entry) != player) {
                NMSEntityTrackerEntry.updatePlayer(entry, player);
            }
        }
    }

    public static Object setEntry(Object entityTrackerInstance, Entity entity, Object entityTrackerEntry) {
        Object previous;
        final int id = entity.getEntityId();
        // Set in tracked entities map
        IntHashMap<?> trackedMap = trackedEntities.get(entityTrackerInstance);
        previous = trackedMap.remove(id);
        trackedMap.put(id, entityTrackerEntry);

        // Replace in set
        Set<Object> trackers = trackerSet.get(entityTrackerInstance);
        trackers.remove(previous);
        trackers.add(entityTrackerEntry);
        return previous;
    }

    /**
     * Creates an entry with the right configuration without actually registering it inside the server.
     * This allows reading the network configuration such as view distance and update interval for an Entity.
     * 
     * @param entity to create a dummy EntityTrackerEntry for
     * @return dummy EntityTrackerEntry
     */
    public static Object createDummyEntry(Entity entity) {
        return CommonNMS.createDummyTrackerEntry(entity).getRaw();
    }

}
