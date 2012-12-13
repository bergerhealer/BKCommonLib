package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import net.minecraft.server.v1_4_5.EntityPlayer;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class EntityPlayerRef {
	public static final FieldAccessor<List<?>> chunkQueue = new SafeField<List<?>>(EntityPlayer.class, "chunkCoordIntPairQueue");
}
