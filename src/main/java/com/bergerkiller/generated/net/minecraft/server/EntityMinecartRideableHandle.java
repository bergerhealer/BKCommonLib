package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartRideable</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityMinecartRideableHandle extends EntityMinecartAbstractHandle {
    /** @See {@link EntityMinecartRideableClass} */
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

    /**
     * Stores class members for <b>net.minecraft.server.EntityMinecartRideable</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartRideableClass extends Template.Class<EntityMinecartRideableHandle> {
    }

}

