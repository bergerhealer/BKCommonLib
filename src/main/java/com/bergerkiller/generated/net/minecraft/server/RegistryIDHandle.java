package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RegistryID</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class RegistryIDHandle extends Template.Handle {
    /** @See {@link RegistryIDClass} */
    public static final RegistryIDClass T = new RegistryIDClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegistryIDHandle.class, "net.minecraft.server.RegistryID", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static RegistryIDHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId(Object value);
    /**
     * Stores class members for <b>net.minecraft.server.RegistryID</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegistryIDClass extends Template.Class<RegistryIDHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

