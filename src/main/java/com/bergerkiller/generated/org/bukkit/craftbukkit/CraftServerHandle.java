package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.DedicatedPlayerListHandle;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.command.SimpleCommandMap;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;

public class CraftServerHandle extends Template.Handle {
    public static final CraftServerClass T = new CraftServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftServerHandle.class, "org.bukkit.craftbukkit.CraftServer");


    /* ============================================================================== */

    public static CraftServerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftServerHandle handle = new CraftServerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public SimpleCommandMap getCommandMap() {
        return T.getCommandMap.invoke(instance);
    }

    public DedicatedPlayerListHandle getPlayerList() {
        return T.getPlayerList.invoke(instance);
    }

    public MinecraftServerHandle getServer() {
        return T.getServer.invoke(instance);
    }

    public static CraftServerHandle instance() {
        return createHandle(org.bukkit.Bukkit.getServer());
    }

    public static final class CraftServerClass extends Template.Class<CraftServerHandle> {
        public final Template.Method<SimpleCommandMap> getCommandMap = new Template.Method<SimpleCommandMap>();
        public final Template.Method.Converted<DedicatedPlayerListHandle> getPlayerList = new Template.Method.Converted<DedicatedPlayerListHandle>();
        public final Template.Method.Converted<MinecraftServerHandle> getServer = new Template.Method.Converted<MinecraftServerHandle>();

    }
}
