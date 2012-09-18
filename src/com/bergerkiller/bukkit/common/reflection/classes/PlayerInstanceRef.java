package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class PlayerInstanceRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create("net.minecraft.server.PlayerInstance");
	public static final SafeField<ChunkCoordIntPair> location = TEMPLATE.getField("location");
	public static final SafeField<List<EntityPlayer>> players = TEMPLATE.getField("b");
}
