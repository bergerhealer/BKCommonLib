package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutSpawnEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutSpawnEntityHandle extends PacketHandle {
    /** @See {@link PacketPlayOutSpawnEntityClass} */
    public static final PacketPlayOutSpawnEntityClass T = new PacketPlayOutSpawnEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutSpawnEntityHandle.class, "net.minecraft.server.PacketPlayOutSpawnEntity");

    /* ============================================================================== */

    public static PacketPlayOutSpawnEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutSpawnEntityHandle handle = new PacketPlayOutSpawnEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


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
        return deserializeVelocity(T.motX_raw.getInteger(instance));
    }

    public double getMotY() {
        return deserializeVelocity(T.motY_raw.getInteger(instance));
    }

    public double getMotZ() {
        return deserializeVelocity(T.motZ_raw.getInteger(instance));
    }

    public void setMotX(double motX) {
        T.motX_raw.setInteger(instance, serializeVelocity(motX));
    }

    public void setMotY(double motY) {
        T.motY_raw.setInteger(instance, serializeVelocity(motY));
    }

    public void setMotZ(double motZ) {
        T.motZ_raw.setInteger(instance, serializeVelocity(motZ));
    }

    public float getYaw() {
        return deserializeRotation(T.yaw_raw.getInteger(instance));
    }

    public float getPitch() {
        return deserializeRotation(T.pitch_raw.getInteger(instance));
    }

    public void setYaw(float yaw) {
        T.yaw_raw.setInteger(instance, serializeRotation(yaw));
    }

    public void setPitch(float pitch) {
        T.pitch_raw.setInteger(instance, serializeRotation(pitch));
    }
    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public int getEntityTypeId() {
        return T.entityTypeId.getInteger(instance);
    }

    public void setEntityTypeId(int value) {
        T.entityTypeId.setInteger(instance, value);
    }

    public int getExtraData() {
        return T.extraData.getInteger(instance);
    }

    public void setExtraData(int value) {
        T.extraData.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutSpawnEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutSpawnEntityClass extends Template.Class<PacketPlayOutSpawnEntityHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<UUID> entityUUID = new Template.Field<UUID>();
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
        public final Template.Field.Integer pitch_raw = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer yaw_raw = new Template.Field.Integer();
        public final Template.Field.Integer entityTypeId = new Template.Field.Integer();
        public final Template.Field.Integer extraData = new Template.Field.Integer();

    }

}

