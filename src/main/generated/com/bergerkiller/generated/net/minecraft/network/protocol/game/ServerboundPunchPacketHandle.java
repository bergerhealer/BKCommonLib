package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundPunchPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundPunchPacket")
public abstract class ServerboundPunchPacketHandle extends PacketHandle {
    /** @see ServerboundPunchPacketClass */
    public static final ServerboundPunchPacketClass T = Template.Class.create(ServerboundPunchPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundPunchPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ServerboundPunchPacketHandle createNew(HumanHandRole handRole) {
        return T.createNew.invoke(handRole);
    }

    public abstract HumanHandRole getHandRole();
    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public static ServerboundPunchPacketHandle createNew(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        return createNew(hand.getRoleOf(humanEntity));
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundPunchPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundPunchPacketClass extends Template.Class<ServerboundPunchPacketHandle> {
        public final Template.StaticMethod.Converted<ServerboundPunchPacketHandle> createNew = new Template.StaticMethod.Converted<ServerboundPunchPacketHandle>();

        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();

    }

}

