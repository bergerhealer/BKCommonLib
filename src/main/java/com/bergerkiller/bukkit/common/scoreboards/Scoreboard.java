package com.bergerkiller.bukkit.common.scoreboards;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

public class Scoreboard {
	private String name;
	private String displayName;
	
	public Scoreboard(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public void create(Player player) {
		CommonPacket packet = new CommonPacket(PacketType.SET_SCOREBOARD_OBJECTIVE);
		packet.write(PacketFields.SET_SCOREBOARD_OBJECTIVE.name, this.name);
		packet.write(PacketFields.SET_SCOREBOARD_OBJECTIVE.displayName, this.displayName);
		packet.write(PacketFields.SET_SCOREBOARD_OBJECTIVE.action, 0);
		PacketUtil.sendPacket(player, packet);
	}
	
	public void remove(Player player) {
		CommonPacket packet = new CommonPacket(PacketType.SET_SCOREBOARD_OBJECTIVE);
		packet.write(PacketFields.SET_SCOREBOARD_OBJECTIVE.name, this.name);
		packet.write(PacketFields.SET_SCOREBOARD_OBJECTIVE.displayName, this.displayName);
		packet.write(PacketFields.SET_SCOREBOARD_OBJECTIVE.action, 1);
		PacketUtil.sendPacket(player, packet);
	}
	
	public void setVisible(Player player, boolean visible) {
		//TODO: Add stuff
	}
}