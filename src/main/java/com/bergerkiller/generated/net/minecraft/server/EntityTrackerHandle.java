package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityTrackerHandle extends Template.Handle {
    public static final EntityTrackerClass T = new EntityTrackerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTrackerHandle.class, "net.minecraft.server.EntityTracker");


    /* ============================================================================== */

    public static EntityTrackerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityTrackerHandle handle = new EntityTrackerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EntityTrackerClass extends Template.Class {
    }
}
