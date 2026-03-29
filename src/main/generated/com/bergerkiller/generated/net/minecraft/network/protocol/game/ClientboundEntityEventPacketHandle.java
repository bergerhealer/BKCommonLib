package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundEntityEventPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundEntityEventPacket")
public abstract class ClientboundEntityEventPacketHandle extends PacketHandle {
    /** @see ClientboundEntityEventPacketClass */
    public static final ClientboundEntityEventPacketClass T = Template.Class.create(ClientboundEntityEventPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundEntityEventPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    public abstract byte getEventId();
    public abstract void setEventId(byte value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundEntityEventPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundEntityEventPacketClass extends Template.Class<ClientboundEntityEventPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field.Byte eventId = new Template.Field.Byte();

    }

}

