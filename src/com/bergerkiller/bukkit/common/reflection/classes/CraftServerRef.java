package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftServerRef {
	public static final FieldAccessor<Map<String, World>> worlds = new SafeField<Map<String, World>>(CraftServer.class, "worlds");
}
