package com.bergerkiller.bukkit.common.protocol;

import org.bukkit.entity.Player;

/**
 * Allows a plugin to monitor the packets being received and sent, but not alter the outcome
 */
public interface PacketMonitor {
	/**
	 * Called when a packet is going to be sent to a player.
	 * This method does not allow the packet to be altered - use a {@link PacketListener} for that!
	 * 
	 * @param packet to send
	 * @param player to send to
	 */
	public void onMonitorPacketSend(CommonPacket packet, Player player);

	/**
	 * Called when a packet is about to be received by the server from a player.
	 * This method does not allow the packet to be altered - use a {@link PacketListener} for that!
	 * 
	 * @param packet to receive
	 * @param player from which the packet came
	 */
	public void onMonitorPacketReceive(CommonPacket packet, Player player);
}
