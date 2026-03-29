package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundTabListPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundTabListPacket")
public abstract class ClientboundTabListPacketHandle extends PacketHandle {
    /** @see ClientboundTabListPacketClass */
    public static final ClientboundTabListPacketClass T = Template.Class.create(ClientboundTabListPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundTabListPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChatText getHeader();
    public abstract void setHeader(ChatText value);
    public abstract ChatText getFooter();
    public abstract void setFooter(ChatText value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundTabListPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundTabListPacketClass extends Template.Class<ClientboundTabListPacketHandle> {
        public final Template.Field.Converted<ChatText> header = new Template.Field.Converted<ChatText>();
        public final Template.Field.Converted<ChatText> footer = new Template.Field.Converted<ChatText>();

    }

}

