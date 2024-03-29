package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.WindowType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutOpenWindow</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow")
public abstract class PacketPlayOutOpenWindowHandle extends PacketHandle {
    /** @see PacketPlayOutOpenWindowClass */
    public static final PacketPlayOutOpenWindowClass T = Template.Class.create(PacketPlayOutOpenWindowClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutOpenWindowHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutOpenWindowHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract WindowType getWindowType();
    public abstract void setWindowType(WindowType windowType);
    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract ChatText getWindowTitle();
    public abstract void setWindowTitle(ChatText value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutOpenWindow</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutOpenWindowClass extends Template.Class<PacketPlayOutOpenWindowHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<ChatText> windowTitle = new Template.Field.Converted<ChatText>();

        public final Template.StaticMethod.Converted<PacketPlayOutOpenWindowHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutOpenWindowHandle>();

        public final Template.Method<WindowType> getWindowType = new Template.Method<WindowType>();
        public final Template.Method<Void> setWindowType = new Template.Method<Void>();

    }

}

