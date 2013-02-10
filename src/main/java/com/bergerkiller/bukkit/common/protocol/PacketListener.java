package com.bergerkiller.bukkit.common.protocol;

import java.util.List;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket.Packets;

public abstract class PacketListener {
	
	/**
	 * Gets fired when a packet is received
	 * 
	 * @param event			Packet
	 */
	public void onPacketReceive(PacketReceiveEvent event) {}
	
	/**
	 * 
	 * @param event
	 */
	public void onPacketSend(PacketSendEvent event) {}
	
	/**
	 * All packets that should get notified
	 * 
	 * @return				Packets
	 */
	public abstract List<Packets> getSupportedPackets();
}
