package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class AttributeInstanceHandle extends Template.Handle {
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

    public static final class AttributeInstanceClass extends Template.Class<AttributeInstanceHandle> {
        public final Template.Method<Void> setValue = new Template.Method<Void>();
        public final Template.Method<Double> getValue = new Template.Method<Double>();

    }
}
