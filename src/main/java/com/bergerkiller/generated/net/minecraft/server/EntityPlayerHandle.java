package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;

public class EntityPlayerHandle extends EntityHumanHandle {
    public static final EntityPlayerClass T = new EntityPlayerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityPlayerHandle.class, "net.minecraft.server.EntityPlayer");


    /* ============================================================================== */

    public static EntityPlayerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityPlayerHandle handle = new EntityPlayerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EntityPlayerClass extends Template.Class<EntityPlayerHandle> {
    }
}
