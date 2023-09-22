package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ClientboundDisconnectPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ClientboundDisconnectPacket")
public abstract class ClientboundDisconnectPacketHandle extends PacketHandle {
    /** @see ClientboundDisconnectPacketClass */
    public static final ClientboundDisconnectPacketClass T = Template.Class.create(ClientboundDisconnectPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundDisconnectPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChatText getReason();
    public abstract void setReason(ChatText value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ClientboundDisconnectPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundDisconnectPacketClass extends Template.Class<ClientboundDisconnectPacketHandle> {
        public final Template.Field.Converted<ChatText> reason = new Template.Field.Converted<ChatText>();

    }

}

