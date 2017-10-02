package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class WorldTypeHandle extends Template.Handle {
    /** @See {@link WorldTypeClass} */
    public static final WorldTypeClass T = new WorldTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldTypeHandle.class, "net.minecraft.server.WorldType");

    /* ============================================================================== */

    public static WorldTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getDimension() {
        return T.getDimension.invoke(getRaw());
    }

    /**
     * Stores class members for <b>net.minecraft.server.WorldType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldTypeClass extends Template.Class<WorldTypeHandle> {
        public final Template.Method<Integer> getDimension = new Template.Method<Integer>();

    }

}

