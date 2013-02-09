package com.bergerkiller.bukkit.common.protocol;

import org.bukkit.Bukkit;

import net.minecraft.server.v1_4_R1.Packet;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class ProtocolLib {
	public static void enable(CommonPlugin plugin_) {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		pm.addPacketListener(new PacketAdapter(plugin_, ConnectionSide.BOTH,
				ListenerPriority.NORMAL, Packets.Client.getSupported()) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Packet vanilla = (Packet)packet.getHandle();
				
				CommonPacket cp = new CommonPacket(vanilla);
				PacketReceiveEvent ev = new PacketReceiveEvent(event.getPlayer(), cp);
				Bukkit.getServer().getPluginManager().callEvent(ev);
				
				if(ev.isCancelled())
					event.setCancelled(true);
			}
			
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Packet vanilla = (Packet)packet.getHandle();
				
				CommonPacket cp = new CommonPacket(vanilla);
				PacketSendEvent ev = new PacketSendEvent(event.getPlayer(), cp);
				Bukkit.getServer().getPluginManager().callEvent(ev);
				
				if(ev.isCancelled())
					event.setCancelled(true);
			}
		});
	}
}
