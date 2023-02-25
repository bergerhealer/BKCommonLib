package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInChat</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInChat")
public abstract class PacketPlayInChatHandle extends PacketHandle {
    /** @See {@link PacketPlayInChatClass} */
    public static final PacketPlayInChatClass T = Template.Class.create(PacketPlayInChatClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInChatHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getMessage();
    public abstract void setMessage(String value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInChat</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInChatClass extends Template.Class<PacketPlayInChatHandle> {
        public final Template.Field<String> message = new Template.Field<String>();

    }

}

