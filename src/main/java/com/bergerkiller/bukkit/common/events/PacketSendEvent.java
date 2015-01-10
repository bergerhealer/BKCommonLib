package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;

public class PacketSendEvent extends PacketEvent {

	public PacketSendEvent(Player player, CommonPacket packet) {
		super(player, packet);
	}
}
