package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket")
public abstract class ClientboundPlayerPositionPacketHandle extends PacketHandle {
    /** @see ClientboundPlayerPositionPacketClass */
    public static final ClientboundPlayerPositionPacketClass T = Template.Class.create(ClientboundPlayerPositionPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundPlayerPositionPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundPlayerPositionPacketHandle createNew(double x, double y, double z, float yaw, float pitch, double deltaX, double deltaY, double deltaZ, RelativeFlags relativeFlags, int teleportWaitTimer) {
        return T.createNew.invokeVA(x, y, z, yaw, pitch, deltaX, deltaY, deltaZ, relativeFlags, teleportWaitTimer);
    }

    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract RelativeFlags getRelativeFlags();
    public abstract int getTeleportWaitTimer();
    public static ClientboundPlayerPositionPacketHandle createNew(double x, double y, double z, float yaw, float pitch, double deltaX, double deltaY, double deltaZ, RelativeFlags relativeFlags) {
        return createNew(x, y, z, yaw, pitch, deltaX, deltaY, deltaZ, relativeFlags, 0);
    }

    public static ClientboundPlayerPositionPacketHandle createNew(double x, double y, double z, float yaw, float pitch, RelativeFlags relativeFlags) {
        return createNew(x, y, z, yaw, pitch, 0.0, 0.0, 0.0, relativeFlags, 0);
    }

    public static ClientboundPlayerPositionPacketHandle createRelative(double dx, double dy, double dz, float dyaw, float dpitch) {
        return createNew(dx, dy, dz, dyaw, dpitch, RelativeFlags.RELATIVE_POSITION_ROTATION);
    }

    public static ClientboundPlayerPositionPacketHandle createAbsolute(org.bukkit.Location location) {
        return createAbsolute(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static ClientboundPlayerPositionPacketHandle createAbsolute(double x, double y, double z, float yaw, float pitch) {
        return createNew(x, y, z, yaw, pitch, RelativeFlags.ABSOLUTE_POSITION);
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundPlayerPositionPacketClass extends Template.Class<ClientboundPlayerPositionPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundPlayerPositionPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundPlayerPositionPacketHandle>();

        public final Template.Method<Double> getX = new Template.Method<Double>();
        public final Template.Method<Double> getY = new Template.Method<Double>();
        public final Template.Method<Double> getZ = new Template.Method<Double>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method.Converted<RelativeFlags> getRelativeFlags = new Template.Method.Converted<RelativeFlags>();
        public final Template.Method<Integer> getTeleportWaitTimer = new Template.Method<Integer>();

    }

}

