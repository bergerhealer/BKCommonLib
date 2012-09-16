package com.bergerkiller.bukkit.common.reflection;

import java.util.Queue;

import net.minecraft.server.LongHashMap;
import net.minecraft.server.PlayerManager;

import com.bergerkiller.bukkit.common.ClassTemplate;
import com.bergerkiller.bukkit.common.SafeField;

public class PlayerManagerRef {
	public static final ClassTemplate<PlayerManager> TEMPLATE = ClassTemplate.create(PlayerManager.class);
	public static final SafeField<LongHashMap> playerInstances = TEMPLATE.getField("c");
	public static final SafeField<Queue<?>> dirtyBlockChunks = TEMPLATE.getField("d");
}
