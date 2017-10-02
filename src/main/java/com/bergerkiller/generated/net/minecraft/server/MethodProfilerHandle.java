package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MethodProfiler</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class MethodProfilerHandle extends Template.Handle {
    /** @See {@link MethodProfilerClass} */
    public static final MethodProfilerClass T = new MethodProfilerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MethodProfilerHandle.class, "net.minecraft.server.MethodProfiler");

    /* ============================================================================== */

    public static MethodProfilerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void begin(String label);
    public abstract void end();
    /**
     * Stores class members for <b>net.minecraft.server.MethodProfiler</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MethodProfilerClass extends Template.Class<MethodProfilerHandle> {
        public final Template.Method<Void> begin = new Template.Method<Void>();
        public final Template.Method<Void> end = new Template.Method<Void>();

    }

}

