package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import net.minecraft.server.v1_4_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_4_R1.EntityPlayer;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

public class PlayerInstanceRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(Common.NMS_ROOT + ".PlayerChunk");
	public static final FieldAccessor<ChunkCoordIntPair> location = TEMPLATE.getField("location");
	public static final FieldAccessor<List<EntityPlayer>> players = TEMPLATE.getField("b");
}
