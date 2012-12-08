package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Queue;

import net.minecraft.server.LongHashMap;
import net.minecraft.server.PlayerManager;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

public class PlayerManagerRef {
	public static final ClassTemplate<PlayerManager> TEMPLATE = ClassTemplate.create(PlayerManager.class);
	public static final FieldAccessor<LongHashMap> playerInstances = TEMPLATE.getField("c");
	public static final FieldAccessor<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("d");
}
