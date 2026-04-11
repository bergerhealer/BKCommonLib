package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.HumanHandRole;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundSwingPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundSwingPacket")
public abstract class ServerboundSwingPacketHandle extends PacketHandle {
    /** @see ServerboundSwingPacketClass */
    public static final ServerboundSwingPacketClass T = Template.Class.create(ServerboundSwingPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundSwingPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract HumanHandRole getHandRole();
    public abstract void setHandRole(HumanHandRole handRole);
    public com.bergerkiller.bukkit.common.wrappers.HumanHand getHand(org.bukkit.entity.HumanEntity humanEntity) {
        return getHandRole().getHandOf(humanEntity);
    }

    public void setHand(org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        setHandRole(hand.getRoleOf(humanEntity));
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundSwingPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundSwingPacketClass extends Template.Class<ServerboundSwingPacketHandle> {
        public final Template.Method.Converted<HumanHandRole> getHandRole = new Template.Method.Converted<HumanHandRole>();
        public final Template.Method.Converted<Void> setHandRole = new Template.Method.Converted<Void>();

    }

}

