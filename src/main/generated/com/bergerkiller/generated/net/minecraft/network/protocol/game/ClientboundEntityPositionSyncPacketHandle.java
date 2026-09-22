package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket")
public abstract class ClientboundEntityPositionSyncPacketHandle extends PacketHandle {
    /** @see ClientboundEntityPositionSyncPacketClass */
    public static final ClientboundEntityPositionSyncPacketClass T = Template.Class.create(ClientboundEntityPositionSyncPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundEntityPositionSyncPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundEntityPositionSyncPacketHandle createNewForEntity(Entity entity) {
        return T.createNewForEntity.invoke(entity);
    }

    public static ClientboundEntityPositionSyncPacketHandle createNew(int entityId, Position pos, float yaw, float pitch, boolean onGround) {
        return T.createNew.invoke(entityId, pos, yaw, pitch, onGround);
    }

    public abstract int getEntityId();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract boolean isOnGround();
    public abstract Position getPosition();
    public abstract int getEncodedYaw();
    public abstract int getEncodedPitch();
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_ENTITY_TELEPORT;
    }

    public static ClientboundEntityPositionSyncPacketHandle createNew(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        return createNew(entityId, Position.encodeLinear(posX, posY, posZ), yaw, pitch, onGround);
    }

    /** @deprecated Use {@link #getPosition()} instead */
    @Deprecated
    public double getPosX() {
        return getPosition().getX();
    }

    /** @deprecated Use {@link #getPosition()} instead */
    @Deprecated
    public double getPosY() {
        return getPosition().getY();
    }

    /** @deprecated Use {@link #getPosition()} instead */
    @Deprecated
    public double getPosZ() {
        return getPosition().getZ();
    }

    /**
     * Represents the updated position of an entity, encoded in the way that the Minecraft protocol expects.
     * The encoding used depends on the Minecraft version.
     */
    public interface Position {
        Position UNSET = new LinearPosition(Double.NaN, Double.NaN, Double.NaN);

        /**
         * Gets the X-component of the new position the client will teleport the entity to
         *
         * @return new position X
         */
        double getX();

        /**
         * Gets the Y-component of the new position the client will teleport the entity to
         *
         * @return new position Y
         */
        double getY();

        /**
         * Gets the Z-component of the new position the client will teleport the entity to
         *
         * @return new position Z
         */
        double getZ();

        /**
         * Creates a new, initially-empty, stepped position update. Add steps to it (chaining), then assign it
         * to a ClientboundEntityPositionSyncPacket packet position field.
         * Multi-step changes are only supported on Minecraft 26.3 and later.
         *
         * @return stepped position
         */
        static SteppedPosition steppedBuilder() {
            return new SteppedPosition();
        }

        /**
         * Creates a new Position instance that encodes the given position values, teleporting the
         * the player towards that position following linear interpolation.
         * The encoding used depends on the Minecraft version.
         *
         * @param x New position to encode, X-coordinate
         * @param y New position to encode, Y-coordinate
         * @param z New position to encode, Z-coordinate
         * @return position instance that can be assigned to a ClientboundEntityPositionSyncPacket packet position field
         */
        static Position encodeLinear(double x, double y, double z) {
            if (com.bergerkiller.bukkit.common.internal.CommonCapabilities.PROTOCOL_TELEPORT_IS_DOUBLE_POSITION) {
                return new LinearPosition(x, y, z);
            } else {
                int encodedX = (int) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(x);
                int encodedY = (int) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(y);
                int encodedZ = (int) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(z);
                return new LinearPositionLegacy(encodedX, encodedY, encodedZ);
            }
        }
    }

    /**
     * Encoding used since 1.9, where the position is encoded as double.
     * Interpolates the movement over the standard entity interpolation tick time (usually 3 ticks),
     * and moves the entity linearly.
     */
    public static class LinearPosition implements Position {
        private final double x;
        private final double y;
        private final double z;

        public LinearPosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getZ() {
            return z;
        }
    }

    /**
     * Encoding used before 1.9, where the position is encoded as an int..
     * Interpolates the movement over the standard entity interpolation tick time (usually 3 ticks),
     * and moves the entity linearly.
     */
    public static final class LinearPositionLegacy implements Position {
        public final int encodedX;
        public final int encodedY;
        public final int encodedZ;

