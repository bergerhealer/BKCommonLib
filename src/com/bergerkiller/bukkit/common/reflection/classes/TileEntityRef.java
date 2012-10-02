package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

import com.bergerkiller.bukkit.common.reflection.SafeField;

public class TileEntityRef {
	public static final SafeField<World> world = new SafeField<World>(TileEntity.class, "world");
}
