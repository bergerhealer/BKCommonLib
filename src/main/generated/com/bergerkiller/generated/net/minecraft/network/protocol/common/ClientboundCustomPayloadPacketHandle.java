package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket")
public abstract class ClientboundCustomPayloadPacketHandle extends PacketHandle {
    /** @see ClientboundCustomPayloadPacketClass */
    public static final ClientboundCustomPayloadPacketClass T = Template.Class.create(ClientboundCustomPayloadPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundCustomPayloadPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundCustomPayloadPacketHandle createNew(String channel, byte[] message) {
        return T.createNew.invoke(channel, message);
    }

    public abstract String getChannel();
    public abstract byte[] getMessage();

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_CUSTOM_PAYLOAD;
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundCustomPayloadPacketClass extends Template.Class<ClientboundCustomPayloadPacketHandle> {
        public final Template.StaticMethod.Converted<ClientboundCustomPayloadPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundCustomPayloadPacketHandle>();

        public final Template.Method<String> getChannel = new Template.Method<String>();
        public final Template.Method<byte[]> getMessage = new Template.Method<byte[]>();

    }

}

