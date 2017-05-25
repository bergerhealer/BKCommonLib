package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityMinecartRideableHandle extends EntityMinecartAbstractHandle {
    public static final EntityMinecartRideableClass T = new EntityMinecartRideableClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartRideableHandle.class, "net.minecraft.server.EntityMinecartRideable");


    /* ============================================================================== */

    public static EntityMinecartRideableHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityMinecartRideableHandle handle = new EntityMinecartRideableHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class EntityMinecartRideableClass extends Template.Class<EntityMinecartRideableHandle> {
    }
}
