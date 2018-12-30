package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldDataHandle extends Template.Handle {
    /** @See {@link WorldDataClass} */
    public static final WorldDataClass T = new WorldDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldDataHandle.class, "net.minecraft.server.WorldData", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static WorldDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract WorldTypeHandle getType();
    public abstract void setClearTimer(int ticks);
    /**
     * Stores class members for <b>net.minecraft.server.WorldData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldDataClass extends Template.Class<WorldDataHandle> {
        public final Template.Method.Converted<WorldTypeHandle> getType = new Template.Method.Converted<WorldTypeHandle>();
        public final Template.Method<Void> setClearTimer = new Template.Method<Void>();

    }

}

