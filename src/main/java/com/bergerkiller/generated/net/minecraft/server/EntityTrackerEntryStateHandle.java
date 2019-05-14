package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityTrackerEntryState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityTrackerEntryStateHandle extends Template.Handle {
    /** @See {@link EntityTrackerEntryStateClass} */
    public static final EntityTrackerEntryStateClass T = new EntityTrackerEntryStateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityTrackerEntryStateHandle.class, "net.minecraft.server.EntityTrackerEntryState", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityTrackerEntryStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean checkTrackNeeded();
    public abstract CommonPacket getSpawnPacket();
    public abstract void onTick();

    public double getXVel() {
        if (T.opt_xVel.isAvailable()) {
            return T.opt_xVel.getDouble(getRaw());
        } else {
            Object vel = T.opt_velocity.raw.get(getRaw());
            return com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.x.getDouble(vel);
        }
    }

    public double getYVel() {
        if (T.opt_yVel.isAvailable()) {
            return T.opt_yVel.getDouble(getRaw());
        } else {
            Object vel = T.opt_velocity.raw.get(getRaw());
            return com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.y.getDouble(vel);
        }
    }

    public double getZVel() {
        if (T.opt_zVel.isAvailable()) {
            return T.opt_zVel.getDouble(getRaw());
        } else {
            Object vel = T.opt_velocity.raw.get(getRaw());
            return com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.z.getDouble(vel);
        }
    }

    public void setXVel(double x) {
        if (T.opt_xVel.isAvailable()) {
            T.opt_xVel.setDouble(getRaw(), x);
        } else {
            Object vel = T.opt_velocity.raw.get(getRaw());
            double y = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.y.getDouble(vel);
            double z = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.z.getDouble(vel);
            setVelocity(x, y, z);
        }
    }

    public void setYVel(double y) {
        if (T.opt_yVel.isAvailable()) {
            T.opt_yVel.setDouble(getRaw(), y);
        } else {
            Object vel = T.opt_velocity.raw.get(getRaw());
            double x = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.x.getDouble(vel);
            double z = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.z.getDouble(vel);
            setVelocity(x, y, z);
        }
    }

    public void setZVel(double z) {
        if (T.opt_zVel.isAvailable()) {
            T.opt_zVel.setDouble(getRaw(), z);
        } else {
            Object vel = T.opt_velocity.raw.get(getRaw());
            double x = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.x.getDouble(vel);
            double y = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.y.getDouble(vel);
            setVelocity(x, y, z);
        }
    }

    public org.bukkit.util.Vector getVelocity() {
        if (T.opt_velocity.isAvailable()) {
            return T.opt_velocity.get(getRaw());
        } else {
            double x = T.opt_xVel.getDouble(getRaw());
            double y = T.opt_yVel.getDouble(getRaw());
            double z = T.opt_zVel.getDouble(getRaw());
            return new org.bukkit.util.Vector(x, y, z);
        }
    }

    public void setVelocity(org.bukkit.util.Vector velocity) {
        if (T.opt_velocity.isAvailable()) {
            T.opt_velocity.set(getRaw(), velocity);
        } else {
            T.opt_xVel.setDouble(getRaw(), velocity.getX());
            T.opt_yVel.setDouble(getRaw(), velocity.getY());
            T.opt_zVel.setDouble(getRaw(), velocity.getZ());
        }
    }

    public void setVelocity(double x, double y, double z) {
        if (T.opt_velocity.isAvailable()) {
            Object vel = com.bergerkiller.generated.net.minecraft.server.Vec3DHandle.T.constr_x_y_z.raw.newInstance(x, y, z);
            T.opt_velocity.raw.set(getRaw(), vel);
        } else {
            T.opt_xVel.setDouble(getRaw(), x);
            T.opt_yVel.setDouble(getRaw(), y);
            T.opt_zVel.setDouble(getRaw(), z);
        }
    }


    public static final double POSITION_STEP;
    public static final float ROTATION_STEP;
    public static final float ROTATION_STEP_INV;
    static {
        if (T.long_xLoc.isAvailable()) {
            POSITION_STEP = 1.0 / 4096.0;
        } else {
            POSITION_STEP = 1.0 / 32.0;
        }
        ROTATION_STEP = 360.0f / 256.0f;
        ROTATION_STEP_INV = 256.0f / 360.0f;
    }

    public static final boolean hasProtocolRotationChanged(float angle1, float angle2) {
        if (angle1 == angle2) {
            return false;
        }

        int prot_diff = com.bergerkiller.bukkit.common.utils.MathUtil.floor((angle2-angle1)*ROTATION_STEP_INV) & 0xFF;
        if (prot_diff > 0 && prot_diff < 255) {
            return true;
        }

        int prot1 = com.bergerkiller.bukkit.common.utils.MathUtil.floor(angle1*ROTATION_STEP_INV);
        int prot2 = com.bergerkiller.bukkit.common.utils.MathUtil.floor(angle2*ROTATION_STEP_INV);
        return ((prot1 - prot2) & 0xFF) != 0;
    }

    public static final int getProtocolRotation(float angle) {
        int protAngle = com.bergerkiller.bukkit.common.utils.MathUtil.floor(angle * ROTATION_STEP_INV) & 0xFF;
        if (protAngle >= 128) {
            protAngle -= 256;
        }
        return protAngle;
    }

    public static final float getRotationFromProtocol(int protocol) {
        int protAngle = protocol & 0xFF;
        if (protAngle >= 128) {
            protAngle -= 256;
        }
        return (float) protAngle * ROTATION_STEP;
    }

    public void setLocX(double x) {
        if (T.long_xLoc.isAvailable()) {
            T.long_xLoc.setLong(getRaw(), com.bergerkiller.bukkit.common.utils.MathUtil.longFloor(x * 4096.0));
        } else {
            T.int_xLoc.setInteger(getRaw(), com.bergerkiller.bukkit.common.utils.MathUtil.floor(x * 32.0));
        }
    }

    public void setLocY(double y) {
        if (T.long_yLoc.isAvailable()) {
            T.long_yLoc.setLong(getRaw(), com.bergerkiller.bukkit.common.utils.MathUtil.longFloor(y * 4096.0));
        } else {
            T.int_yLoc.setInteger(getRaw(), com.bergerkiller.bukkit.common.utils.MathUtil.floor(y * 32.0));
        }
    }

    public void setLocZ(double z) {
        if (T.long_zLoc.isAvailable()) {
            T.long_zLoc.setLong(getRaw(), com.bergerkiller.bukkit.common.utils.MathUtil.longFloor(z * 4096.0));
        } else {
            T.int_zLoc.setInteger(getRaw(), com.bergerkiller.bukkit.common.utils.MathUtil.floor(z * 32.0));
        }
    }

    public void setYaw(float yaw) {
        T.raw_yRot.setInteger(getRaw(), getProtocolRotation(yaw));
    }

    public void setPitch(float pitch) {
        T.raw_xRot.setInteger(getRaw(), getProtocolRotation(pitch));
    }

    public void setHeadYaw(float headYaw) {
        T.raw_headYaw.setInteger(getRaw(), getProtocolRotation(headYaw));
    }

    public double getLocX() {
        if (T.long_xLoc.isAvailable()) {
            return (double) T.long_xLoc.getLong(getRaw()) / 4096.0;
        } else {
            return (double) T.int_xLoc.getInteger(getRaw()) / 32.0;
        }
    }

    public double getLocY() {
        if (T.long_yLoc.isAvailable()) {
            return (double) T.long_yLoc.getLong(getRaw()) / 4096.0;
        } else {
            return (double) T.int_yLoc.getInteger(getRaw()) / 32.0;
        }
    }

    public double getLocZ() {
        if (T.long_zLoc.isAvailable()) {
            return (double) T.long_zLoc.getLong(getRaw()) / 4096.0;
        } else {
            return (double) T.int_zLoc.getInteger(getRaw()) / 32.0;
        }
    }

    public float getYaw() {
        return getRotationFromProtocol(T.raw_yRot.getInteger(getRaw()));
    }

    public float getPitch() {
        return getRotationFromProtocol(T.raw_xRot.getInteger(getRaw()));
    }

    public float getHeadYaw() {
        return getRotationFromProtocol(T.raw_headYaw.getInteger(getRaw()));
    }
    @Template.Readonly
    public abstract EntityHandle getEntity();
    public abstract int getUpdateInterval();
    public abstract void setUpdateInterval(int value);
    public abstract boolean isMobile();
    public abstract void setIsMobile(boolean value);
    public abstract int getRaw_xRot();
    public abstract void setRaw_xRot(int value);
    public abstract int getRaw_yRot();
    public abstract void setRaw_yRot(int value);
    public abstract int getRaw_headYaw();
    public abstract void setRaw_headYaw(int value);
    public abstract int getTickCounter();
    public abstract void setTickCounter(int value);
    public abstract int getTimeSinceLocationSync();
    public abstract void setTimeSinceLocationSync(int value);
    /**
     * Stores class members for <b>net.minecraft.server.EntityTrackerEntryState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTrackerEntryStateClass extends Template.Class<EntityTrackerEntryStateHandle> {
        @Template.Readonly
        public final Template.Field.Converted<EntityHandle> entity = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Integer updateInterval = new Template.Field.Integer();
        public final Template.Field.Boolean isMobile = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field.Long long_xLoc = new Template.Field.Long();
        @Template.Optional
        public final Template.Field.Long long_yLoc = new Template.Field.Long();
        @Template.Optional
        public final Template.Field.Long long_zLoc = new Template.Field.Long();
        @Template.Optional
        public final Template.Field.Integer int_xLoc = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer int_yLoc = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer int_zLoc = new Template.Field.Integer();
        public final Template.Field.Integer raw_xRot = new Template.Field.Integer();
        public final Template.Field.Integer raw_yRot = new Template.Field.Integer();
        public final Template.Field.Integer raw_headYaw = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Double opt_xVel = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double opt_yVel = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double opt_zVel = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Converted<Vector> opt_velocity = new Template.Field.Converted<Vector>();
        public final Template.Field.Integer tickCounter = new Template.Field.Integer();
        public final Template.Field.Integer timeSinceLocationSync = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<List<Entity>> opt_passengers = new Template.Field.Converted<List<Entity>>();
        @Template.Optional
        public final Template.Field.Converted<Entity> opt_vehicle = new Template.Field.Converted<Entity>();

        public final Template.Method<Boolean> checkTrackNeeded = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonPacket> getSpawnPacket = new Template.Method.Converted<CommonPacket>();
        public final Template.Method<Void> onTick = new Template.Method<Void>();

    }

}

