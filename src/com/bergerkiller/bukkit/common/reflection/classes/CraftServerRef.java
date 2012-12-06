package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class CraftServerRef {
	public static final Map<String, World> worlds = SafeField.get(CommonUtil.getCraftServer(), "worlds");
}
