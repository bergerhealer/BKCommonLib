package com.bergerkiller.bukkit.common.protocol;

import org.bukkit.entity.Player;

import net.minecraft.server.v1_4_R1.Packet;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.MonitorAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class ProtocolLib {
	
	public static void enable(CommonPlugin plugin_) {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		
		pm.addPacketListener(new MonitorAdapter(plugin_, ConnectionSide.BOTH) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Packet vanilla = (Packet)packet.getHandle();
				Player player = event.getPlayer();
				
				if(!PacketUtil.callPacketReceiveEvent(player, vanilla))
					event.setCancelled(true);
			}

			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Packet vanilla = (Packet)packet.getHandle();
				Player player = event.getPlayer();
				
				if(!PacketUtil.callPacketSendEvent(player, vanilla))
					event.setCancelled(true);
			}
		});
	}
}
