package com.bergerkiller.bukkit.common.reflection;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.ClassTemplate;
import com.bergerkiller.bukkit.common.SafeField;

public class CraftTaskRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create("org.bukkit.craftbukkit.scheduler.CraftTask");
	public static final SafeField<Runnable> task = TEMPLATE.getField("task");
	public static final SafeField<Plugin> plugin = TEMPLATE.getField("plugin");
}
