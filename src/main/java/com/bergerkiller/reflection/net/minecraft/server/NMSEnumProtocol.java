package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.EnumProtocolHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumProtocolDirectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.google.common.collect.BiMap;

import java.util.Map;

@Deprecated
public class NMSEnumProtocol {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EnumProtocol");

    private static final Object PLAY = EnumProtocolHandle.PLAY.getRaw();
    private static final Object CLIENTBOUND = EnumProtocolDirectionHandle.CLIENTBOUND.getRaw();
    private static final Object SERVERBOUND = EnumProtocolDirectionHandle.SERVERBOUND.getRaw();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final FieldAccessor<Map<Object, BiMap<Integer, Class<?>>>> packetMap = (FieldAccessor) EnumProtocolHandle.T.packetMap.raw.toFieldAccessor();

    public static Class<?> getPacketClassIn(Integer id) {
        return packetMap.get(PLAY).get(SERVERBOUND).get(id);
    }

    public static Class<?> getPacketClassOut(Integer id) {
        return packetMap.get(PLAY).get(CLIENTBOUND).get(id);
    }

    public static int getPacketIdIn(Class<?> packetClass) {
        BiMap<Integer, Class<?>> map = packetMap.get(PLAY).get(SERVERBOUND);
        for (Integer i : map.keySet()) {
            if (map.get(i).equals(packetClass)) {
                return i;
            }
        }
        return -1;
    }

    public static int getPacketIdOut(Class<?> packetClass) {
        BiMap<Integer, Class<?>> map = packetMap.get(PLAY).get(CLIENTBOUND);
        for (Integer i : map.keySet()) {
            if (map.get(i).equals(packetClass)) {
                return i;
            }
        }
        return -1;
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
