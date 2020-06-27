package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.AttributeModifiable</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class AttributeModifiableHandle extends Template.Handle {
    /** @See {@link AttributeModifiableClass} */
    public static final AttributeModifiableClass T = new AttributeModifiableClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(AttributeModifiableHandle.class, "net.minecraft.server.AttributeModifiable", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static AttributeModifiableHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setValue(double value);
    public abstract double getValue();
    /**
     * Stores class members for <b>net.minecraft.server.AttributeModifiable</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeModifiableClass extends Template.Class<AttributeModifiableHandle> {
        public final Template.Method<Void> setValue = new Template.Method<Void>();
        public final Template.Method<Double> getValue = new Template.Method<Double>();

    }

}

