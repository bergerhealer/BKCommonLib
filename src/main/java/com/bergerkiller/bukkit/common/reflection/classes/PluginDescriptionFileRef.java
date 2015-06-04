package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Map;

public class PluginDescriptionFileRef {

    public static final FieldAccessor<Map<String, Map<String, Object>>> commands = new SafeField<Map<String, Map<String, Object>>>(PluginDescriptionFile.class, "commands");
}
