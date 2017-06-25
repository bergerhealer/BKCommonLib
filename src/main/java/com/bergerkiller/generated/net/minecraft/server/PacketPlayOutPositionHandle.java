package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Set;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutPositionHandle extends PacketHandle {
    /** @See {@link PacketPlayOutPositionClass} */
    public static final PacketPlayOutPositionClass T = new PacketPlayOutPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutPositionHandle.class, "net.minecraft.server.PacketPlayOutPosition");

    /* ============================================================================== */

    public static PacketPlayOutPositionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutPositionHandle handle = new PacketPlayOutPositionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public int getTeleportWaitTimer() {
        if (T.teleportWaitTimer.isAvailable()) {
            return T.teleportWaitTimer.getInteger(instance);
        } else {
            return 0;
        }
    }

    public void setTeleportWaitTimer(int ticks) {
        if (T.teleportWaitTimer.isAvailable()) {
            T.teleportWaitTimer.setInteger(instance, ticks);
        }
    }
    public double getX() {
        return T.x.getDouble(instance);
    }

    public void setX(double value) {
        T.x.setDouble(instance, value);
    }

    public double getY() {
        return T.y.getDouble(instance);
    }

    public void setY(double value) {
        T.y.setDouble(instance, value);
    }

    public double getZ() {
        return T.z.getDouble(instance);
    }

    public void setZ(double value) {
        T.z.setDouble(instance, value);
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

    public Set<?> getTeleportFlags() {
        return T.teleportFlags.get(instance);
    }

    public void setTeleportFlags(Set<?> value) {
        T.teleportFlags.set(instance, value);
    }

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

