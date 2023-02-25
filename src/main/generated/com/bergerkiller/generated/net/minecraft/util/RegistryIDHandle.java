package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.util.RegistryID</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.util.RegistryID")
public abstract class RegistryIDHandle extends Template.Handle {
    /** @see RegistryIDClass */
    public static final RegistryIDClass T = Template.Class.create(RegistryIDClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static RegistryIDHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId(Object value);
    /**
     * Stores class members for <b>net.minecraft.util.RegistryID</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegistryIDClass extends Template.Class<RegistryIDHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

