package com.bergerkiller.bukkit.common.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.classes.PacketRef;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.MonitorAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldUtils;

public class ProtocolLib {
	
	public static void enable(CommonPlugin plugin_) {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		
		pm.addPacketListener(new MonitorAdapter(plugin_, ConnectionSide.BOTH) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				
				if(!PacketUtil.callPacketReceiveEvent(player, packet.getHandle(), event.getPacketID()))
					event.setCancelled(true);
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				
				if(!PacketUtil.callPacketSendEvent(player, packet.getHandle(), event.getPacketID()))
					event.setCancelled(true);
			}
		});
	}
	
	public static void sendSilenVanillaPacket(Player player, Object packet) {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		
		PacketContainer toSend = new PacketContainer(PacketRef.packetID.get(packet), packet);
		try {
			pm.sendServerPacket(player, toSend, false);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Invalid packet target");
		}
	}
	
	public static void writeDataToPacket(Object packet, String field, Object value) throws IllegalAccessException {
		FieldUtils.writeField(packet, field, value);
	}
	
	public static Object readDataFromPacket(Object packet, String field) throws IllegalAccessException {
		return FieldUtils.readField(packet, field);
	}
	
	public static Field[] getFields(Object packet) {
		int id = PacketRef.packetID.get(packet);
		if(Packets.Client.isSupported(id) && Packets.Server.isSupported(id))
			return packet.getClass().getSuperclass().getDeclaredFields();
		else
			return packet.getClass().getDeclaredFields();
	}
}
