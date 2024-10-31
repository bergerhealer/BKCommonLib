package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutPosition")
public abstract class PacketPlayOutPositionHandle extends PacketHandle {
    /** @see PacketPlayOutPositionClass */
    public static final PacketPlayOutPositionClass T = Template.Class.create(PacketPlayOutPositionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutPositionHandle createNew(int teleportWaitTimer, double x, double y, double z, float yaw, float pitch, RelativeFlags relativeFlags) {
        return T.createNew.invokeVA(teleportWaitTimer, x, y, z, yaw, pitch, relativeFlags);
    }

    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract RelativeFlags getRelativeFlags();
    public abstract int getTeleportWaitTimer();
    public static PacketPlayOutPositionHandle createRelative(double dx, double dy, double dz, float dyaw, float dpitch) {
        return createNew(dx, dy, dz, dyaw, dpitch, RelativeFlags.RELATIVE_POSITION_ROTATION);
    }

    public static PacketPlayOutPositionHandle createAbsolute(org.bukkit.Location location) {
        return createAbsolute(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static PacketPlayOutPositionHandle createAbsolute(double x, double y, double z, float yaw, float pitch) {
        return createNew(x, y, z, yaw, pitch, RelativeFlags.ABSOLUTE_POSITION);
    }

    public static PacketPlayOutPositionHandle createNew(double x, double y, double z, float yaw, float pitch, RelativeFlags relativeFlags) {
        return createNew(0, x, y, z, yaw, pitch, relativeFlags);
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutPositionClass extends Template.Class<PacketPlayOutPositionHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutPositionHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutPositionHandle>();

        public final Template.Method<Double> getX = new Template.Method<Double>();
        public final Template.Method<Double> getY = new Template.Method<Double>();
        public final Template.Method<Double> getZ = new Template.Method<Double>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method.Converted<RelativeFlags> getRelativeFlags = new Template.Method.Converted<RelativeFlags>();
        public final Template.Method<Integer> getTeleportWaitTimer = new Template.Method<Integer>();

    }

}

