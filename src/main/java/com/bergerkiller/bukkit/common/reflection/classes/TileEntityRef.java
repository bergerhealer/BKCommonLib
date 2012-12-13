package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_5.TileEntity;
import net.minecraft.server.v1_4_5.World;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class TileEntityRef {
	public static final FieldAccessor<World> world = new SafeField<World>(TileEntity.class, "world");
}
