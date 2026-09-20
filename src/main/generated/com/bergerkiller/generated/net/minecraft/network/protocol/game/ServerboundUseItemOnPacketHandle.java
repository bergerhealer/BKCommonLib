package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.BlockHitResultHandle;

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

    public static ServerboundUseItemOnPacketHandle createNew(HumanHandRole handRole, BlockHitResultHandle blockHitResult, int sequence, long timestamp) {
        return T.createNew.invoke(handRole, blockHitResult, sequence, timestamp);
    }

    public abstract BlockHitResultHandle getHitResult();
    public abstract HumanHandRole getHandRole();
    public abstract boolean isBlockPlacePacket();
    public abstract void setBlockPlacePacket();
    public abstract int getSequence();
    public abstract long getTimestamp();
    public ServerboundUseItemOnPacketHandle withHandRole(HumanHandRole handRole) {
        return createNew(handRole, getHitResult(), getSequence(), getTimestamp());
    }

    public ServerboundUseItemOnPacketHandle withHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        return withHandRole(hand.getRoleOf(humanEntity));
    }

    public ServerboundUseItemOnPacketHandle withHitResult(BlockHitResultHandle hitResult) {
        return createNew(getHandRole(), hitResult, getSequence(), getTimestamp());
    }

    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public com.bergerkiller.bukkit.common.bases.IntVector3 getBlockPos() {
        return getHitResult().getBlockPos();
    }

    public org.bukkit.util.Vector getLocation() {
        return getHitResult().getLocation();
    }

    public org.bukkit.block.BlockFace getDirection() {
        return getHitResult().getDirection();
    }

    public org.bukkit.util.Vector getRelativePosition() {
        return getHitResult().getRelativePosition();
    }

    @Deprecated
    public float getDeltaX() {
        return (float) getRelativePosition().getX();
    }

    @Deprecated
    public float getDeltaY() {
        return (float) getRelativePosition().getY();
    }

    @Deprecated
    public float getDeltaZ() {
        return (float) getRelativePosition().getZ();
    }

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.IN_USE_ITEM_ON;
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundUseItemOnPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundUseItemOnPacketClass extends Template.Class<ServerboundUseItemOnPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundUseItemOnPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundUseItemOnPacketHandle>();

        public final Template.Method.Converted<BlockHitResultHandle> getHitResult = new Template.Method.Converted<BlockHitResultHandle>();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();
        public final Template.Method<Boolean> isBlockPlacePacket = new Template.Method<Boolean>();
        public final Template.Method<Void> setBlockPlacePacket = new Template.Method<Void>();
        public final Template.Method<Integer> getSequence = new Template.Method<Integer>();
        public final Template.Method<Long> getTimestamp = new Template.Method<Long>();

    }

}

