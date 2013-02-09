package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Map;

import net.minecraft.server.v1_4_R1.Packet;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class PacketRef {
	public static final ClassTemplate<Packet> TEMPLATE = ClassTemplate.create(Packet.class);
	public static final MethodAccessor<Packet> getPacketById = TEMPLATE.getMethod("d", int.class);
	public static final Map<Class<?>, Integer> classToIds = SafeField.get(Packet.class, "a");
	public static final FieldAccessor<Integer> packetID = new SafeField<Integer>(Packet.class, "packetID");
}
