package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.ChatColor;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IChatBaseComponent</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.IChatBaseComponent")
public abstract class IChatBaseComponentHandle extends Template.Handle {
    /** @See {@link IChatBaseComponentClass} */
    public static final IChatBaseComponentClass T = Template.Class.create(IChatBaseComponentClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IChatBaseComponentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract IChatBaseComponentHandle addSibling(IChatBaseComponentHandle sibling);
    public abstract boolean isMutable();
    public abstract IChatBaseComponentHandle createCopy();
    public abstract IChatBaseComponentHandle setClickableURL(String url);
    public abstract IChatBaseComponentHandle setClickableContent(String content);
    public abstract IChatBaseComponentHandle setClickableSuggestedCommand(String command);
    public abstract IChatBaseComponentHandle setClickableRunCommand(String command);
    public abstract IChatBaseComponentHandle setHoverText(IChatBaseComponentHandle hoverText);
    /**
     * Stores class members for <b>net.minecraft.server.IChatBaseComponent</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IChatBaseComponentClass extends Template.Class<IChatBaseComponentHandle> {
        public final Template.Method.Converted<IChatBaseComponentHandle> addSibling = new Template.Method.Converted<IChatBaseComponentHandle>();
        public final Template.Method<Boolean> isMutable = new Template.Method<Boolean>();
        public final Template.Method.Converted<IChatBaseComponentHandle> createCopy = new Template.Method.Converted<IChatBaseComponentHandle>();
        public final Template.Method.Converted<IChatBaseComponentHandle> setClickableURL = new Template.Method.Converted<IChatBaseComponentHandle>();
        public final Template.Method.Converted<IChatBaseComponentHandle> setClickableContent = new Template.Method.Converted<IChatBaseComponentHandle>();
        public final Template.Method.Converted<IChatBaseComponentHandle> setClickableSuggestedCommand = new Template.Method.Converted<IChatBaseComponentHandle>();
        public final Template.Method.Converted<IChatBaseComponentHandle> setClickableRunCommand = new Template.Method.Converted<IChatBaseComponentHandle>();
        public final Template.Method.Converted<IChatBaseComponentHandle> setHoverText = new Template.Method.Converted<IChatBaseComponentHandle>();

    }


    /**
     * Instance wrapper handle for type <b>net.minecraft.server.IChatBaseComponent.ChatSerializer</b>.
     * To access members without creating a handle type, use the static {@link #T} member.
     * New handles can be created from raw instances using {@link #createHandle(Object)}.
     */
    @Template.InstanceType("net.minecraft.server.IChatBaseComponent.ChatSerializer")
    public abstract static class ChatSerializerHandle extends Template.Handle {
        /** @See {@link ChatSerializerClass} */
        public static final ChatSerializerClass T = Template.Class.create(ChatSerializerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
        /* ============================================================================== */

        public static ChatSerializerHandle createHandle(Object handleInstance) {
            return T.createHandle(handleInstance);
        }

        /* ============================================================================== */

        public static String chatComponentToJson(IChatBaseComponentHandle chatComponent) {
            return T.chatComponentToJson.invoke(chatComponent);
        }

        public static IChatBaseComponentHandle jsonToChatComponent(String jsonString) {
            return T.jsonToChatComponent.invoke(jsonString);
        }

        public static IChatBaseComponentHandle empty() {
            return T.empty.invoke();
        }

        public static IChatBaseComponentHandle modifiersToComponent(Collection<ChatColor> colors) {
            return T.modifiersToComponent.invoke(colors);
        }

        /**
         * Stores class members for <b>net.minecraft.server.IChatBaseComponent.ChatSerializer</b>.
         * Methods, fields, and constructors can be used without using Handle Objects.
         */
        public static final class ChatSerializerClass extends Template.Class<ChatSerializerHandle> {
            public final Template.StaticMethod.Converted<String> chatComponentToJson = new Template.StaticMethod.Converted<String>();
            public final Template.StaticMethod.Converted<IChatBaseComponentHandle> jsonToChatComponent = new Template.StaticMethod.Converted<IChatBaseComponentHandle>();
            public final Template.StaticMethod.Converted<IChatBaseComponentHandle> empty = new Template.StaticMethod.Converted<IChatBaseComponentHandle>();
            public final Template.StaticMethod.Converted<IChatBaseComponentHandle> modifiersToComponent = new Template.StaticMethod.Converted<IChatBaseComponentHandle>();

        }

    }

}

