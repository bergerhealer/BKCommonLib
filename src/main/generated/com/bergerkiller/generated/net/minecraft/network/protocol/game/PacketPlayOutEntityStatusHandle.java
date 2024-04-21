package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutEntityStatus</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutEntityStatus")
public abstract class PacketPlayOutEntityStatusHandle extends PacketHandle {
    /** @see PacketPlayOutEntityStatusClass */
    public static final PacketPlayOutEntityStatusClass T = Template.Class.create(PacketPlayOutEntityStatusClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityStatusHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract byte getEventId();
    public abstract void setEventId(byte value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutEntityStatus</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityStatusClass extends Template.Class<PacketPlayOutEntityStatusHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Byte eventId = new Template.Field.Byte();

    }

}

