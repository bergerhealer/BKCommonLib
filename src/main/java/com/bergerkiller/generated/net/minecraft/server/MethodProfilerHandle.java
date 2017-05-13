package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class MethodProfilerHandle extends Template.Handle {
    public static final MethodProfilerClass T = new MethodProfilerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MethodProfilerHandle.class, "net.minecraft.server.MethodProfiler");


    /* ============================================================================== */

    public static final MethodProfilerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) throw new IllegalArgumentException("Handle instance can not be null");
        MethodProfilerHandle handle = new MethodProfilerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void begin(String label) {
        T.begin.invoke(instance, label);
    }

    public void end() {
        T.end.invoke(instance);
    }

    public static final class MethodProfilerClass extends Template.Class {
        public final Template.Method<Void> begin = new Template.Method<Void>();
        public final Template.Method<Void> end = new Template.Method<Void>();

    }
}
