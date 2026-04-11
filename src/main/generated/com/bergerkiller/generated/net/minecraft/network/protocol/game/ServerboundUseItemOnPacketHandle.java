package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundUseItemOnPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundUseItemOnPacket")
public abstract class ServerboundUseItemOnPacketHandle extends PacketHandle {
    /** @see ServerboundUseItemOnPacketClass */
    public static final ServerboundUseItemOnPacketClass T = Template.Class.create(ServerboundUseItemOnPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundUseItemOnPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract HumanHandRole getHandRole();
    public abstract void setHandRole(HumanHandRole handRole);
    public abstract BlockFace getDirection();
    public abstract void setDirection(BlockFace direction);
    public abstract boolean isBlockPlacePacket();
    public abstract void setBlockPlacePacket();
    public abstract IntVector3 getPosition();
    public abstract void setPosition(IntVector3 blockPosition);
    public abstract float getDeltaX();
    public abstract float getDeltaY();
    public abstract float getDeltaZ();
    public abstract void setDeltaX(float dx);
    public abstract void setDeltaY(float dy);
    public abstract void setDeltaZ(float dz);
    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public void setHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        setHandRole(hand.getRoleOf(humanEntity));
    }

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.IN_USE_ITEM;
    }

    public void setTimestamp(long timestamp) {
        if (T.timestamp.isAvailable()) {
            T.timestamp.setLong(getRaw(), timestamp);
        }
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundUseItemOnPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundUseItemOnPacketClass extends Template.Class<ServerboundUseItemOnPacketHandle> {
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();

        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();
        public final Template.Method.Converted<Void> setHandRole = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<BlockFace> getDirection = new Template.Method.Converted<BlockFace>();
        public final Template.Method.Converted<Void> setDirection = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isBlockPlacePacket = new Template.Method<Boolean>();
        public final Template.Method<Void> setBlockPlacePacket = new Template.Method<Void>();
        public final Template.Method.Converted<IntVector3> getPosition = new Template.Method.Converted<IntVector3>();
        public final Template.Method.Converted<Void> setPosition = new Template.Method.Converted<Void>();
        public final Template.Method<Float> getDeltaX = new Template.Method<Float>();
        public final Template.Method<Float> getDeltaY = new Template.Method<Float>();
        public final Template.Method<Float> getDeltaZ = new Template.Method<Float>();
        public final Template.Method<Void> setDeltaX = new Template.Method<Void>();
        public final Template.Method<Void> setDeltaY = new Template.Method<Void>();
        public final Template.Method<Void> setDeltaZ = new Template.Method<Void>();

    }

}

