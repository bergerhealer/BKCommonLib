package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.IdMapper</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.core.IdMapper")
public abstract class IdMapperHandle extends Template.Handle {
    /** @see IdMapperClass */
    public static final IdMapperClass T = Template.Class.create(IdMapperClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IdMapperHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId(Object value);
    /**
     * Stores class members for <b>net.minecraft.core.IdMapper</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IdMapperClass extends Template.Class<IdMapperHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

