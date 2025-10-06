package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.util.Vector;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving")
public abstract class PacketPlayOutSpawnEntityLivingHandle extends PacketHandle {
    /** @see PacketPlayOutSpawnEntityLivingClass */
    public static final PacketPlayOutSpawnEntityLivingClass T = Template.Class.create(PacketPlayOutSpawnEntityLivingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityLivingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityLivingHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract CommonEntityType getCommonEntityType();
    public abstract void setCommonEntityType(CommonEntityType commonEntityType);
    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract void setPosX(double x);
    public abstract void setPosY(double y);
    public abstract void setPosZ(double z);
    public abstract Vector getMotVector();
    public abstract void setMotVector(Vector motVector);
    public abstract double getMotX();
    public abstract double getMotY();
    public abstract double getMotZ();
    public abstract void setMotX(double motX);
    public abstract void setMotY(double motY);
    public abstract void setMotZ(double motZ);
    public boolean hasDataWatcherSupport() {
        return T.opt_dataWatcher.isAvailable();
    }

    @Deprecated
    public com.bergerkiller.bukkit.common.wrappers.DataWatcher getDataWatcher() {
        if (T.opt_dataWatcher.isAvailable()) {
            return T.opt_dataWatcher.get(getRaw());
        } else {
            return null;
        }
    }

    @Deprecated
    public void setDataWatcher(com.bergerkiller.bukkit.common.wrappers.DataWatcher dataWatcher) {
        if (T.opt_dataWatcher.isAvailable()) {
            T.opt_dataWatcher.set(getRaw(), dataWatcher);
        }
    }

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_SPAWN_LIVING;
    }

    public void setEntityUUID(UUID uuid) {
        if (T.entityUUID.isAvailable()) {
            T.entityUUID.set(getRaw(), uuid);
        }
    }

    public static boolean isCommonEntityTypeSupported(com.bergerkiller.bukkit.common.entity.CommonEntityType type) {
        return type != null && type.entityTypeId != -1;
    }

    public static boolean isEntityTypeSupported(org.bukkit.entity.EntityType type) {
        return type != null && isCommonEntityTypeSupported(com.bergerkiller.bukkit.common.entity.CommonEntityType.byEntityType(type));
    }

    public void setEntityType(org.bukkit.entity.EntityType type) {
        if (type == null) {
            throw new IllegalArgumentException("Input EntityType is null");
        }
        setCommonEntityType(com.bergerkiller.bukkit.common.entity.CommonEntityType.byEntityType(type));
    }

    public org.bukkit.entity.EntityType getEntityType() {
        return getCommonEntityType().entityType;
    }

    public float getYaw() {
        return getProtocolRotation(T.yaw_raw);
    }

    public float getPitch() {
        return getProtocolRotation(T.pitch_raw);
    }

    public float getHeadYaw() {
        return getProtocolRotation(T.headYaw_raw);
    }

    public void setYaw(float yaw) {
        setProtocolRotation(T.yaw_raw, yaw);
    }

    public void setPitch(float pitch) {
        setProtocolRotation(T.pitch_raw, pitch);
    }

    public void setHeadYaw(float headYaw) {
        setProtocolRotation(T.headYaw_raw, headYaw);
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnEntityLivingClass extends Template.Class<PacketPlayOutSpawnEntityLivingHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field<UUID>();
        @Template.Optional
        public final Template.Field.Byte pitch_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte yaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte headYaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Converted<DataWatcher> opt_dataWatcher = new Template.Field.Converted<DataWatcher>();

        public final Template.StaticMethod.Converted<PacketPlayOutSpawnEntityLivingHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutSpawnEntityLivingHandle>();

        public final Template.Method<CommonEntityType> getCommonEntityType = new Template.Method<CommonEntityType>();
        public final Template.Method<Void> setCommonEntityType = new Template.Method<Void>();
        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Void> setPosX = new Template.Method<Void>();
        public final Template.Method<Void> setPosY = new Template.Method<Void>();
        public final Template.Method<Void> setPosZ = new Template.Method<Void>();
        public final Template.Method.Converted<Vector> getMotVector = new Template.Method.Converted<Vector>();
        public final Template.Method.Converted<Void> setMotVector = new Template.Method.Converted<Void>();
        public final Template.Method<Double> getMotX = new Template.Method<Double>();
        public final Template.Method<Double> getMotY = new Template.Method<Double>();
        public final Template.Method<Double> getMotZ = new Template.Method<Double>();
        public final Template.Method<Void> setMotX = new Template.Method<Void>();
        public final Template.Method<Void> setMotY = new Template.Method<Void>();
        public final Template.Method<Void> setMotZ = new Template.Method<Void>();

    }

}

