package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import net.minecraft.server.v1_8_R2.EnumProtocol;
import net.minecraft.server.v1_8_R2.EnumProtocolDirection;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class EnumProtocolRef {
	
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EnumProtocol");
	private static final Object PLAY = EnumProtocol.PLAY;
	private static final MethodAccessor<Map<EnumProtocolDirection, Class<?>>> getInIdToPacketMap = TEMPLATE.getMethod("a");
	private static final MethodAccessor<Map<EnumProtocolDirection, Class<?>>> getOutIdToPacketMap = TEMPLATE.getMethod("a"); //b
											//Integer
	public static Class<?> getPacketClassIn(Integer id) {
		return getInIdToPacketMap.invoke(PLAY).get(id);
	}

	public static Class<?> getPacketClassOut(Integer id) {
		return getOutIdToPacketMap.invoke(PLAY).get(id);
	}

	public static EnumProtocolDirection getPacketIdIn(Class<?> packetClass) {
		return LogicUtil.getKeyAtValue(getInIdToPacketMap.invoke(PLAY), packetClass);
	}

	public static EnumProtocolDirection getPacketIdOut(Class<?> packetClass) {
		return LogicUtil.getKeyAtValue(getOutIdToPacketMap.invoke(PLAY), packetClass);
	}

	/**
	 * Tries to obtain the Packet ID to which a specific packet is mapped.
	 * 
	 * @param packetClass to get
	 * @return id to which it is mapped, or null if not found
	 */
	public static EnumProtocolDirection getPacketId(Class<?> packetClass) {
		EnumProtocolDirection id = getPacketIdIn(packetClass);
		if (id != null) {
			return id;
		}
		id = getPacketIdOut(packetClass);
		if (id != null) {
			return id;
		}
		return null;
	}
}