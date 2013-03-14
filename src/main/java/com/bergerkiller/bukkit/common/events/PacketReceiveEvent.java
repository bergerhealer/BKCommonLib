package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;

public class PacketReceiveEvent extends PacketEvent {

	public PacketReceiveEvent(Player player, CommonPacket packet) {
		super(player, packet);
	}
}
