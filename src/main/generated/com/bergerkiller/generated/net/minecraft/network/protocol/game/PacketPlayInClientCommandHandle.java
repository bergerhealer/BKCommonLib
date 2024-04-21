package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInClientCommand</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInClientCommand")
public abstract class PacketPlayInClientCommandHandle extends PacketHandle {
    /** @see PacketPlayInClientCommandClass */
    public static final PacketPlayInClientCommandClass T = Template.Class.create(PacketPlayInClientCommandClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInClientCommandHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Object getAction();
    public abstract void setAction(Object value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInClientCommand</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInClientCommandClass extends Template.Class<PacketPlayInClientCommandHandle> {
        public final Template.Field.Converted<Object> action = new Template.Field.Converted<Object>();

    }

}

