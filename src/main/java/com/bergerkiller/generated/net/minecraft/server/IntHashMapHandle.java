package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IntHashMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class IntHashMapHandle extends Template.Handle {
    /** @See {@link IntHashMapClass} */
    public static final IntHashMapClass T = new IntHashMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IntHashMapHandle.class, "net.minecraft.server.IntHashMap");

    /* ============================================================================== */

    public static IntHashMapHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IntHashMapHandle handle = new IntHashMapHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.IntHashMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IntHashMapClass extends Template.Class<IntHashMapHandle> {
    }

}

