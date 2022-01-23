package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.List;
import java.util.function.Consumer;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.EntityTrackerEntryState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.EntityTrackerEntryState")
public abstract class EntityTrackerEntryStateHandle extends Template.Handle {
    /** @See {@link EntityTrackerEntryStateClass} */
    public static final EntityTrackerEntryStateClass T = Template.Class.create(EntityTrackerEntryStateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityTrackerEntryStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract double getXVel();
    public abstract double getYVel();
    public abstract double getZVel();
    public abstract void setXVel(double x);
    public abstract void setYVel(double y);
    public abstract void setZVel(double z);
    public abstract Vector getVelocity();
    public abstract void setVelocity(double x, double y, double z);
    public abstract boolean checkTrackNeeded();
    public abstract CommonPacket getSpawnPacket();
    public abstract void onTick();

    public void setVelocity(org.bukkit.util.Vector velocity) {
        setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
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
    public abstract EntityHandle getEntity();
    public abstract void setEntity(EntityHandle value);
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
     * Stores class members for <b>net.minecraft.server.level.EntityTrackerEntryState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityTrackerEntryStateClass extends Template.Class<EntityTrackerEntryStateHandle> {
        public final Template.Field.Converted<EntityHandle> entity = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Integer updateInterval = new Template.Field.Integer();
        public final Template.Field.Boolean isMobile = new Template.Field.Boolean();
        @Template.Optional
        public final Template.Field<Consumer> broadcastMethod = new Template.Field<Consumer>();
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
        public final Template.Field.Integer tickCounter = new Template.Field.Integer();
        public final Template.Field.Integer timeSinceLocationSync = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<List<Entity>> opt_passengers = new Template.Field.Converted<List<Entity>>();
        @Template.Optional
        public final Template.Field.Converted<Entity> opt_vehicle = new Template.Field.Converted<Entity>();

        public final Template.Method<Double> getXVel = new Template.Method<Double>();
        public final Template.Method<Double> getYVel = new Template.Method<Double>();
        public final Template.Method<Double> getZVel = new Template.Method<Double>();
        public final Template.Method<Void> setXVel = new Template.Method<Void>();
        public final Template.Method<Void> setYVel = new Template.Method<Void>();
        public final Template.Method<Void> setZVel = new Template.Method<Void>();
        public final Template.Method<Vector> getVelocity = new Template.Method<Vector>();
        public final Template.Method<Void> setVelocity = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> onViewerAdded_tuinity = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method<Void> removeViewerFromMap_tuinity = new Template.Method<Void>();
        public final Template.Method<Boolean> checkTrackNeeded = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonPacket> getSpawnPacket = new Template.Method.Converted<CommonPacket>();
        public final Template.Method<Void> onTick = new Template.Method<Void>();

    }

}

