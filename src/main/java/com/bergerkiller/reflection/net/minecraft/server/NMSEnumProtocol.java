package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.network.EnumProtocolHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

@Deprecated
public class NMSEnumProtocol {
    public static final ClassTemplate<?> T = ClassTemplate.create(EnumProtocolHandle.T.getType());

    public static Class<?> getPacketClassIn(Integer id) {
        return EnumProtocolHandle.PLAY.getPacketClassIn(id.intValue());
    }

    public static Class<?> getPacketClassOut(Integer id) {
        return EnumProtocolHandle.PLAY.getPacketClassOut(id.intValue());
    }

    public static int getPacketIdIn(Class<?> packetClass) {
        return EnumProtocolHandle.PLAY.getPacketIdIn(packetClass);
    }

    public static int getPacketIdOut(Class<?> packetClass) {
        return EnumProtocolHandle.PLAY.getPacketIdOut(packetClass);
    }

    /**
     * Tries to obtain the Packet ID to which a specific packet is mapped.
     *
     * @param packetClass to get
     * @return id to which it is mapped, or -1 if not found
     */
    public static int getPacketId(Class<?> packetClass) {
        int id = getPacketIdIn(packetClass);
        if (id != -1) {
            return id;
        }
        id = getPacketIdOut(packetClass);
        if (id != -1) {
            return id;
        }
        return -1;
    }
}
