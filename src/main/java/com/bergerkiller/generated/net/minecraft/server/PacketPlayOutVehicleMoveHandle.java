package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutVehicleMove</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.PacketPlayOutVehicleMove")
public abstract class PacketPlayOutVehicleMoveHandle extends PacketHandle {
    /** @See {@link PacketPlayOutVehicleMoveClass} */
    public static final PacketPlayOutVehicleMoveClass T = Template.Class.create(PacketPlayOutVehicleMoveClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutVehicleMoveHandle createHandle(Object handleInstance) {
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
     * Stores class members for <b>net.minecraft.server.PacketPlayOutVehicleMove</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutVehicleMoveClass extends Template.Class<PacketPlayOutVehicleMoveHandle> {
        public final Template.Field.Double posX = new Template.Field.Double();
        public final Template.Field.Double posY = new Template.Field.Double();
        public final Template.Field.Double posZ = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();

    }

}

