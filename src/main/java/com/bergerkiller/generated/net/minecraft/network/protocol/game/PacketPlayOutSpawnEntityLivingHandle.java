package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.LivingEntity;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving")
public abstract class PacketPlayOutSpawnEntityLivingHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSpawnEntityLivingClass} */
    public static final PacketPlayOutSpawnEntityLivingClass T = Template.Class.create(PacketPlayOutSpawnEntityLivingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityLivingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutSpawnEntityLivingHandle createNew() {
        return T.constr.newInstance();
    }

    public static final PacketPlayOutSpawnEntityLivingHandle createNew(LivingEntity entity) {
        return T.constr_entity.newInstance(entity);
    }

    /* ============================================================================== */


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

    public void setCommonEntityType(com.bergerkiller.bukkit.common.entity.CommonEntityType commonEntityType) {
        if (commonEntityType == null) {
            throw new IllegalArgumentException("Input CommonEntityType is null");
        }
        if (commonEntityType.entityTypeId == -1) {
            throw new IllegalArgumentException("Input CommonEntityType " + commonEntityType.toString() + " cannot be spawned using this packet");
        }
        setEntityTypeId(commonEntityType.entityTypeId);
    }

    public com.bergerkiller.bukkit.common.entity.CommonEntityType getCommonEntityType() {
        return com.bergerkiller.bukkit.common.entity.CommonEntityType.byEntityTypeId(getEntityTypeId());
    }

    public void setEntityType(org.bukkit.entity.EntityType type) {
        if (type == null) {
            throw new IllegalArgumentException("Input EntityType is null");
        }
        int typeId = com.bergerkiller.bukkit.common.entity.CommonEntityType.byEntityType(type).entityTypeId;
        if (typeId == -1) {
            throw new IllegalArgumentException("Input EntityType " + type.name() + " cannot be spawned using this packet");
        }
        setEntityTypeId(typeId);
    }

    public org.bukkit.entity.EntityType getEntityType() {
        return getCommonEntityType().entityType;
    }

    public double getPosX() {
        return getProtocolPosition(T.posX_1_8_8, T.posX_1_10_2);
    }

    public double getPosY() {
        return getProtocolPosition(T.posY_1_8_8, T.posY_1_10_2);
    }

    public double getPosZ() {
        return getProtocolPosition(T.posZ_1_8_8, T.posZ_1_10_2);
    }

    public void setPosX(double posX) {
        setProtocolPosition(T.posX_1_8_8, T.posX_1_10_2, posX);
    }

    public void setPosY(double posY) {
        setProtocolPosition(T.posY_1_8_8, T.posY_1_10_2, posY);
    }

    public void setPosZ(double posZ) {
        setProtocolPosition(T.posZ_1_8_8, T.posZ_1_10_2, posZ);
    }

    public double getMotX() {
        return deserializeVelocity(T.motX_raw.getInteger(getRaw()));
    }

    public double getMotY() {
        return deserializeVelocity(T.motY_raw.getInteger(getRaw()));
    }

    public double getMotZ() {
        return deserializeVelocity(T.motZ_raw.getInteger(getRaw()));
    }

    public void setMotX(double motX) {
        T.motX_raw.setInteger(getRaw(), serializeVelocity(motX));
    }

    public void setMotY(double motY) {
        T.motY_raw.setInteger(getRaw(), serializeVelocity(motY));
    }

    public void setMotZ(double motZ) {
        T.motZ_raw.setInteger(getRaw(), serializeVelocity(motZ));
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
    public abstract int getEntityTypeId();
    public abstract void setEntityTypeId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnEntityLivingClass extends Template.Class<PacketPlayOutSpawnEntityLivingHandle> {
        public final Template.Constructor.Converted<PacketPlayOutSpawnEntityLivingHandle> constr = new Template.Constructor.Converted<PacketPlayOutSpawnEntityLivingHandle>();
        public final Template.Constructor.Converted<PacketPlayOutSpawnEntityLivingHandle> constr_entity = new Template.Constructor.Converted<PacketPlayOutSpawnEntityLivingHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field<UUID>();
        public final Template.Field.Integer entityTypeId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posX_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posY_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posZ_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Double posX_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posY_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posZ_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Integer motX_raw = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer motY_raw = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer motZ_raw = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Byte yaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte pitch_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte headYaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Converted<DataWatcher> opt_dataWatcher = new Template.Field.Converted<DataWatcher>();

    }

}

