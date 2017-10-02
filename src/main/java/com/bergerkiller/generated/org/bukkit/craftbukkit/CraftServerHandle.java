package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.DedicatedPlayerListHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import org.bukkit.command.SimpleCommandMap;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftServerHandle extends Template.Handle {
    /** @See {@link CraftServerClass} */
    public static final CraftServerClass T = new CraftServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftServerHandle.class, "org.bukkit.craftbukkit.CraftServer");

    /* ============================================================================== */

    public static CraftServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public SimpleCommandMap getCommandMap() {
        return T.getCommandMap.invoke(getRaw());
    }

    public DedicatedPlayerListHandle getPlayerList() {
        return T.getPlayerList.invoke(getRaw());
    }

    public MinecraftServerHandle getServer() {
        return T.getServer.invoke(getRaw());
    }


    public static CraftServerHandle instance() {
        return createHandle(org.bukkit.Bukkit.getServer());
    }
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftServerClass extends Template.Class<CraftServerHandle> {
        public final Template.Method<SimpleCommandMap> getCommandMap = new Template.Method<SimpleCommandMap>();
        public final Template.Method.Converted<DedicatedPlayerListHandle> getPlayerList = new Template.Method.Converted<DedicatedPlayerListHandle>();
        public final Template.Method.Converted<MinecraftServerHandle> getServer = new Template.Method.Converted<MinecraftServerHandle>();

    }

}

