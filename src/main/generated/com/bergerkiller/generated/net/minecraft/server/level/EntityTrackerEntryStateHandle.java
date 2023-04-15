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
    /** @see EntityTrackerEntryStateClass */
    public static final EntityTrackerEntryStateClass T = Template.Class.create(EntityTrackerEntryStateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityTrackerEntryStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Vector getLoc();
    public abstract double getLocX();
    public abstract double getLocY();
    public abstract double getLocZ();
    public abstract void setLoc(double x, double y, double z);
    public abstract void setLocX(double x);
    public abstract void setLocY(double y);
    public abstract void setLocZ(double z);
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
        if (com.bergerkiller.bukkit.common.internal.CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
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

    public void setYaw(float yaw) {
        T.raw_yRot.setInteger(getRaw(), getProtocolRotation(yaw));
    }

    public void setPitch(float pitch) {
        T.raw_xRot.setInteger(getRaw(), getProtocolRotation(pitch));
    }

    public void setHeadYaw(float headYaw) {
        T.raw_headYaw.setInteger(getRaw(), getProtocolRotation(headYaw));
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
        public final Template.Field.Integer raw_xRot = new Template.Field.Integer();
        public final Template.Field.Integer raw_yRot = new Template.Field.Integer();
        public final Template.Field.Integer raw_headYaw = new Template.Field.Integer();
        public final Template.Field.Integer tickCounter = new Template.Field.Integer();
        public final Template.Field.Integer timeSinceLocationSync = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<List<Entity>> opt_passengers = new Template.Field.Converted<List<Entity>>();
        @Template.Optional
        public final Template.Field.Converted<Entity> opt_vehicle = new Template.Field.Converted<Entity>();

        public final Template.Method<Vector> getLoc = new Template.Method<Vector>();
        public final Template.Method<Double> getLocX = new Template.Method<Double>();
        public final Template.Method<Double> getLocY = new Template.Method<Double>();
        public final Template.Method<Double> getLocZ = new Template.Method<Double>();
        public final Template.Method<Void> setLoc = new Template.Method<Void>();
        public final Template.Method<Void> setLocX = new Template.Method<Void>();
        public final Template.Method<Void> setLocY = new Template.Method<Void>();
        public final Template.Method<Void> setLocZ = new Template.Method<Void>();
        public final Template.Method<Double> getXVel = new Template.Method<Double>();
        public final Template.Method<Double> getYVel = new Template.Method<Double>();
        public final Template.Method<Double> getZVel = new Template.Method<Double>();
        public final Template.Method<Void> setXVel = new Template.Method<Void>();
        public final Template.Method<Void> setYVel = new Template.Method<Void>();
        public final Template.Method<Void> setZVel = new Template.Method<Void>();
        public final Template.Method<Vector> getVelocity = new Template.Method<Vector>();
        public final Template.Method<Void> setVelocity = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> removePairing = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> addPairing = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> checkTrackNeeded = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonPacket> getSpawnPacket = new Template.Method.Converted<CommonPacket>();
        public final Template.Method<Void> onTick = new Template.Method<Void>();

    }

}

