package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

public class CraftTaskRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(Common.CB_ROOT + ".scheduler.CraftTask");
	public static final FieldAccessor<Runnable> task = TEMPLATE.getField("task");
	public static final FieldAccessor<Plugin> plugin = TEMPLATE.getField("plugin");
}
