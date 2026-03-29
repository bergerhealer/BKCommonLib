package com.bergerkiller.generated.net.minecraft.network.chat;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import org.bukkit.ChatColor;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.chat.Component</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.chat.Component")
public abstract class ComponentHandle extends Template.Handle {
    /** @see ComponentClass */
    public static final ComponentClass T = Template.Class.create(ComponentClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ComponentHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static String chatComponentToJson(ComponentHandle chatComponent) {
        return T.chatComponentToJson.invoke(chatComponent);
    }

    public static ComponentHandle jsonToChatComponent(String jsonString) {
        return T.jsonToChatComponent.invoke(jsonString);
    }

    public static ComponentHandle nbtToChatComponent(CommonTag nbt) {
        return T.nbtToChatComponent.invoke(nbt);
    }

    public static CommonTag chatComponentToNBT(ComponentHandle chatComponent) {
        return T.chatComponentToNBT.invoke(chatComponent);
    }

    public static ComponentHandle empty() {
        return T.empty.invoke();
    }

    public static ComponentHandle newLine() {
        return T.newLine.invoke();
    }

    public static ComponentHandle modifiersToComponent(Collection<ChatColor> colors) {
        return T.modifiersToComponent.invoke(colors);
    }

    public abstract String getText();
    public abstract boolean isEmpty();
    public abstract ComponentHandle addSibling(ComponentHandle sibling);
    public abstract boolean isMutable();
    public abstract ComponentHandle createCopy();
    public abstract ComponentHandle setClickableURL(String url);
    public abstract ComponentHandle setClickableContent(String content);
    public abstract ComponentHandle setClickableSuggestedCommand(String command);
    public abstract ComponentHandle setClickableRunCommand(String command);
    public abstract ComponentHandle setHoverText(ComponentHandle hoverText);
    /**
     * Stores class members for <b>net.minecraft.network.chat.Component</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ComponentClass extends Template.Class<ComponentHandle> {
        public final Template.StaticMethod.Converted<String> chatComponentToJson = new Template.StaticMethod.Converted<String>();
        public final Template.StaticMethod.Converted<ComponentHandle> jsonToChatComponent = new Template.StaticMethod.Converted<ComponentHandle>();
        public final Template.StaticMethod.Converted<ComponentHandle> nbtToChatComponent = new Template.StaticMethod.Converted<ComponentHandle>();
        public final Template.StaticMethod.Converted<CommonTag> chatComponentToNBT = new Template.StaticMethod.Converted<CommonTag>();
        public final Template.StaticMethod.Converted<ComponentHandle> empty = new Template.StaticMethod.Converted<ComponentHandle>();
        public final Template.StaticMethod.Converted<ComponentHandle> newLine = new Template.StaticMethod.Converted<ComponentHandle>();
        public final Template.StaticMethod.Converted<ComponentHandle> modifiersToComponent = new Template.StaticMethod.Converted<ComponentHandle>();

        public final Template.Method<String> getText = new Template.Method<String>();
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method.Converted<ComponentHandle> addSibling = new Template.Method.Converted<ComponentHandle>();
        public final Template.Method<Boolean> isMutable = new Template.Method<Boolean>();
        public final Template.Method.Converted<ComponentHandle> createCopy = new Template.Method.Converted<ComponentHandle>();
        public final Template.Method.Converted<ComponentHandle> setClickableURL = new Template.Method.Converted<ComponentHandle>();
        public final Template.Method.Converted<ComponentHandle> setClickableContent = new Template.Method.Converted<ComponentHandle>();
        public final Template.Method.Converted<ComponentHandle> setClickableSuggestedCommand = new Template.Method.Converted<ComponentHandle>();
        public final Template.Method.Converted<ComponentHandle> setClickableRunCommand = new Template.Method.Converted<ComponentHandle>();
        public final Template.Method.Converted<ComponentHandle> setHoverText = new Template.Method.Converted<ComponentHandle>();

    }

}

