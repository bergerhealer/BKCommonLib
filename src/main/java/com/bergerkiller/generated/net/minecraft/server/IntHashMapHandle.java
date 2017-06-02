package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IntHashMapHandle extends Template.Handle {
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

    public static final class IntHashMapClass extends Template.Class<IntHashMapHandle> {
    }

}

