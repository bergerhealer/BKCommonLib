package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.AttributeInstance</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class AttributeInstanceHandle extends Template.Handle {
    /** @See {@link AttributeInstanceClass} */
    public static final AttributeInstanceClass T = new AttributeInstanceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(AttributeInstanceHandle.class, "net.minecraft.server.AttributeInstance");

    /* ============================================================================== */

    public static AttributeInstanceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        AttributeInstanceHandle handle = new AttributeInstanceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void setValue(double value) {
        T.setValue.invoke(instance, value);
    }

    public double getValue() {
        return T.getValue.invoke(instance);
    }

    /**
     * Stores class members for <b>net.minecraft.server.AttributeInstance</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class AttributeInstanceClass extends Template.Class<AttributeInstanceHandle> {
        public final Template.Method<Void> setValue = new Template.Method<Void>();
        public final Template.Method<Double> getValue = new Template.Method<Double>();

    }

}

