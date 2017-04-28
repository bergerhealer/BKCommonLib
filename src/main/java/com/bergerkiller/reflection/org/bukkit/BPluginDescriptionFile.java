package com.bergerkiller.reflection.org.bukkit;

import java.util.Map;

import org.bukkit.plugin.PluginDescriptionFile;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class BPluginDescriptionFile {
	public static final ClassTemplate<PluginDescriptionFile> T = ClassTemplate.create(PluginDescriptionFile.class);
	
    public static final FieldAccessor<Map<String, Map<String, Object>>> commands = 
    		T.selectField("private Map<String, Map<String, Object>> commands");
}
