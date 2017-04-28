package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import org.bukkit.World;

import java.util.Map;

public class CBCraftServer {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("CraftServer")
            .addImport("net.minecraft.server.MinecraftServer")
            .addImport("net.minecraft.server.DedicatedPlayerList")
            .addImport("org.bukkit.World");

    public static final FieldAccessor<Map<String, World>> worlds = T.selectField("private final Map<String, World> worlds");
    public static final MethodAccessor<Object> getServer = T.selectMethod("public MinecraftServer getServer()");
    public static final MethodAccessor<Object> getPlayerList = T.selectMethod("public DedicatedPlayerList getHandle()");
}
