package com.bergerkiller.bukkit.common.events;

import net.minecraft.server.v1_4_R1.EntityPlayer;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class PacketReceiveEvent {
	private boolean cancelled = false;
	
	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
	
	private Player player;
	private CommonPacket packet;
	
	public PacketReceiveEvent(EntityPlayer player, CommonPacket packet) {
		this.player = NativeUtil.getPlayer(player);
		this.packet = packet;
	}
	
	public PacketReceiveEvent(Player player, CommonPacket packet) {
		this.player = player;
		this.packet = packet;
	}
	
	/**
	 * Get the player who has sended the packet
	 * 
	 * @return			Player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the packet that is about to be handled
	 * 
	 * @return			Packet
	 */
	public CommonPacket getPacket() {
		return packet;
	}
}
