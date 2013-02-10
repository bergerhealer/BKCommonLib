package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;

public interface PacketListener {
	
	/**
	 * Gets fired when a packet is received
	 * 
	 * @param event			Packet event
	 */
	public void onPacketReceive(PacketReceiveEvent event);
	
	/**
	 * Gets fired then a packet is sended
	 * 
	 * @param event			Packet event
	 */
	public void onPacketSend(PacketSendEvent event);
}
