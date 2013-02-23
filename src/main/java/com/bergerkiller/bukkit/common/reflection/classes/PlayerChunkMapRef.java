package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Queue;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class PlayerChunkMapRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerChunkMap");
	/**
	 * Type: LongHashMap
	 */
	public static final FieldAccessor<Object> playerInstances = TEMPLATE.getField("c");
	public static final FieldAccessor<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("d");
}
