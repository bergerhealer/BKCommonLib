package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MinecraftServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class MinecraftServerHandle extends Template.Handle {
    /** @See {@link MinecraftServerClass} */
    public static final MinecraftServerClass T = new MinecraftServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MinecraftServerHandle.class, "net.minecraft.server.MinecraftServer");

    /* ============================================================================== */

    public static MinecraftServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public boolean isRunning() {
        return T.isRunning.invoke(getRaw());
    }


    public static MinecraftServerHandle instance() {
        return com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle.instance().getServer();
    }
    public List<WorldServerHandle> getWorlds() {
        return T.worlds.get(getRaw());
    }

    public void setWorlds(List<WorldServerHandle> value) {
        T.worlds.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.MinecraftServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecraftServerClass extends Template.Class<MinecraftServerHandle> {
        public final Template.Field.Converted<List<WorldServerHandle>> worlds = new Template.Field.Converted<List<WorldServerHandle>>();

        public final Template.Method<Boolean> isRunning = new Template.Method<Boolean>();

    }

}

