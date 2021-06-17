package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

import org.bukkit.World;

import java.util.Map;

public class CBCraftServer {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.CraftServer")
            .addImport("org.bukkit.World");

    public static final FieldAccessor<Map<String, World>> worlds = T.selectField("private final Map<String, World> worlds");
    public static final MethodAccessor<Object> getServer = CraftServerHandle.T.getServer.raw.toMethodAccessor();
    public static final MethodAccessor<Object> getPlayerList = CraftServerHandle.T.getPlayerList.raw.toMethodAccessor();
}
