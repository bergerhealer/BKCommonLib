package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityPlayerHandleHandle extends Template.Handle {
    public static final EntityPlayerHandleClass T = new EntityPlayerHandleClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityPlayerHandleHandle.class, "net.minecraft.server.EntityPlayerHandle");


    /* ============================================================================== */

    public static final EntityPlayerHandleHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityPlayerHandleHandle handle = new EntityPlayerHandleHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EntityPlayerHandleClass extends Template.Class {
    }
}
