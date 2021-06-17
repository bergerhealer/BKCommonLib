package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect")
public abstract class PacketPlayOutKickDisconnectHandle extends Template.Handle {
    /** @See {@link PacketPlayOutKickDisconnectClass} */
    public static final PacketPlayOutKickDisconnectClass T = Template.Class.create(PacketPlayOutKickDisconnectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutKickDisconnectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChatText getReason();
    public abstract void setReason(ChatText value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutKickDisconnectClass extends Template.Class<PacketPlayOutKickDisconnectHandle> {
        public final Template.Field.Converted<ChatText> reason = new Template.Field.Converted<ChatText>();

    }

}

