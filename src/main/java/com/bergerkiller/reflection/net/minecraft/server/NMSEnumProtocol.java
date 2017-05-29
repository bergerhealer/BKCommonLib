package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.EnumProtocolHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumProtocolDirectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.google.common.collect.BiMap;

import java.util.Map;

public class NMSEnumProtocol {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("EnumProtocol");

    private static final Object PLAY = EnumProtocolHandle.PLAY.getRaw();

    static class Direction {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("EnumProtocolDirection");
        public static final Object CLIENTBOUND = EnumProtocolDirectionHandle.CLIENTBOUND.getRaw();
        public static final Object SERVERBOUND = T.selectStaticValue("public static final EnumProtocolDirection SERVERBOUND");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final FieldAccessor<Map<Object, BiMap<Integer, Class<?>>>> packetMap = (FieldAccessor) EnumProtocolHandle.T.packetMap.raw.toFieldAccessor();

    public static Class<?> getPacketClassIn(Integer id) {
        return packetMap.get(PLAY).get(Direction.CLIENTBOUND).get(id);
    }

    public static Class<?> getPacketClassOut(Integer id) {
        return packetMap.get(PLAY).get(Direction.SERVERBOUND).get(id);
    }

    public static Integer getPacketIdIn(Class<?> packetClass) {
        BiMap<Integer, Class<?>> map = packetMap.get(PLAY).get(Direction.CLIENTBOUND);
        for (Integer i : map.keySet()) {
            if (map.get(i).equals(packetClass)) {
                return i;
            }
        }
        return -1;
    }

    public static Integer getPacketIdOut(Class<?> packetClass) {
        BiMap<Integer, Class<?>> map = packetMap.get(PLAY).get(Direction.SERVERBOUND);
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
     * @return id to which it is mapped, or null if not found
     */
    public static Integer getPacketId(Class<?> packetClass) {
        Integer id = getPacketIdIn(packetClass);
        if (id != null) {
            return id.intValue();
        }
        id = getPacketIdOut(packetClass);
        if (id != null) {
            return id.intValue();
        }
        return null;
    }
}
