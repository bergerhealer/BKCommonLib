package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class PlayerConnectionRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("PlayerConnection");
	public static final FieldAccessor<Object> networkManager = TEMPLATE.getField("networkManager");
	public static final FieldAccessor<Boolean> checkMovement = TEMPLATE.getField("checkMovement");
	private static final MethodAccessor<Void> sendPacket = TEMPLATE.getMethod("sendPacket", PacketType.DEFAULT.getType());

	public static void sendPacket(Object instance, Object packet) {
		sendPacket.invoke(instance, packet);
	}
}
