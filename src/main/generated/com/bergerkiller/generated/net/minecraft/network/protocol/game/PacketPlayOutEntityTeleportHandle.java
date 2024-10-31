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
    /** @see PacketPlayOutEntityTeleportClass */
    public static final PacketPlayOutEntityTeleportClass T = Template.Class.create(PacketPlayOutEntityTeleportClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityTeleportHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutEntityTeleportHandle createNewForEntity(Entity entity) {
        return T.createNewForEntity.invoke(entity);
    }

    public static PacketPlayOutEntityTeleportHandle createNew(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        return T.createNew.invokeVA(entityId, posX, posY, posZ, yaw, pitch, onGround);
    }

    public abstract int getEntityId();
    public abstract double getPosX();
    public abstract double getPosY();
    public abstract double getPosZ();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract boolean isOnGround();
    public abstract int getEncodedPosX();
    public abstract int getEncodedPosY();
    public abstract int getEncodedPosZ();
    public abstract int getEncodedYaw();
    public abstract int getEncodedPitch();
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_TELEPORT;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int entityId;
        private double posX, posY, posZ;
        private float yaw, pitch;
        private boolean onGround;

        public Builder entityId(int entityId) { this.entityId = entityId; return this; }
        public Builder posX(double posX) { this.posX = posX; return this; }
        public Builder posY(double posY) { this.posY = posY; return this; }
        public Builder posZ(double posZ) { this.posZ = posZ; return this; }
        public Builder position(org.bukkit.util.Vector position) {
            return position(position.getX(), position.getY(), position.getZ());
        }
        public Builder position(double x, double y, double z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
            return this;
        }
        public Builder yaw(float yaw) { this.yaw = yaw; return this; }
        public Builder pitch(float pitch) { this.pitch = pitch; return this; }
        public Builder rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }
        public Builder onGround(boolean onGround) { this.onGround = onGround; return this; }

        public PacketPlayOutEntityTeleportHandle create() {
            return PacketPlayOutEntityTeleportHandle.createNew(entityId, posX, posY, posZ, yaw, pitch, onGround);
        }
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityTeleportClass extends Template.Class<PacketPlayOutEntityTeleportHandle> {
        public final Template.StaticMethod.Converted<PacketPlayOutEntityTeleportHandle> createNewForEntity = new Template.StaticMethod.Converted<PacketPlayOutEntityTeleportHandle>();
        public final Template.StaticMethod.Converted<PacketPlayOutEntityTeleportHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutEntityTeleportHandle>();

        public final Template.Method<Integer> getEntityId = new Template.Method<Integer>();
        public final Template.Method<Double> getPosX = new Template.Method<Double>();
        public final Template.Method<Double> getPosY = new Template.Method<Double>();
        public final Template.Method<Double> getPosZ = new Template.Method<Double>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Boolean> isOnGround = new Template.Method<Boolean>();
        public final Template.Method<Integer> getEncodedPosX = new Template.Method<Integer>();
        public final Template.Method<Integer> getEncodedPosY = new Template.Method<Integer>();
        public final Template.Method<Integer> getEncodedPosZ = new Template.Method<Integer>();
        public final Template.Method<Integer> getEncodedYaw = new Template.Method<Integer>();
        public final Template.Method<Integer> getEncodedPitch = new Template.Method<Integer>();

    }

}

