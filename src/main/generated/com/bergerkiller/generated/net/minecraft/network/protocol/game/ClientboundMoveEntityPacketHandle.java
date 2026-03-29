package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundMoveEntityPacket")
public abstract class ClientboundMoveEntityPacketHandle extends PacketHandle {
    /** @see ClientboundMoveEntityPacketClass */
    public static final ClientboundMoveEntityPacketClass T = Template.Class.create(ClientboundMoveEntityPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundMoveEntityPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract double getDeltaX();
    public abstract double getDeltaY();
    public abstract double getDeltaZ();
    public abstract void setDeltaX(double dx);
    public abstract void setDeltaY(double dy);
    public abstract void setDeltaZ(double dz);
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract void setYaw(float yaw);
    public abstract void setPitch(float pitch);

    @Deprecated
    public float getDeltaYaw() {
        return getYaw();
    }


    @Deprecated
    public float getDeltaPitch() {
        return getPitch();
    }


    @Deprecated
    public void setDeltaYaw(float deltaYaw) {
        setYaw(deltaYaw);
    }


    @Deprecated
    public void setDeltaPitch(float deltaPitch) {
        setPitch(deltaPitch);
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract boolean isOnGround();
    public abstract void setOnGround(boolean value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundMoveEntityPacketClass extends Template.Class<ClientboundMoveEntityPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();

        public final Template.Method<Double> getDeltaX = new Template.Method<Double>();
        public final Template.Method<Double> getDeltaY = new Template.Method<Double>();
        public final Template.Method<Double> getDeltaZ = new Template.Method<Double>();
        public final Template.Method<Void> setDeltaX = new Template.Method<Void>();
        public final Template.Method<Void> setDeltaY = new Template.Method<Void>();
        public final Template.Method<Void> setDeltaZ = new Template.Method<Void>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Void> setYaw = new Template.Method<Void>();
        public final Template.Method<Void> setPitch = new Template.Method<Void>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot")
    public abstract static class RotHandle extends ClientboundMoveEntityPacketHandle {
        /** @see RotClass */
        public static final RotClass T = Template.Class.create(RotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static RotHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static RotHandle createNew() {
            return T.createNew.invoke();
        }

        @Override
        public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
            return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_LOOK;
        }

        public static ClientboundMoveEntityPacketHandle.RotHandle createNew(int entityId, float yaw, float pitch, boolean onGround) {
            ClientboundMoveEntityPacketHandle.RotHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setYaw(yaw);
            handle.setPitch(pitch);
            handle.setOnGround(onGround);
            return handle;
        }
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class RotClass extends Template.Class<RotHandle> {
            public final Template.StaticMethod.Converted<RotHandle> createNew = new Template.StaticMethod.Converted<RotHandle>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos")
    public abstract static class PosHandle extends ClientboundMoveEntityPacketHandle {
        /** @see PosClass */
        public static final PosClass T = Template.Class.create(PosClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static PosHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static PosHandle createNew() {
            return T.createNew.invoke();
        }

        @Override
        public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
            return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_MOVE;
        }

        public static ClientboundMoveEntityPacketHandle.PosHandle createNew(int entityId, double dx, double dy, double dz, boolean onGround) {
            ClientboundMoveEntityPacketHandle.PosHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setDeltaX(dx);
            handle.setDeltaY(dy);
            handle.setDeltaZ(dz);
            handle.setOnGround(onGround);
            return handle;
        }
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Pos</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PosClass extends Template.Class<PosHandle> {
            public final Template.StaticMethod.Converted<PosHandle> createNew = new Template.StaticMethod.Converted<PosHandle>();

        }

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.PosRot</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.PosRot")
    public abstract static class PosRotHandle extends ClientboundMoveEntityPacketHandle {
        /** @see PosRotClass */
        public static final PosRotClass T = Template.Class.create(PosRotClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static PosRotHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static PosRotHandle createNew() {
            return T.createNew.invoke();
        }

        @Override
        public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
            return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_MOVE_LOOK;
        }

        public static ClientboundMoveEntityPacketHandle.PosRotHandle createNew(int entityId, double dx, double dy, double dz, float yaw, float pitch, boolean onGround) {
            ClientboundMoveEntityPacketHandle.PosRotHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setDeltaX(dx);
            handle.setDeltaY(dy);
            handle.setDeltaZ(dz);
            handle.setYaw(yaw);
            handle.setPitch(pitch);
            handle.setOnGround(onGround);
            return handle;
        }
        /**
         * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.PosRot</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class PosRotClass extends Template.Class<PosRotHandle> {
            public final Template.StaticMethod.Converted<PosRotHandle> createNew = new Template.StaticMethod.Converted<PosRotHandle>();

        }

    }

}

