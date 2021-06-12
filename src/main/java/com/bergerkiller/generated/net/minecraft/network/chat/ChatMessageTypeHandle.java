package com.bergerkiller.generated.net.minecraft.network.chat;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.chat.ChatMessageType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.network.chat.ChatMessageType")
public abstract class ChatMessageTypeHandle extends Template.Handle {
    /** @See {@link ChatMessageTypeClass} */
    public static final ChatMessageTypeClass T = Template.Class.create(ChatMessageTypeClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChatMessageTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getRawById(byte id) {
        return T.getRawById.invoker.invoke(null,id);
    }

    public abstract byte getId();
    /**
     * Stores class members for <b>net.minecraft.network.chat.ChatMessageType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChatMessageTypeClass extends Template.Class<ChatMessageTypeHandle> {
        public final Template.StaticMethod<Object> getRawById = new Template.StaticMethod<Object>();

        public final Template.Method<Byte> getId = new Template.Method<Byte>();

    }

}

