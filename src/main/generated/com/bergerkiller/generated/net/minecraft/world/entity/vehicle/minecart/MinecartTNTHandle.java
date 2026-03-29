package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.minecart.MinecartTNT</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.minecart.MinecartTNT")
public abstract class MinecartTNTHandle extends AbstractMinecartHandle {
    /** @see MinecartTNTClass */
    public static final MinecartTNTClass T = Template.Class.create(MinecartTNTClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MinecartTNTHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void explode(double damage);
    public abstract void prime();
    public abstract int getFuse();
    public abstract void setFuse(int value);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.minecart.MinecartTNT</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecartTNTClass extends Template.Class<MinecartTNTHandle> {
        public final Template.Field.Integer fuse = new Template.Field.Integer();

        public final Template.Method<Void> explode = new Template.Method<Void>();
        public final Template.Method<Void> prime = new Template.Method<Void>();

    }

}

