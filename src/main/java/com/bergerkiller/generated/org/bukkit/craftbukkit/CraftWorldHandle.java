package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftWorld</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftWorldHandle extends Template.Handle {
    /** @See {@link CraftWorldClass} */
    public static final CraftWorldClass T = new CraftWorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftWorldHandle.class, "org.bukkit.craftbukkit.CraftWorld");

    /* ============================================================================== */

    public static CraftWorldHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getHandle();
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.CraftWorld</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftWorldClass extends Template.Class<CraftWorldHandle> {
        public final Template.Method.Converted<Object> getHandle = new Template.Method.Converted<Object>();

    }

}

