package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutPositionHandle extends PacketHandle {
    /** @See {@link PacketPlayOutPositionClass} */
    public static final PacketPlayOutPositionClass T = new PacketPlayOutPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutPositionHandle.class, "net.minecraft.server.PacketPlayOutPosition", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutPositionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


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
    public abstract Set<?> getTeleportFlags();
    public abstract void setTeleportFlags(Set<?> value);
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
        public final Template.Field.Converted<Set<?>> teleportFlags = new Template.Field.Converted<Set<?>>();
        @Template.Optional
        public final Template.Field.Integer teleportWaitTimer = new Template.Field.Integer();

    }

}

