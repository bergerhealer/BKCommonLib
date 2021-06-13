package com.bergerkiller.generated.net.minecraft.world.entity.vehicle;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.EntityMinecartRideable</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.EntityMinecartRideable")
public abstract class EntityMinecartRideableHandle extends EntityMinecartAbstractHandle {
    /** @See {@link EntityMinecartRideableClass} */
    public static final EntityMinecartRideableClass T = Template.Class.create(EntityMinecartRideableClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityMinecartRideableHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.EntityMinecartRideable</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartRideableClass extends Template.Class<EntityMinecartRideableHandle> {
    }

}

