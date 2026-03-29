package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ServerboundChatPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ServerboundChatPacket")
public abstract class ServerboundChatPacketHandle extends PacketHandle {
    /** @see ServerboundChatPacketClass */
    public static final ServerboundChatPacketClass T = Template.Class.create(ServerboundChatPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerboundChatPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getMessage();
    public abstract void setMessage(String value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ServerboundChatPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerboundChatPacketClass extends Template.Class<ServerboundChatPacketHandle> {
        public final Template.Field<String> message = new Template.Field<String>();

    }

}

