package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport")
public abstract class PacketPlayOutEntityTeleportHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityTeleportClass} */
    public static final PacketPlayOutEntityTeleportClass T = Template.Class.create(PacketPlayOutEntityTeleportClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityTeleportHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityTeleportHandle createNew(Entity entity) {
        return T.constr_entity.newInstance(entity);
    }

    /* ============================================================================== */

    public static PacketPlayOutEntityTeleportHandle createNew() {
        return T.createNew.invoke();
    }


    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_TELEPORT;
    }

    public static PacketPlayOutEntityTeleportHandle createNew(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        PacketPlayOutEntityTeleportHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setPosX(posX);
        handle.setPosY(posY);
        handle.setPosZ(posZ);
        handle.setYaw(yaw);
        handle.setPitch(pitch);
        handle.setOnGround(onGround);
        return handle;
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

    public float getYaw() {
        return getProtocolRotation(T.yaw_raw);
    }

    public float getPitch() {
        return getProtocolRotation(T.pitch_raw);
    }

    public void setYaw(float yaw) {
        setProtocolRotation(T.yaw_raw, yaw);
    }

    public void setPitch(float pitch) {
        setProtocolRotation(T.pitch_raw, pitch);
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract boolean isOnGround();
    public abstract void setOnGround(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityTeleportClass extends Template.Class<PacketPlayOutEntityTeleportHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityTeleportHandle> constr_entity = new Template.Constructor.Converted<PacketPlayOutEntityTeleportHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
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
        public final Template.Field.Byte yaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte pitch_raw = new Template.Field.Byte();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();

        public final Template.StaticMethod.Converted<PacketPlayOutEntityTeleportHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutEntityTeleportHandle>();

    }

}

