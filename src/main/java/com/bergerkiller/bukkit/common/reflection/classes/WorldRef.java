package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.List;

@SuppressWarnings("rawtypes")
public class WorldRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("World");
    private static final MethodAccessor<Server> getServer = TEMPLATE.getMethod("getServer");
    public static final FieldAccessor<List> tileEntityList = TEMPLATE.getField("tileEntityList");
    public static final FieldAccessor<List> entityRemovalList = TEMPLATE.getField("h");
    public static final FieldAccessor<World> bukkitWorld = TEMPLATE.getField("world");

    public static Server getServer(Object worldHandle) {
        return getServer.invoke(worldHandle);
    }
}
