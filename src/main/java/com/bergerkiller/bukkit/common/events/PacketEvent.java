package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * Base class for packet-related events
 */
public abstract class PacketEvent implements Cancellable {
	private boolean cancelled = false;	
	private Player player;
	private CommonPacket packet;

	public PacketEvent(Player player, CommonPacket packet) {
		this.player = player;
		this.packet = packet;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}

	/**
	 * Gets the type of packet that is being received/sent
	 * 
	 * @return the Packet type
	 */
	public PacketType getType() {
		return this.packet.getType();
	}

	/**
	 * Get the player who is receiving/sending the packets from the server
	 * 
	 * @return the Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the packet that is about to be received/sent
	 * 
	 * @return the Packet
	 */
	public CommonPacket getPacket() {
		return packet;
	}
}
