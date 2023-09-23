package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * @deprecated This class is moved to {@link com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundCustomPayloadPacketHandle}
 */
@Deprecated
public class PacketPlayOutCustomPayloadHandle extends PacketHandle {
    private final Object raw;

    private PacketPlayOutCustomPayloadHandle(Object raw) {
        this.raw = raw;
    }

    public static PacketPlayOutCustomPayloadHandle createNew(String channel, byte[] message) {
        return new PacketPlayOutCustomPayloadHandle(
                com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundCustomPayloadPacketHandle.createNew(
                        channel, message).getRaw());
    }

    @Override
    public Object getRaw() {
        return raw;
    }
}