        public LinearPositionLegacy(int encodedX, int encodedY, int encodedZ) {
            this.encodedX = encodedX;
            this.encodedY = encodedY;
            this.encodedZ = encodedZ;
        }

        @Override
        public double getX() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(encodedX);
        }

        @Override
        public double getY() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(encodedY);
        }

        @Override
        public double getZ() {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(encodedZ);
        }
    }

    /**
     * Encoding used since 26.3, where the position is encoded as a list of steps.
     * Each step has its own absolute position and duration in ticks to interpolate over.
     * Multi-step changes are only supported on Minecraft 26.3 and later.
     */
    public static final class SteppedPosition implements Position {
        public final java.util.List<Step> steps;

        public SteppedPosition() {
            this(new java.util.ArrayList<>());
        }

        public SteppedPosition(java.util.List<Step> steps) {
            this.steps = steps;
        }

        /**
         * Gets the list of steps that make up this multi-step position update.
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
         * @return this, for chaining
         */
        public SteppedPosition addStep(Step step) {
            steps.add(step);
            return this;
        }

        /**
         * Adds a new position step to this multi-step position update, encoding the given position and tick duration as a single step.
         * Multi-step changes are only supported on Minecraft 26.3 and later.
         *
         * @param x Step postion, X-coordinate
         * @param y Step postion, Y-coordinate
         * @param z Step postion, Z-coordinate
         * @param ticks Duration of this step in ticks
         * @return this, for chaining
         */
        public SteppedPosition addStep(double x, double y, double z, int ticks) {
            steps.add(new Step(x, y, z, ticks));
            return this;
        }

        @Override
        public double getX() {
            int count = steps.size();
            return count == 0 ? Double.NaN : steps.get(count - 1).getX();
        }

        @Override
        public double getY() {
            int count = steps.size();
            return count == 0 ? Double.NaN : steps.get(count - 1).getY();
        }

        @Override
        public double getZ() {
            int count = steps.size();
            return count == 0 ? Double.NaN : steps.get(count - 1).getZ();
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
         * Represents a single step in a multi-step position teleport.
         * Each step has its own position and duration in ticks.
         */
        public static final class Step extends LinearPosition {
            public final int ticks;

            public Step(double x, double y, double z, int ticks) {
                super(x, y, z);
                this.ticks = ticks;
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int entityId;
        private Position pos = Position.UNSET;
        private float yaw, pitch;
        private boolean onGround;

        public Builder entityId(int entityId) { this.entityId = entityId; return this; }

        /** @deprecated Use the position() style methods instead */
        @Deprecated
        public Builder posX(double posX) {
            return position(posX, pos.getY(), pos.getZ());
        }
        /** @deprecated Use the position() style methods instead */
        @Deprecated
        public Builder posY(double posY) {
            return position(pos.getX(), posY, pos.getZ());
        }
        /** @deprecated Use the position() style methods instead */
        @Deprecated
        public Builder posZ(double posZ) {
            return position(pos.getX(), pos.getY(), posZ);
        }

        public Builder position(org.bukkit.util.Vector position) {
            return position(position.getX(), position.getY(), position.getZ());
        }
        public Builder position(double x, double y, double z) {
            return position(Position.encodeLinear(x, y, z));
        }
        public Builder position(Position pos) { this.pos = pos; return this; }
        public Builder yaw(float yaw) { this.yaw = yaw; return this; }
        public Builder pitch(float pitch) { this.pitch = pitch; return this; }
        public Builder rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
            return this;
        }
        public Builder onGround(boolean onGround) { this.onGround = onGround; return this; }

        public ClientboundEntityPositionSyncPacketHandle create() {
            return ClientboundEntityPositionSyncPacketHandle.createNew(entityId, pos, yaw, pitch, onGround);
        }
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundEntityPositionSyncPacketClass extends Template.Class<ClientboundEntityPositionSyncPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundEntityPositionSyncPacketHandle> createNewForEntity = new Template.StaticMethod.Converted<ClientboundEntityPositionSyncPacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundEntityPositionSyncPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundEntityPositionSyncPacketHandle>();

        public final Template.Method<Integer> getEntityId = new Template.Method<Integer>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Boolean> isOnGround = new Template.Method<Boolean>();
        public final Template.Method<Position> getPosition = new Template.Method<Position>();
        public final Template.Method<Integer> getEncodedYaw = new Template.Method<Integer>();
        public final Template.Method<Integer> getEncodedPitch = new Template.Method<Integer>();

    }

}

