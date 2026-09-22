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

    public abstract PositionChange getPositionChange();
    public abstract void setPositionChange(PositionChange change);
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract void setYaw(float yaw);
    public abstract void setPitch(float pitch);
    /**
     * Deprecated: is not actually a delta, use getYaw() instead
     */
    @Deprecated
    public float getDeltaYaw() {
        return getYaw();
    }

    /**
     * Deprecated: is not actually a delta, use getPitch() instead
     */
    @Deprecated
    public float getDeltaPitch() {
        return getPitch();
    }

    /**
     * Deprecated: is not actually a delta, use setYaw(yaw) instead
     */
    @Deprecated
    public void setDeltaYaw(float deltaYaw) {
        setYaw(deltaYaw);
    }

    /**
     * Deprecated: is not actually a delta, use setPitch(pitch) instead
     */
    @Deprecated
    public void setDeltaPitch(float deltaPitch) {
        setPitch(deltaPitch);
    }

    /** @deprecated Use getPositionChange() instead */
    @Deprecated
    public double getDeltaX() {
        return getPositionChange().getDeltaX();
    }

    /** @deprecated Use getPositionChange() instead */
    @Deprecated
    public double getDeltaY() {
        return getPositionChange().getDeltaY();
    }

    /** @deprecated Use getPositionChange() instead */
    @Deprecated
    public double getDeltaZ() {
        return getPositionChange().getDeltaZ();
    }

    /** @deprecated Use {@link #setPositionChange(PositionChange)} instead */
    @Deprecated
    public void setDeltaX(double dx) {
        PositionChange change = getPositionChange();
        setPositionChange(PositionChange.encodeLinearChange(dx, change.getDeltaY(), change.getDeltaZ()));
    }

    /** @deprecated Use {@link #setPositionChange(PositionChange)} instead */
    @Deprecated
    public void setDeltaY(double dy) {
        PositionChange change = getPositionChange();
        setPositionChange(PositionChange.encodeLinearChange(change.getDeltaX(), dy, change.getDeltaZ()));
    }

    /** @deprecated Use {@link #setPositionChange(PositionChange)} instead */
    @Deprecated
    public void setDeltaZ(double dz) {
        PositionChange change = getPositionChange();
        setPositionChange(PositionChange.encodeLinearChange(change.getDeltaX(), change.getDeltaY(), dz));
    }

    /**
     * Represents a change in position of an entity, encoded in the way that the Minecraft protocol expects.
     * The encoding used depends on the Minecraft version.
     */
    public interface PositionChange {
        /**
         * Gets the X-component of the movement change the client will perform with this position change
         *
         * @return delta movement X
         */
        double getDeltaX();

        /**
         * Gets the Y-component of the movement change the client will perform with this position change
         *
         * @return delta movement Y
         */
        double getDeltaY();

        /**
         * Gets the Z-component of the movement change the client will perform with this position change
         *
         * @return delta movement Z
         */
        double getDeltaZ();

        /**
         * Creates a new Change instance that encodes the given delta values.
         * The encoding used depends on the Minecraft version.
         *
         * @param deltaX Delta movement to encode, X-coordinate
         * @param deltaY Delta movement to encode, Y-coordinate
         * @param deltaZ Delta movement to encode, Z-coordinate
         * @return change instance that can be assigned to a ClientboundMoveEntityPacket packet change field
         */
        static PositionChange encodeLinearChange(double deltaX, double deltaY, double deltaZ) {
            if (com.bergerkiller.bukkit.common.internal.CommonCapabilities.PROTOCOL_MOVEMENT_IS_SHORT_DELTA) {
                short encodedDeltaX = (short) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(deltaX);
                short encodedDeltaY = (short) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(deltaY);
                short encodedDeltaZ = (short) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(deltaZ);
                return new LinearPositionChange(encodedDeltaX, encodedDeltaY, encodedDeltaZ);
            } else {
                byte encodedDeltaX = (byte) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(deltaX);
                byte encodedDeltaY = (byte) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(deltaY);
                byte encodedDeltaZ = (byte) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(deltaZ);
                return new LinearPositionChangeLegacy(encodedDeltaX, encodedDeltaY, encodedDeltaZ);
            }
        }
    }

    /**
     * Encoding used since 1.10.2, where the delta is encoded as a short.
     * Interpolates the movement over the standard entity interpolation tick time (usually 3 ticks),
     * and moves the entity linearly.
     */
    public static class LinearPositionChange implements PositionChange {
        public final short encodedDeltaX;
        public final short encodedDeltaY;
        public final short encodedDeltaZ;

        public LinearPositionChange(short encodedDeltaX, short encodedDeltaY, short encodedDeltaZ) {
            this.encodedDeltaX = encodedDeltaX;
            this.encodedDeltaY = encodedDeltaY;
            this.encodedDeltaZ = encodedDeltaZ;
        }

        @Override
        public double getDeltaX() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_10_2(encodedDeltaX);
        }

        @Override
        public double getDeltaY() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_10_2(encodedDeltaY);
        }

        @Override
        public double getDeltaZ() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_10_2(encodedDeltaZ);
        }
    }

    /**
     * Encoding used since 26.3, where the delta is encoded as a list of steps.
     * Each step has its own delta and duration in ticks.
     * Multi-step changes are only supported on Minecraft 26.3 and later.
     */
    public static final class SteppedPositionChange implements PositionChange {
        public final java.util.List<Step> steps;

        public SteppedPositionChange() {
            this(new java.util.ArrayList<>());
        }

        public SteppedPositionChange(java.util.List<Step> steps) {
            this.steps = steps;
        }

        /**
         * Gets the list of steps that make up this multi-step change.
         * Multi-step changes are only supported on Minecraft 26.3 and later.
         *
         * @return list of steps
         */
        public java.util.List<Step> getSteps() {
            return steps;
        }

        /**
         * Adds a new step to this multi-step change.
         * Multi-step changes are only supported on Minecraft 26.3 and later.
         *
         * @param step Step to add
         */
        public void addStep(Step step) {
            steps.add(step);
        }

        /**
         * Adds a new step to this multi-step change, encoding the given delta values and tick duration as a single step.
         * Multi-step changes are only supported on Minecraft 26.3 and later.
         *
         * @param deltaX Delta movement to encode, X-coordinate
         * @param deltaY Delta movement to encode, Y-coordinate
         * @param deltaZ Delta movement to encode, Z-coordinate
         * @param ticks Duration of this step in ticks
         */
        public void addStep(double deltaX, double deltaY, double deltaZ, int ticks) {
            steps.add(encodeStep(deltaX, deltaY, deltaZ, ticks));
        }

        @Override
        public double getDeltaX() {
            double sum = 0.0;
            for (Step change : steps) {
                sum += change.getDeltaX();
            }
            return sum;
        }

        @Override
        public double getDeltaY() {
            double sum = 0.0;
            for (Step change : steps) {
                sum += change.getDeltaY();
            }
            return sum;
        }

        @Override
        public double getDeltaZ() {
            double sum = 0.0;
            for (Step change : steps) {
                sum += change.getDeltaZ();
            }
            return sum;
        }

        /**
         * Gets the total number of ticks that this multi-step change will take to complete.
         *
         * @return total ticks
         */
        public int getTicks() {
            int sum = 0;
            for (Step change : steps) {
                sum += change.ticks;
            }
            return sum;
        }

        /**
         * Creates a new Step instance that encodes the given delta values and tick duration as a single step.
         * Multi-step changes are only supported on Minecraft 26.3 and later.
         *
         * @param deltaX Delta movement to encode, X-coordinate
         * @param deltaY Delta movement to encode, Y-coordinate
         * @param deltaZ Delta movement to encode, Z-coordinate
         * @param ticks Duration of this step in ticks
         * @return step instance that can be added to MultiPositionChange
         */
        public static Step encodeStep(double deltaX, double deltaY, double deltaZ, int ticks) {
            short encodedDeltaX = (short) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(deltaX);
            short encodedDeltaY = (short) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(deltaY);
            short encodedDeltaZ = (short) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(deltaZ);
            return new Step(encodedDeltaX, encodedDeltaY, encodedDeltaZ, ticks);
        }

        /**
         * Represents a single step in a multi-step position change.
         * Each step has its own delta and duration in ticks.
         */
        public static final class Step extends LinearPositionChange {
            public final int ticks;

            public Step(short encodedDeltaX, short encodedDeltaY, short encodedDeltaZ, int ticks) {
                super(encodedDeltaX, encodedDeltaY, encodedDeltaZ);
                this.ticks = ticks;
            }
        }
    }

    /**
     * Encoding used before 1.10.2, where the delta is encoded as a byte.
     * Interpolates the movement over the standard entity interpolation tick time (usually 3 ticks),
     * and moves the entity linearly.
     */
    public static final class LinearPositionChangeLegacy implements PositionChange {
        public final byte encodedDeltaX;
        public final byte encodedDeltaY;
        public final byte encodedDeltaZ;

        public LinearPositionChangeLegacy(byte encodedDeltaX, byte encodedDeltaY, byte encodedDeltaZ) {
            this.encodedDeltaX = encodedDeltaX;
            this.encodedDeltaY = encodedDeltaY;
            this.encodedDeltaZ = encodedDeltaZ;
        }

        @Override
        public double getDeltaX() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(encodedDeltaX);
        }

        @Override
        public double getDeltaY() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(encodedDeltaY);
        }

        @Override
        public double getDeltaZ() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(encodedDeltaZ);
        }
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

        public final Template.Method<PositionChange> getPositionChange = new Template.Method<PositionChange>();
        public final Template.Method<Void> setPositionChange = new Template.Method<Void>();
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
            return createNew(entityId, PositionChange.encodeLinearChange(dx, dy, dz), onGround);
        }

        public static ClientboundMoveEntityPacketHandle.PosHandle createNew(int entityId, PositionChange posChange, boolean onGround) {
            ClientboundMoveEntityPacketHandle.PosHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setPositionChange(posChange);
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
            return createNew(entityId, PositionChange.encodeLinearChange(dx, dy, dz), yaw, pitch, onGround);
        }

        public static ClientboundMoveEntityPacketHandle.PosRotHandle createNew(int entityId, PositionChange posChange, float yaw, float pitch, boolean onGround) {
            ClientboundMoveEntityPacketHandle.PosRotHandle handle = createNew();
            handle.setEntityId(entityId);
            handle.setPositionChange(posChange);
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

