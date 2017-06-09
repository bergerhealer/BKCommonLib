package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartHopper</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityMinecartHopperHandle extends EntityMinecartAbstractHandle {
    /** @See {@link EntityMinecartHopperClass} */
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

    /**
     * Stores class members for <b>net.minecraft.server.EntityMinecartHopper</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartHopperClass extends Template.Class<EntityMinecartHopperHandle> {
        public final Template.Field.Integer suckingCooldown = new Template.Field.Integer();

    }

}

