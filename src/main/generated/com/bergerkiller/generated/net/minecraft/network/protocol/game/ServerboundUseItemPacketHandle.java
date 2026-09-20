package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundUseItemPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundUseItemPacket")
public abstract class ServerboundUseItemPacketHandle extends PacketHandle {
    /** @see ServerboundUseItemPacketClass */
    public static final ServerboundUseItemPacketClass T = Template.Class.create(ServerboundUseItemPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundUseItemPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundUseItemPacketHandle createNew(HumanHandRole handRole, long timestamp, int sequence, float yaw, float pitch) {
        return T.createNew.invoke(handRole, timestamp, sequence, yaw, pitch);
    }

    public abstract int getSequence();
    public abstract float getYaw();
    public abstract float getPitch();
    public abstract HumanHandRole getHandRole();
    public abstract long getTimestamp();
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.IN_USE_ITEM;
    }

    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public ServerboundUseItemPacketHandle withRotation(float yaw, float pitch) {
        return createNew(getHandRole(), getTimestamp(), getSequence(), yaw, pitch);
    }

    public ServerboundUseItemPacketHandle withHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        return withHandRole(hand.getRoleOf(humanEntity));
    }

    public ServerboundUseItemPacketHandle withHandRole(HumanHandRole handRole) {
        return createNew(handRole, getTimestamp(), getSequence(), getYaw(), getPitch());
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundUseItemPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundUseItemPacketClass extends Template.Class<ServerboundUseItemPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundUseItemPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundUseItemPacketHandle>();

        public final Template.Method<Integer> getSequence = new Template.Method<Integer>();
        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();
        public final Template.Method<Long> getTimestamp = new Template.Method<Long>();

    }

}

