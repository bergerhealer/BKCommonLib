package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatMessageType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutChat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutChatHandle extends Template.Handle {
    /** @See {@link PacketPlayOutChatClass} */
    public static final PacketPlayOutChatClass T = new PacketPlayOutChatClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutChatHandle.class, "net.minecraft.server.PacketPlayOutChat");

    /* ============================================================================== */

    public static PacketPlayOutChatHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutChatHandle handle = new PacketPlayOutChatHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public ChatText getText() {
        return T.text.get(instance);
    }

    public void setText(ChatText value) {
        T.text.set(instance, value);
    }

    public Object[] getComponents() {
        return T.components.get(instance);
    }

    public void setComponents(Object[] value) {
        T.components.set(instance, value);
    }

    public ChatMessageType getType() {
        return T.type.get(instance);
    }

    public void setType(ChatMessageType value) {
        T.type.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutChat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutChatClass extends Template.Class<PacketPlayOutChatHandle> {
        public final Template.Field.Converted<ChatText> text = new Template.Field.Converted<ChatText>();
        public final Template.Field.Converted<Object[]> components = new Template.Field.Converted<Object[]>();
        public final Template.Field.Converted<ChatMessageType> type = new Template.Field.Converted<ChatMessageType>();

    }

}

