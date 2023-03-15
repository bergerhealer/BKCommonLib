package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.RelativeMovementHandle;
import java.util.Set;

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

    public static PacketPlayOutPositionHandle createRelative(double dx, double dy, double dz, float dyaw, float dpitch) {
        return T.createRelative.invoke(dx, dy, dz, dyaw, dpitch);
    }

    public static PacketPlayOutPositionHandle createAbsolute(double x, double y, double z, float yaw, float pitch) {
        return T.createAbsolute.invoke(x, y, z, yaw, pitch);
    }


    public int getTeleportWaitTimer() {
        if (T.teleportWaitTimer.isAvailable()) {
            return T.teleportWaitTimer.getInteger(getRaw());
        } else {
            return 0;
        }
    }

    public void setTeleportWaitTimer(int ticks) {
        if (T.teleportWaitTimer.isAvailable()) {
            T.teleportWaitTimer.setInteger(getRaw(), ticks);
        }
    }

    public void setRotationRelative(boolean relative) {
        Set<RelativeMovementHandle> flags = getTeleportFlags();
        if (relative) {
            flags.add(RelativeMovementHandle.Y_ROT);
            flags.add(RelativeMovementHandle.X_ROT);
        } else {
            flags.remove(RelativeMovementHandle.Y_ROT);
            flags.remove(RelativeMovementHandle.X_ROT);
        }
    }

    public void setPositionRelative(boolean relative) {
        Set<RelativeMovementHandle> flags = getTeleportFlags();
        if (relative) {
            flags.add(RelativeMovementHandle.X);
            flags.add(RelativeMovementHandle.Y);
            flags.add(RelativeMovementHandle.Z);
        } else {
            flags.remove(RelativeMovementHandle.X);
            flags.remove(RelativeMovementHandle.Y);
            flags.remove(RelativeMovementHandle.Z);
        }
    }


    public static PacketPlayOutPositionHandle createAbsolute(org.bukkit.Location location) {
        return createAbsolute(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
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
    public abstract Set<RelativeMovementHandle> getTeleportFlags();
    public abstract void setTeleportFlags(Set<RelativeMovementHandle> value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutPositionClass extends Template.Class<PacketPlayOutPositionHandle> {
        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Converted<Set<RelativeMovementHandle>> teleportFlags = new Template.Field.Converted<Set<RelativeMovementHandle>>();
        @Template.Optional
        public final Template.Field.Integer teleportWaitTimer = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutPositionHandle> createRelative = new Template.StaticMethod.Converted<PacketPlayOutPositionHandle>();
        public final Template.StaticMethod.Converted<PacketPlayOutPositionHandle> createAbsolute = new Template.StaticMethod.Converted<PacketPlayOutPositionHandle>();

    }

}

