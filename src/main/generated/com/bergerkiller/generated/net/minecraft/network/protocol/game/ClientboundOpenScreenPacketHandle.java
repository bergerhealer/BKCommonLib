package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.WindowType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundOpenScreenPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundOpenScreenPacket")
public abstract class ClientboundOpenScreenPacketHandle extends PacketHandle {
    /** @see ClientboundOpenScreenPacketClass */
    public static final ClientboundOpenScreenPacketClass T = Template.Class.create(ClientboundOpenScreenPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundOpenScreenPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundOpenScreenPacketHandle createNew() {
        return T.createNew.invoke();
    }

    public abstract WindowType getWindowType();
    public abstract void setWindowType(WindowType windowType);
    public abstract int getWindowId();
    public abstract void setWindowId(int value);
    public abstract ChatText getWindowTitle();
    public abstract void setWindowTitle(ChatText value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundOpenScreenPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundOpenScreenPacketClass extends Template.Class<ClientboundOpenScreenPacketHandle> {
        public final Template.Field.Integer windowId = new Template.Field.Integer();
        public final Template.Field.Converted<ChatText> windowTitle = new Template.Field.Converted<ChatText>();

        public final Template.StaticMethod.Converted<ClientboundOpenScreenPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundOpenScreenPacketHandle>();

        public final Template.Method<WindowType> getWindowType = new Template.Method<WindowType>();
        public final Template.Method<Void> setWindowType = new Template.Method<Void>();

    }

}

