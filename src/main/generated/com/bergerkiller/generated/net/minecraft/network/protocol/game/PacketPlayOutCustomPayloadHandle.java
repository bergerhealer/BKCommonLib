package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutCustomPayload</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutCustomPayload")
public abstract class PacketPlayOutCustomPayloadHandle extends PacketHandle {
    /** @see PacketPlayOutCustomPayloadClass */
    public static final PacketPlayOutCustomPayloadClass T = Template.Class.create(PacketPlayOutCustomPayloadClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutCustomPayloadHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutCustomPayloadHandle createNew(String channel, byte[] message) {
        return T.createNew.invoke(channel, message);
    }

    public abstract byte[] getMessage();

    @Override
    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.OUT_CUSTOM_PAYLOAD;
    }
    public abstract String getChannel();
    public abstract void setChannel(String value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutCustomPayload</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCustomPayloadClass extends Template.Class<PacketPlayOutCustomPayloadHandle> {
        public final Template.Field.Converted<String> channel = new Template.Field.Converted<String>();

        public final Template.StaticMethod.Converted<PacketPlayOutCustomPayloadHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutCustomPayloadHandle>();

        public final Template.Method<byte[]> getMessage = new Template.Method<byte[]>();

    }

}

