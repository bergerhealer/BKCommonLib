package com.bergerkiller.bukkit.common.reflection.classes;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_4_R1.Packet;

import com.bergerkiller.bukkit.common.protocol.CommonPacket.Packets;

public class PacketFieldRef {
	public static HashMap<Packets, HashMap<Integer, String>> fields = new HashMap<Packets, HashMap<Integer, String>>();
	
	public static void init() {
		try {
			for(Packets packet : Packets.values()) {
				Packet vp = packet.getPacket();
				Field[] Allfields = vp.getClass().getFields();
				
				HashMap<Integer, String> fieldNames = new HashMap<Integer, String>();
				for(Field field : Allfields) {
					fieldNames.put(fieldNames.size(), field.getName());
				}
				
				fields.put(packet, fieldNames);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
