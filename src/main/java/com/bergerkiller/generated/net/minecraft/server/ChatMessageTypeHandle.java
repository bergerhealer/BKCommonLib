package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

@Template.Optional
public class ChatMessageTypeHandle extends Template.Handle {
    public static final ChatMessageTypeClass T = new ChatMessageTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChatMessageTypeHandle.class, "net.minecraft.server.ChatMessageType");

    /* ============================================================================== */

    public static ChatMessageTypeHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChatMessageTypeHandle handle = new ChatMessageTypeHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static Object getRawById(byte id) {
        return T.getRawById.invokeVA(id);
    }

    public byte getId() {
        return T.getId.invoke(instance);
    }

    public static final class ChatMessageTypeClass extends Template.Class<ChatMessageTypeHandle> {
        public final Template.StaticMethod.Converted<Object> getRawById = new Template.StaticMethod.Converted<Object>();

        public final Template.Method<Byte> getId = new Template.Method<Byte>();

    }

}

