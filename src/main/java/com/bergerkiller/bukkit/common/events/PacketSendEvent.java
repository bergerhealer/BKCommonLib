package com.bergerkiller.bukkit.common.events;

import net.minecraft.server.v1_4_R1.EntityPlayer;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class PacketSendEvent {
	private boolean cancelled = false;
	
	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
	
	private Player player;
	private CommonPacket packet;
	
	public PacketSendEvent(EntityPlayer player, CommonPacket packet) {
		this.player = NativeUtil.getPlayer(player);
		this.packet = packet;
	}
	
	public PacketSendEvent(Player player, CommonPacket packet) {
		this.player = player;
		this.packet = packet;
	}
	
	/**
	 * Get the plaer who is receiving the packets from the server
	 * 
	 * @return			Player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the packet that is about to be sended
	 * 
	 * @return			Packet
	 */
	public CommonPacket getPacket() {
		return packet;
	}
}
