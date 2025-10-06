package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import org.bukkit.util.Vector;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity")
public abstract class PacketPlayOutSpawnEntityHandle extends PacketHandle {
    /** @see PacketPlayOutSpawnEntityClass */
    public static final PacketPlayOutSpawnEntityClass T = Template.Class.create(PacketPlayOutSpawnEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract float getYaw();
    public abstract float getPitch();
    public abstract void setYaw(float yaw);
    public abstract void setPitch(float pitch);
    public abstract Vector getMotVector();
    public abstract void setMotVector(Vector motVector);
    public abstract double getMotX();
    public abstract double getMotY();
    public abstract double getMotZ();
    public abstract void setMotX(double motX);
    public abstract void setMotY(double motY);
    public abstract void setMotZ(double motZ);
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_SPAWN;
    }

    public void setEntityUUID(UUID uuid) {
        if (T.entityUUID.isAvailable()) {
            T.entityUUID.set(getRaw(), uuid);
        }
    }

    public static boolean isEntityTypeSupported(org.bukkit.entity.EntityType type) {
        return isCommonEntityTypeSupported(com.bergerkiller.bukkit.common.entity.CommonEntityType.byEntityType(type));
    }

    public static boolean isCommonEntityTypeSupported(com.bergerkiller.bukkit.common.entity.CommonEntityType commonEntityType) {
        if (commonEntityType == null) {
            return false;
        } else if (T.opt_entityTypeId.isAvailable()) {
            return (commonEntityType.objectTypeId != -1);
        } else {
            return (commonEntityType.nmsEntityType != null);
        }
    }

    public void setCommonEntityType(com.bergerkiller.bukkit.common.entity.CommonEntityType commonEntityType) {
        if (commonEntityType == null) {
            throw new IllegalArgumentException("Input CommonEntityType is null");
        }
        if (T.opt_entityTypeId.isAvailable()) {
            if (commonEntityType.objectTypeId == -1) {
                throw new IllegalArgumentException("Input " + commonEntityType.toString() + " cannot be spawned using this packet");
            }
            T.opt_entityTypeId.setInteger(getRaw(), commonEntityType.objectTypeId);
        } else {
            if (commonEntityType.nmsEntityType == null) {
                throw new IllegalArgumentException("Input " + commonEntityType.toString() + " cannot be spawned using this packet");
            }
            T.opt_entityType.set(getRaw(), commonEntityType.nmsEntityType);
        }
        if (commonEntityType.objectExtraData != -1) {
            setExtraData(commonEntityType.objectExtraData);
        }
    }

    public com.bergerkiller.bukkit.common.entity.CommonEntityType getCommonEntityType() {
        if (T.opt_entityTypeId.isAvailable()) {
            int id = T.opt_entityTypeId.getInteger(getRaw());
            return com.bergerkiller.bukkit.common.entity.CommonEntityType.byObjectTypeId(id);
        } else {
            EntityTypesHandle nmsType = T.opt_entityType.get(getRaw());
            return com.bergerkiller.bukkit.common.entity.CommonEntityType.byNMSEntityType(nmsType);
        }
    }

    public void setEntityType(org.bukkit.entity.EntityType type) {
        setCommonEntityType(com.bergerkiller.bukkit.common.entity.CommonEntityType.byEntityType(type));
    }

    public org.bukkit.entity.EntityType getEntityType() {
        return getCommonEntityType().entityType;
    }

    @Deprecated
    public void setEntityTypeId(int typeId) {
        if (T.opt_entityTypeId.isAvailable()) {
            T.opt_entityTypeId.setInteger(getRaw(), typeId);
        } else {
            setCommonEntityType(com.bergerkiller.bukkit.common.entity.CommonEntityType.byObjectTypeId(typeId));
        }
    }

    @Deprecated
    public int getEntityTypeId() {
        if (T.opt_entityTypeId.isAvailable()) {
            return T.opt_entityTypeId.getInteger(getRaw());
        } else {
            return getCommonEntityType().objectTypeId;
        }
    }

    public void setFallingBlockData(com.bergerkiller.bukkit.common.wrappers.BlockData blockData) {
        setExtraData(blockData.getCombinedId());
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
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract int getExtraData();
    public abstract void setExtraData(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnEntityClass extends Template.Class<PacketPlayOutSpawnEntityHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field<UUID>();
        @Template.Optional
        public final Template.Field.Converted<EntityTypesHandle> opt_entityType = new Template.Field.Converted<EntityTypesHandle>();
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
        public final Template.Field.Integer opt_entityTypeId = new Template.Field.Integer();
        public final Template.Field.Integer extraData = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutSpawnEntityHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutSpawnEntityHandle>();

        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Void> setYaw = new Template.Method<Void>();
        public final Template.Method<Void> setPitch = new Template.Method<Void>();
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

