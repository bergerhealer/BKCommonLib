package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Queue;
import net.minecraft.server.v1_4_6.LongHashMap;
import net.minecraft.server.v1_4_6.PlayerChunkMap;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

public class PlayerManagerRef {
	public static final ClassTemplate<PlayerChunkMap> TEMPLATE = ClassTemplate.create(PlayerChunkMap.class);
	public static final FieldAccessor<LongHashMap> playerInstances = TEMPLATE.getField("c");
	public static final FieldAccessor<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("d");
}
