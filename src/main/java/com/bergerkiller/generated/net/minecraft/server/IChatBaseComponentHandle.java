package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IChatBaseComponentHandle extends Template.Handle {
    public static final IChatBaseComponentClass T = new IChatBaseComponentClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IChatBaseComponentHandle.class, "net.minecraft.server.IChatBaseComponent");

    /* ============================================================================== */

    public static IChatBaseComponentHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IChatBaseComponentHandle handle = new IChatBaseComponentHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class IChatBaseComponentClass extends Template.Class<IChatBaseComponentHandle> {
    }


    public static class ChatSerializerHandle extends Template.Handle {
        public static final ChatSerializerClass T = new ChatSerializerClass();
        static final StaticInitHelper _init_helper = new StaticInitHelper(ChatSerializerHandle.class, "net.minecraft.server.IChatBaseComponent.ChatSerializer");

        /* ============================================================================== */

        public static ChatSerializerHandle createHandle(Object handleInstance) {
            if (handleInstance == null) return null;
            ChatSerializerHandle handle = new ChatSerializerHandle();
            handle.instance = handleInstance;
            return handle;
        }

        /* ============================================================================== */

        public static String chatComponentToJson(IChatBaseComponentHandle chatComponent) {
            return T.chatComponentToJson.invokeVA(chatComponent);
        }

        public static IChatBaseComponentHandle jsonToChatComponent(String jsonString) {
            return T.jsonToChatComponent.invokeVA(jsonString);
        }

        public static final class ChatSerializerClass extends Template.Class<ChatSerializerHandle> {
            public final Template.StaticMethod.Converted<String> chatComponentToJson = new Template.StaticMethod.Converted<String>();
            public final Template.StaticMethod.Converted<IChatBaseComponentHandle> jsonToChatComponent = new Template.StaticMethod.Converted<IChatBaseComponentHandle>();

        }

    }

}

