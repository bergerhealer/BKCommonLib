package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftServerRef {
	public static final Map<String, World> worlds = SafeField.get(Bukkit.getServer(), "worlds");
}
