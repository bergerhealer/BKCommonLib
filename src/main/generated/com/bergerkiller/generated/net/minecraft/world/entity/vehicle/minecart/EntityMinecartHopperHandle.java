package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.EntityMinecartHopper</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.EntityMinecartHopper")
public abstract class EntityMinecartHopperHandle extends EntityMinecartAbstractHandle {
    /** @see EntityMinecartHopperClass */
    public static final EntityMinecartHopperClass T = Template.Class.create(EntityMinecartHopperClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityMinecartHopperHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean suckItems();
    public abstract boolean isSuckingEnabled();
    public abstract void setSuckingEnabled(boolean enabled);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.EntityMinecartHopper</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartHopperClass extends Template.Class<EntityMinecartHopperHandle> {
        public final Template.Method<Boolean> suckItems = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isSuckingEnabled = new Template.Method<Boolean>();
        public final Template.Method<Void> setSuckingEnabled = new Template.Method<Void>();

    }

}

