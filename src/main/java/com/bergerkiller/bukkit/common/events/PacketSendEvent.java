package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;

public class PacketSendEvent implements Cancellable {
	private boolean cancelled = false;	
	private Player player;
	private CommonPacket packet;

	public PacketSendEvent(Player player, CommonPacket packet) {
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
	 * Get the player who is receiving the packets from the server
	 * 
	 * @return the Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the packet that is about to be sent
	 * 
	 * @return the Packet
	 */
	public CommonPacket getPacket() {
		return packet;
	}
}
