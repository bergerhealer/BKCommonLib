package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket")
public abstract class ServerboundMoveVehiclePacketHandle extends PacketHandle {
    /** @see ServerboundMoveVehiclePacketClass */
    public static final ServerboundMoveVehiclePacketClass T = Template.Class.create(ServerboundMoveVehiclePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundMoveVehiclePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundMoveVehiclePacketHandle createNew(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        return T.createNew.invokeVA(posX, posY, posZ, yaw, pitch, onGround);
    }

    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract boolean isOnGround();
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundMoveVehiclePacketClass extends Template.Class<ServerboundMoveVehiclePacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundMoveVehiclePacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundMoveVehiclePacketHandle>();

        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Boolean> isOnGround = new Template.Method<Boolean>();

    }

}

