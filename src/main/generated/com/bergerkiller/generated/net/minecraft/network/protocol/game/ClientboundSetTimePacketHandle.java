package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetTimePacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetTimePacket")
public abstract class ClientboundSetTimePacketHandle extends PacketHandle {
    /** @see ClientboundSetTimePacketClass */
    public static final ClientboundSetTimePacketClass T = Template.Class.create(ClientboundSetTimePacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetTimePacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract long getGameTime();
    public abstract void setGameTime(long value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetTimePacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetTimePacketClass extends Template.Class<ClientboundSetTimePacketHandle> {
        public final Template.Field.Long gameTime = new Template.Field.Long();

    }

}

