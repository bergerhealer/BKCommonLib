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

    public static PacketPlayInVehicleMoveHandle createNew(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        return T.createNew.invokeVA(posX, posY, posZ, yaw, pitch, onGround);
    }

    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract boolean isOnGround();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInVehicleMove</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInVehicleMoveClass extends Template.Class<PacketPlayInVehicleMoveHandle> {
        public final Template.StaticMethod.Converted<PacketPlayInVehicleMoveHandle> createNew = new Template.StaticMethod.Converted<PacketPlayInVehicleMoveHandle>();

        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Boolean> isOnGround = new Template.Method<Boolean>();

    }

}

