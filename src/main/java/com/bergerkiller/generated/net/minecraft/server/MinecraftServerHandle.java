package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MinecraftServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class MinecraftServerHandle extends Template.Handle {
    /** @See {@link MinecraftServerClass} */
    public static final MinecraftServerClass T = new MinecraftServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MinecraftServerHandle.class, "net.minecraft.server.MinecraftServer", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static MinecraftServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getResourcePack();
    public abstract String getResourcePackHash();
    public abstract String getProperty(String key, String defaultValue);
    public abstract int getTicks();
    public abstract boolean isMainThread();

    private static MinecraftServerHandle _cached_instance = null;
    public static MinecraftServerHandle instance() {
        if (_cached_instance == null) {
            _cached_instance = com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle.instance().getServer();
        }
        return _cached_instance;
    }
    public abstract boolean isHasStopped();
    public abstract void setHasStopped(boolean value);
    /**
     * Stores class members for <b>net.minecraft.server.MinecraftServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecraftServerClass extends Template.Class<MinecraftServerHandle> {
        public final Template.Field.Boolean hasStopped = new Template.Field.Boolean();

        public final Template.Method<String> getResourcePack = new Template.Method<String>();
        public final Template.Method<String> getResourcePackHash = new Template.Method<String>();
        public final Template.Method<String> getProperty = new Template.Method<String>();
        public final Template.Method<Integer> getTicks = new Template.Method<Integer>();
        public final Template.Method<Boolean> isMainThread = new Template.Method<Boolean>();

    }

}

