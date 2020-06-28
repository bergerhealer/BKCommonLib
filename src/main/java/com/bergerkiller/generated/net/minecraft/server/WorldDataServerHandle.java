package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldDataServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldDataServerHandle extends Template.Handle {
    /** @See {@link WorldDataServerClass} */
    public static final WorldDataServerClass T = new WorldDataServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldDataServerHandle.class, "net.minecraft.server.WorldDataServer", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static WorldDataServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setClearTimer(int ticks);
    /**
     * Stores class members for <b>net.minecraft.server.WorldDataServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldDataServerClass extends Template.Class<WorldDataServerHandle> {
        public final Template.Method<Void> setClearTimer = new Template.Method<Void>();

    }

}

