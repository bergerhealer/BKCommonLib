package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatMessageType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutChat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutChat")
public abstract class PacketPlayOutChatHandle extends PacketHandle {
    /** @See {@link PacketPlayOutChatClass} */
    public static final PacketPlayOutChatClass T = Template.Class.create(PacketPlayOutChatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutChatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChatText getText();
    public abstract void setText(ChatText value);
    public abstract ChatMessageType getType();
    public abstract void setType(ChatMessageType value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutChat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutChatClass extends Template.Class<PacketPlayOutChatHandle> {
        public final Template.Field.Converted<ChatText> text = new Template.Field.Converted<ChatText>();
        @Template.Optional
        public final Template.Field.Converted<Object[]> components = new Template.Field.Converted<Object[]>();
        public final Template.Field.Converted<ChatMessageType> type = new Template.Field.Converted<ChatMessageType>();

    }

}

