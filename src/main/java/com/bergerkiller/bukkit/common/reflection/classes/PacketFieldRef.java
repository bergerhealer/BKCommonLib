package com.bergerkiller.bukkit.common.reflection.classes;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.server.v1_4_R1.DataWatcher;
import net.minecraft.server.v1_4_R1.Packet;

import com.bergerkiller.bukkit.common.protocol.CommonPacket.Packets;
import com.bergerkiller.bukkit.common.protocol.PacketManager;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class PacketFieldRef {
	public static HashMap<Packets, HashMap<Integer, String>> fields = new HashMap<Packets, HashMap<Integer, String>>();
	public static HashMap<Packets, String> datawatchers = new HashMap<Packets, String>();
	
	public static void init() {
		try {
			for(Packets packet : Packets.values()) {
				Packet vp = packet.getPacket();
				
				Field[] Allfields;
				if(PacketManager.instance.libaryInstalled)
					Allfields = vp.getClass().getSuperclass().getDeclaredFields();
				else
					Allfields = vp.getClass().getDeclaredFields();
				
				HashMap<Integer, String> fieldNames = new HashMap<Integer, String>();
				for(Field field : Allfields) {
					if(field.getType().equals(DataWatcher.class))
						datawatchers.put(packet, field.getName());
					else
						fieldNames.put(fieldNames.size(), field.getName());
				}
				
				fields.put(packet, fieldNames);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void write(Packet packet, String field, Object value) {
		try {
			if(PacketManager.instance.libaryInstalled) {
				for(Field f : packet.getClass().getSuperclass().getDeclaredFields()) {
					if(f.getName().equals(field)) {
						f.set(packet, value);
					}
				}
			} else {
				SafeField.set(packet, field, value);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}
	
	public static Object read(Packet packet, String field) {
		try {
			if(PacketManager.instance.libaryInstalled) {
				for(Field f : packet.getClass().getSuperclass().getDeclaredFields()) {
					if(f.getName().equals(field)) {
						return f.get(packet);
					}
				}
			} else {
				return SafeField.get(packet, field);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
		return null;
	}
}
