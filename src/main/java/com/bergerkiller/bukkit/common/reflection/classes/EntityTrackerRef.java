package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

public class EntityTrackerRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityTracker");
	public static final FieldAccessor<Set<Object>> trackerSet = TEMPLATE.getField("c");
	public static final FieldAccessor<IntHashMap<Object>> trackedEntities = TEMPLATE.getField("trackedEntities").translate(ConversionPairs.intHashMap);
	private static final MethodAccessor<Void> spawnEntities = TEMPLATE.getMethod("a", EntityPlayerRef.TEMPLATE.getType(), ChunkRef.TEMPLATE.getType());
	private static final MethodAccessor<Void> track = TEMPLATE.getMethod("track", EntityRef.TEMPLATE.getType());
	private static final MethodAccessor<Void> untrack = TEMPLATE.getMethod("untrackEntity", EntityRef.TEMPLATE.getType());
	private static final MethodAccessor<Void> sendPacket = TEMPLATE.getMethod("sendPacketToEntity", EntityRef.TEMPLATE.getType(), PacketType.DEFAULT.getType());
	private static final MethodAccessor<Void> untrackPlayer = TEMPLATE.getMethod("untrackPlayer", EntityPlayerRef.TEMPLATE.getType());

	public static void sendPacket(Object entityTrackerInstance, Entity entity, Object packet) {
		sendPacket.invoke(entityTrackerInstance, Conversion.toEntityHandle.convert(entity), packet);
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
		return trackedEntities.get(entityTrackerInstance).get(entity.getEntityId());
	}

	public static void updatePlayer(Object entityTrackerInstance, Player player) {
		for (Object entry : trackerSet.get(entityTrackerInstance)) {
			if (EntityTrackerEntryRef.tracker.get(entry) != player) {
				EntityTrackerEntryRef.updatePlayer(entry, player);
			}
		}
	}

	public static Object setEntry(Object entityTrackerInstance, Entity entity, Object entityTrackerEntry) {
		Object previous;
		final int id = entity.getEntityId();
		// Set in tracked entities map
		IntHashMap<Object> trackedMap = trackedEntities.get(entityTrackerInstance);
		previous = trackedMap.remove(id);
		trackedMap.put(id, entityTrackerEntry);

		// Replace in set
		Set<Object> trackers = trackerSet.get(entityTrackerInstance);
		trackers.remove(previous);
		trackers.add(entityTrackerEntry);
		return previous;
	}
}
