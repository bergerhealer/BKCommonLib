package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DedicatedPlayerList</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class DedicatedPlayerListHandle extends PlayerListHandle {
    /** @See {@link DedicatedPlayerListClass} */
    public static final DedicatedPlayerListClass T = new DedicatedPlayerListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DedicatedPlayerListHandle.class, "net.minecraft.server.DedicatedPlayerList", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static DedicatedPlayerListHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.DedicatedPlayerList</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DedicatedPlayerListClass extends Template.Class<DedicatedPlayerListHandle> {
    }

}

