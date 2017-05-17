package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityHumanHandle extends Template.Handle {
    public static final EntityHumanClass T = new EntityHumanClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityHumanHandle.class, "net.minecraft.server.EntityHuman");


    /* ============================================================================== */

    public static EntityHumanHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityHumanHandle handle = new EntityHumanHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EntityHumanClass extends Template.Class<EntityHumanHandle> {
    }
}
