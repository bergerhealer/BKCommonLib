package com.bergerkiller.bukkit.common.reflection.classes;

import java.lang.reflect.Field;
import java.util.HashMap;
import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.ProtocolLib;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class PacketFieldRef {
	public static HashMap<PacketType, HashMap<Integer, String>> fields = new HashMap<PacketType, HashMap<Integer, String>>();
	public static HashMap<PacketType, String> datawatchers = new HashMap<PacketType, String>();
	
	public static void init() {
		try {
			for(PacketType packet : PacketType.values()) {
				if(packet == PacketType.UNKOWN)
					continue;
				
				Object vp = packet.getPacket();
				
				Field[] Allfields;
				if(CommonPlugin.getInstance().libaryInstalled)
					Allfields = ProtocolLib.getFields(vp);
				else
					Allfields = vp.getClass().getDeclaredFields();
				
				HashMap<Integer, String> fieldNames = new HashMap<Integer, String>();
				for(Field field : Allfields) {
					if(field.getType().equals(Class.forName(Common.NMS_ROOT+".DataWatcher")))
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
	
	public static void write(Object packet, String field, Object value) {
		try {
			if(CommonPlugin.getInstance().libaryInstalled) {
				ProtocolLib.writeDataToPacket(packet, field, value);
			} else {
				SafeField.set(packet, field, value);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}
	
	public static Object read(Object packet, String field) {
		try {
			if(CommonPlugin.getInstance().libaryInstalled) {
				return ProtocolLib.readDataFromPacket(packet, field);
			} else {
				return SafeField.get(packet, field);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid field name: "+field);
		}
	}
}
