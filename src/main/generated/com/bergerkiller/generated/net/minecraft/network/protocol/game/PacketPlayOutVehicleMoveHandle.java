package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutVehicleMove</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutVehicleMove")
public abstract class PacketPlayOutVehicleMoveHandle extends PacketHandle {
    /** @see PacketPlayOutVehicleMoveClass */
    public static final PacketPlayOutVehicleMoveClass T = Template.Class.create(PacketPlayOutVehicleMoveClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutVehicleMoveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutVehicleMoveHandle createNew(double posX, double posY, double posZ, float yaw, float pitch) {
        return T.createNew.invoke(posX, posY, posZ, yaw, pitch);
    }

    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract float getYaw();
    public abstract float getPitch();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutVehicleMove</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutVehicleMoveClass extends Template.Class<PacketPlayOutVehicleMoveHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutVehicleMoveHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutVehicleMoveHandle>();

        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();

    }

}

