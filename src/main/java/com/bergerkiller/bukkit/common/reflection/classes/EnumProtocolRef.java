package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.google.common.collect.BiMap;
import net.minecraft.server.v1_9_R1.EnumProtocol;
import net.minecraft.server.v1_9_R1.EnumProtocolDirection;
import net.minecraft.server.v1_9_R1.Packet;

import java.util.Map;

public class EnumProtocolRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EnumProtocol");
    private static final Object PLAY = EnumProtocol.PLAY;
    public static final FieldAccessor<Map<EnumProtocolDirection, BiMap<Integer, Class<? extends Packet>>>> packetMap = TEMPLATE.getField("j");

    public static Class<?> getPacketClassIn(Integer id) {
        return packetMap.get(PLAY).get(EnumProtocolDirection.CLIENTBOUND).get(id);
    }

    public static Class<?> getPacketClassOut(Integer id) {
        return packetMap.get(PLAY).get(EnumProtocolDirection.SERVERBOUND).get(id);
    }

    public static Integer getPacketIdIn(Class<?> packetClass) {
        BiMap<Integer, Class<? extends Packet>> map = packetMap.get(PLAY).get(EnumProtocolDirection.CLIENTBOUND);
        for (Integer i : map.keySet()) {
            if (map.get(i).equals(packetClass)) {
                return i;
            }
        }
        return -1;
    }

    public static Integer getPacketIdOut(Class<?> packetClass) {
        BiMap<Integer, Class<? extends Packet>> map = packetMap.get(PLAY).get(EnumProtocolDirection.SERVERBOUND);
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
