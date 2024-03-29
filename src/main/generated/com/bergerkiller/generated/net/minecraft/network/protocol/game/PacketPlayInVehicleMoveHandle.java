package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInVehicleMove</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInVehicleMove")
public abstract class PacketPlayInVehicleMoveHandle extends PacketHandle {
    /** @see PacketPlayInVehicleMoveClass */
    public static final PacketPlayInVehicleMoveClass T = Template.Class.create(PacketPlayInVehicleMoveClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInVehicleMoveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract double getPosX();
    public abstract void setPosX(double value);
    public abstract double getPosY();
    public abstract void setPosY(double value);
    public abstract double getPosZ();
    public abstract void setPosZ(double value);
    public abstract float getYaw();
    public abstract void setYaw(float value);
    public abstract float getPitch();
    public abstract void setPitch(float value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInVehicleMove</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInVehicleMoveClass extends Template.Class<PacketPlayInVehicleMoveHandle> {
        public final Template.Field.Double posX = new Template.Field.Double();
        public final Template.Field.Double posY = new Template.Field.Double();
        public final Template.Field.Double posZ = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();

    }

}

