package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutVehicleMove</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayOutVehicleMoveHandle extends PacketHandle {
    /** @See {@link PacketPlayOutVehicleMoveClass} */
    public static final PacketPlayOutVehicleMoveClass T = new PacketPlayOutVehicleMoveClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutVehicleMoveHandle.class, "net.minecraft.server.PacketPlayOutVehicleMove");

    /* ============================================================================== */

    public static PacketPlayOutVehicleMoveHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutVehicleMoveHandle handle = new PacketPlayOutVehicleMoveHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public double getPosX() {
        return T.posX.getDouble(instance);
    }

    public void setPosX(double value) {
        T.posX.setDouble(instance, value);
    }

    public double getPosY() {
        return T.posY.getDouble(instance);
    }

    public void setPosY(double value) {
        T.posY.setDouble(instance, value);
    }

    public double getPosZ() {
        return T.posZ.getDouble(instance);
    }

    public void setPosZ(double value) {
        T.posZ.setDouble(instance, value);
    }

    public float getYaw() {
        return T.yaw.getFloat(instance);
    }

    public void setYaw(float value) {
        T.yaw.setFloat(instance, value);
    }

    public float getPitch() {
        return T.pitch.getFloat(instance);
    }

    public void setPitch(float value) {
        T.pitch.setFloat(instance, value);
    }

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

