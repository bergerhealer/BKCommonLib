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

    public abstract float getYaw();
    public abstract float getPitch();
    public abstract void setYaw(float yaw);
    public abstract void setPitch(float pitch);
    public abstract HumanHandRole getHandRole();
    public abstract void setHandRole(HumanHandRole handRole);
    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.IN_USE_ITEM;
    }

    public void setTimestamp(long timestamp) {
        if (T.timestamp.isAvailable()) {
            T.timestamp.setLong(getRaw(), timestamp);
        }
    }

    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public void setHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        setHandRole(hand.getRoleOf(humanEntity));
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundUseItemPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundUseItemPacketClass extends Template.Class<ServerboundUseItemPacketHandle> {
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();

        public final Template.Method<Float> getYaw = new Template.Method<Float>();
        public final Template.Method<Float> getPitch = new Template.Method<Float>();
        public final Template.Method<Void> setYaw = new Template.Method<Void>();
        public final Template.Method<Void> setPitch = new Template.Method<Void>();
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();
        public final Template.Method.Converted<Void> setHandRole = new Template.Method.Converted<Void>();

    }

}

