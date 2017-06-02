package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityMinecartHopperHandle extends EntityMinecartAbstractHandle {
    public static final EntityMinecartHopperClass T = new EntityMinecartHopperClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartHopperHandle.class, "net.minecraft.server.EntityMinecartHopper");

    /* ============================================================================== */

    public static EntityMinecartHopperHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityMinecartHopperHandle handle = new EntityMinecartHopperHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getSuckingCooldown() {
        return T.suckingCooldown.getInteger(instance);
    }

    public void setSuckingCooldown(int value) {
        T.suckingCooldown.setInteger(instance, value);
    }

    public static final class EntityMinecartHopperClass extends Template.Class<EntityMinecartHopperHandle> {
        public final Template.Field.Integer suckingCooldown = new Template.Field.Integer();

    }

}

