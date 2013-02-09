package com.bergerkiller.bukkit.common.events;

import net.minecraft.server.v1_4_R1.EntityPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class PacketSendEvent extends Event implements Cancellable {
	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
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
