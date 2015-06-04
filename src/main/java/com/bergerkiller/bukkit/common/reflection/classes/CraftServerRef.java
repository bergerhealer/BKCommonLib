package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.CBClassTemplate;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Map;

public class CraftServerRef {

    public static final ClassTemplate<?> TEMPLATE = CBClassTemplate.create("CraftServer");
    public static final Map<String, World> worlds = SafeField.get(Bukkit.getServer(), "worlds");
    public static final MethodAccessor<Object> getServer = TEMPLATE.getMethod("getServer");
    public static final MethodAccessor<Object> getPlayerList = TEMPLATE.getMethod("getHandle");
}
