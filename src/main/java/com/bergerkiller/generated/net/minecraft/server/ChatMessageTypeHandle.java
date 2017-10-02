package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChatMessageType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class ChatMessageTypeHandle extends Template.Handle {
    /** @See {@link ChatMessageTypeClass} */
    public static final ChatMessageTypeClass T = new ChatMessageTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChatMessageTypeHandle.class, "net.minecraft.server.ChatMessageType");

    /* ============================================================================== */

    public static ChatMessageTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object getRawById(byte id) {
        return T.getRawById.invoke(id);
    }

    public abstract byte getId();
    /**
     * Stores class members for <b>net.minecraft.server.ChatMessageType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChatMessageTypeClass extends Template.Class<ChatMessageTypeHandle> {
        public final Template.StaticMethod.Converted<Object> getRawById = new Template.StaticMethod.Converted<Object>();

        public final Template.Method<Byte> getId = new Template.Method<Byte>();

    }

}

