package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;

/**
 * Allows a plugin to listen to packet send and receive events, and alter the outcome
 */
public interface PacketListener {

	/**
	 * Gets fired when a packet is received
	 * 
	 * @param event	Packet event
	 */
	public void onPacketReceive(PacketReceiveEvent event);

	/**
	 * Gets fired then a packet is sent
	 * 
	 * @param event	Packet event
	 */
	public void onPacketSend(PacketSendEvent event);
}
