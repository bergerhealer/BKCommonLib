package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;

public class PacketReceiveEvent implements Cancellable {
	private boolean cancelled = false;
	private Player player;
	private CommonPacket packet;

	public PacketReceiveEvent(Player player, CommonPacket packet) {
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
	 * Get the player who sent the packet
	 * 
	 * @return the Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the packet that is about to be handled
	 * 
	 * @return the Packet
	 */
	public CommonPacket getPacket() {
		return packet;
	}
}
