package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class WorldTypeHandle extends Template.Handle {
    public static final WorldTypeClass T = new WorldTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldTypeHandle.class, "net.minecraft.server.WorldType");


    /* ============================================================================== */

    public static final WorldTypeHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldTypeHandle handle = new WorldTypeHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getDimension() {
        return T.getDimension.invoke(instance);
    }

    public static final class WorldTypeClass extends Template.Class {
        public final Template.Method<Integer> getDimension = new Template.Method<Integer>();

    }
}
