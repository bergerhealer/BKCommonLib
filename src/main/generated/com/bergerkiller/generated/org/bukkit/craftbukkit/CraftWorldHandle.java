package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.org.bukkit.WorldHandle;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.CraftWorld</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.CraftWorld")
public abstract class CraftWorldHandle extends WorldHandle {
    /** @see CraftWorldClass */
    public static final CraftWorldClass T = Template.Class.create(CraftWorldClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
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
        public final Template.Method<Object> getHandle = new Template.Method<Object>();

    }

}

