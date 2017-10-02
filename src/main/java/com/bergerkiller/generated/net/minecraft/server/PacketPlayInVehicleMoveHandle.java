package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInVehicleMove</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayInVehicleMoveHandle extends PacketHandle {
    /** @See {@link PacketPlayInVehicleMoveClass} */
    public static final PacketPlayInVehicleMoveClass T = new PacketPlayInVehicleMoveClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInVehicleMoveHandle.class, "net.minecraft.server.PacketPlayInVehicleMove");

    /* ============================================================================== */

    public static PacketPlayInVehicleMoveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public double getPosX() {
        return T.posX.getDouble(getRaw());
    }

    public void setPosX(double value) {
        T.posX.setDouble(getRaw(), value);
    }

    public double getPosY() {
        return T.posY.getDouble(getRaw());
    }

    public void setPosY(double value) {
        T.posY.setDouble(getRaw(), value);
    }

    public double getPosZ() {
        return T.posZ.getDouble(getRaw());
    }

    public void setPosZ(double value) {
        T.posZ.setDouble(getRaw(), value);
    }

    public float getYaw() {
        return T.yaw.getFloat(getRaw());
    }

    public void setYaw(float value) {
        T.yaw.setFloat(getRaw(), value);
    }

    public float getPitch() {
        return T.pitch.getFloat(getRaw());
    }

    public void setPitch(float value) {
        T.pitch.setFloat(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInVehicleMove</b>.
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

