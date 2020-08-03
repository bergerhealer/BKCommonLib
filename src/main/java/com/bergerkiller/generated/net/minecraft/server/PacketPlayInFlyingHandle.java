package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInFlying</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayInFlying")
public abstract class PacketPlayInFlyingHandle extends PacketHandle {
    /** @See {@link PacketPlayInFlyingClass} */
    public static final PacketPlayInFlyingClass T = Template.Class.create(PacketPlayInFlyingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInFlyingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract double getX();
    public abstract void setX(double value);
    public abstract double getY();
    public abstract void setY(double value);
    public abstract double getZ();
    public abstract void setZ(double value);
    public abstract float getYaw();
    public abstract void setYaw(float value);
    public abstract float getPitch();
    public abstract void setPitch(float value);
    public abstract boolean isOnGround();
    public abstract void setOnGround(boolean value);
    public abstract boolean isHasPos();
    public abstract void setHasPos(boolean value);
    public abstract boolean isHasLook();
    public abstract void setHasLook(boolean value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInFlying</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInFlyingClass extends Template.Class<PacketPlayInFlyingHandle> {
        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        public final Template.Field.Boolean hasPos = new Template.Field.Boolean();
        public final Template.Field.Boolean hasLook = new Template.Field.Boolean();

    }

}

