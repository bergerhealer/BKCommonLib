package com.bergerkiller.reflection.org.bukkit;

import java.util.Collection;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class BSimplePluginManager {
	public static final ClassTemplate<SimplePluginManager> T = ClassTemplate.create(SimplePluginManager.class);
    public static final FieldAccessor<Collection<Plugin>> plugins = T.getField("plugins", List.class);
}
