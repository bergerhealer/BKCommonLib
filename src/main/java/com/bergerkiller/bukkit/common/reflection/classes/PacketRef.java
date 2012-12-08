package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import net.minecraft.server.Packet;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class PacketRef {
	public static final Map<Class<?>, Integer> classToIds = SafeField.get(Packet.class, "a");
	public static final FieldAccessor<Integer> packetID = new SafeField<Integer>(Packet.class, "packetID");
}
