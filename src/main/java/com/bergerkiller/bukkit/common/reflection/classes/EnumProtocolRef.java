package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import net.minecraft.server.v1_8_R1.EnumProtocol;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class EnumProtocolRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EnumProtocol");
    private static final Object PLAY = EnumProtocol.PLAY;
    private static final MethodAccessor<Map<Integer, Class<?>>> getInIdToPacketMap = TEMPLATE.getMethod("a");
    private static final MethodAccessor<Map<Integer, Class<?>>> getOutIdToPacketMap = TEMPLATE.getMethod("b");

    public static Class<?> getPacketClassIn(Integer id) {
        return getInIdToPacketMap.invoke(PLAY).get(id);
    }

    public static Class<?> getPacketClassOut(Integer id) {
        return getOutIdToPacketMap.invoke(PLAY).get(id);
    }

    public static Integer getPacketIdIn(Class<?> packetClass) {
        return LogicUtil.getKeyAtValue(getInIdToPacketMap.invoke(PLAY), packetClass);
    }

    public static Integer getPacketIdOut(Class<?> packetClass) {
        return LogicUtil.getKeyAtValue(getOutIdToPacketMap.invoke(PLAY), packetClass);
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
