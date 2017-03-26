package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

import org.bukkit.plugin.Plugin;

public class CBCraftTask {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("scheduler.CraftTask")
    		.addImport("org.bukkit.plugin.Plugin");
    
    public static final FieldAccessor<Runnable> task = T.selectField("private final Runnable task");
    public static final FieldAccessor<Plugin> plugin = T.selectField("private final Plugin plugin");
}
