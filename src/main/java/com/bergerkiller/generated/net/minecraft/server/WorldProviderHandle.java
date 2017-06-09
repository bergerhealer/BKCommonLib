package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldProvider</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class WorldProviderHandle extends Template.Handle {
    /** @See {@link WorldProviderClass} */
    public static final WorldProviderClass T = new WorldProviderClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldProviderHandle.class, "net.minecraft.server.WorldProvider");

    /* ============================================================================== */

    public static WorldProviderHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldProviderHandle handle = new WorldProviderHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean isDarkWorld() {
        return T.isDarkWorld.invoke(instance);
    }

    /**
     * Stores class members for <b>net.minecraft.server.WorldProvider</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldProviderClass extends Template.Class<WorldProviderHandle> {
        public final Template.Method<Boolean> isDarkWorld = new Template.Method<Boolean>();

    }

}

