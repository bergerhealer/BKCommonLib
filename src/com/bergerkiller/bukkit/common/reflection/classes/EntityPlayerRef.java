package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import net.minecraft.server.EntityPlayer;

import com.bergerkiller.bukkit.common.reflection.SafeField;

public class EntityPlayerRef {
	public static final SafeField<List<?>> chunkQueue = new SafeField<List<?>>(EntityPlayer.class, "chunkCoordIntPairQueue");
}
