package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutPosition")
public abstract class PacketPlayOutPositionHandle extends PacketHandle {
    /** @See {@link PacketPlayOutPositionClass} */
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
        Set<EnumPlayerTeleportFlagsHandle> flags = getTeleportFlags();
        if (relative) {
            flags.add(EnumPlayerTeleportFlagsHandle.Y_ROT);
            flags.add(EnumPlayerTeleportFlagsHandle.X_ROT);
        } else {
            flags.remove(EnumPlayerTeleportFlagsHandle.Y_ROT);
            flags.remove(EnumPlayerTeleportFlagsHandle.X_ROT);
        }
    }

    public void setPositionRelative(boolean relative) {
        Set<EnumPlayerTeleportFlagsHandle> flags = getTeleportFlags();
        if (relative) {
            flags.add(EnumPlayerTeleportFlagsHandle.X);
            flags.add(EnumPlayerTeleportFlagsHandle.Y);
            flags.add(EnumPlayerTeleportFlagsHandle.Z);
        } else {
            flags.remove(EnumPlayerTeleportFlagsHandle.X);
            flags.remove(EnumPlayerTeleportFlagsHandle.Y);
            flags.remove(EnumPlayerTeleportFlagsHandle.Z);
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
    public abstract Set<EnumPlayerTeleportFlagsHandle> getTeleportFlags();
    public abstract void setTeleportFlags(Set<EnumPlayerTeleportFlagsHandle> value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutPositionClass extends Template.Class<PacketPlayOutPositionHandle> {
        public final Template.Field.Double x = new Template.Field.Double();
        public final Template.Field.Double y = new Template.Field.Double();
        public final Template.Field.Double z = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Converted<Set<EnumPlayerTeleportFlagsHandle>> teleportFlags = new Template.Field.Converted<Set<EnumPlayerTeleportFlagsHandle>>();
        @Template.Optional
        public final Template.Field.Integer teleportWaitTimer = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutPositionHandle> createRelative = new Template.StaticMethod.Converted<PacketPlayOutPositionHandle>();
        public final Template.StaticMethod.Converted<PacketPlayOutPositionHandle> createAbsolute = new Template.StaticMethod.Converted<PacketPlayOutPositionHandle>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPosition.EnumPlayerTeleportFlags</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.server.PacketPlayOutPosition.EnumPlayerTeleportFlags")
    public abstract static class EnumPlayerTeleportFlagsHandle extends Template.Handle {
        /** @See {@link EnumPlayerTeleportFlagsClass} */
        public static final EnumPlayerTeleportFlagsClass T = Template.Class.create(EnumPlayerTeleportFlagsClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        public static final EnumPlayerTeleportFlagsHandle X = T.X.getSafe();
        public static final EnumPlayerTeleportFlagsHandle Y = T.Y.getSafe();
        public static final EnumPlayerTeleportFlagsHandle Z = T.Z.getSafe();
        public static final EnumPlayerTeleportFlagsHandle Y_ROT = T.Y_ROT.getSafe();
        public static final EnumPlayerTeleportFlagsHandle X_ROT = T.X_ROT.getSafe();
        /* ============================================================================== */

        public static EnumPlayerTeleportFlagsHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */


        public static Set<?> allAbsolute() {
            return java.util.EnumSet.noneOf((Class) T.getType());
        }

        public static Set<?> allRelative() {
            return java.util.EnumSet.allOf((Class) T.getType());
        }
        /**
         * Stores class members for <b>net.minecraft.server.PacketPlayOutPosition.EnumPlayerTeleportFlags</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class EnumPlayerTeleportFlagsClass extends Template.Class<EnumPlayerTeleportFlagsHandle> {
            public final Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle> X = new Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle> Y = new Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle> Z = new Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle> Y_ROT = new Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle>();
            public final Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle> X_ROT = new Template.EnumConstant.Converted<EnumPlayerTeleportFlagsHandle>();

        }

    }

}

