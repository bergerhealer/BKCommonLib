package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayInEntityAction</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayInEntityAction")
public abstract class PacketPlayInEntityActionHandle extends PacketHandle {
    /** @see PacketPlayInEntityActionClass */
    public static final PacketPlayInEntityActionClass T = Template.Class.create(PacketPlayInEntityActionClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInEntityActionHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getPlayerId();
    public abstract void setPlayerId(int value);
    public abstract Object getAction();
    public abstract void setAction(Object value);
    public abstract int getData();
    public abstract void setData(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayInEntityAction</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInEntityActionClass extends Template.Class<PacketPlayInEntityActionHandle> {
        public final Template.Field.Integer playerId = new Template.Field.Integer();
        public final Template.Field.Converted<Object> action = new Template.Field.Converted<Object>();
        public final Template.Field.Integer data = new Template.Field.Integer();

    }

}

