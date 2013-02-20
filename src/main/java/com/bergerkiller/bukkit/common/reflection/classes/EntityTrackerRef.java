package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class EntityTrackerRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityTracker");
	public static final FieldAccessor<Set<Object>> trackerSet = TEMPLATE.getField("b");
	private static final MethodAccessor<Void> updateMethod = TEMPLATE.getMethod("a", EntityPlayerRef.TEMPLATE.getType(), ChunkRef.TEMPLATE.getType());

	public static void updatePlayer(Object entityTrackerInstance, Player player, Chunk chunk) {
		updateMethod.invoke(entityTrackerInstance, Conversion.toEntityHandle.convert(player), Conversion.toChunkHandle.convert(chunk));
	}
}
