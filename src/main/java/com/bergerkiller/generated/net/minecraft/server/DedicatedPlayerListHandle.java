package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class DedicatedPlayerListHandle extends PlayerListHandle {
    public static final DedicatedPlayerListClass T = new DedicatedPlayerListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DedicatedPlayerListHandle.class, "net.minecraft.server.DedicatedPlayerList");

    /* ============================================================================== */

    public static DedicatedPlayerListHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DedicatedPlayerListHandle handle = new DedicatedPlayerListHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class DedicatedPlayerListClass extends Template.Class<DedicatedPlayerListHandle> {
    }

}

