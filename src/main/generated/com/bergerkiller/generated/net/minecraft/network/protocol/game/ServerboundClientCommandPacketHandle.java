package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundClientCommandPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundClientCommandPacket")
public abstract class ServerboundClientCommandPacketHandle extends PacketHandle {
    /** @see ServerboundClientCommandPacketClass */
    public static final ServerboundClientCommandPacketClass T = Template.Class.create(ServerboundClientCommandPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundClientCommandPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getAction();
    public abstract void setAction(Object value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundClientCommandPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundClientCommandPacketClass extends Template.Class<ServerboundClientCommandPacketHandle> {
        public final Template.Field.Converted<Object> action = new Template.Field.Converted<Object>();

    }

}

