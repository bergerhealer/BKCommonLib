package com.bergerkiller.bukkit.common.scoreboards;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

public class CommonScore {
	private CommonScoreboard scoreboard;
	private String name;
	private String objName;
	private int value;
	private boolean created;
	
	protected CommonScore(CommonScoreboard scoreboard, String name, String objName) {
		this.scoreboard = scoreboard;
		this.name = name;
		this.objName = objName;
	}
	
	/**
	 * Get the unique id from the score
	 * 
	 * @return Unique id
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the score value
	 * 
	 * @return Score value
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * Set the score value
	 * 
	 * @param value Score value
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Update the score
	 */
	public void update() {
		if(!this.created)
			return;
		
		CommonPacket packet = new CommonPacket(PacketType.SET_SCOREBOARD_SCORE);
		packet.write(PacketFields.SET_SCOREBOARD_SCORE.name, this.name);
		packet.write(PacketFields.SET_SCOREBOARD_SCORE.objName, this.objName);
		packet.write(PacketFields.SET_SCOREBOARD_SCORE.value, this.value);
		packet.write(PacketFields.SET_SCOREBOARD_SCORE.action, 0);
		PacketUtil.sendPacket(scoreboard.getPlayer(), packet);
	}
	
	/**
	 * Create the score
	 */
	protected void create() {
		if(this.created)
			return;
		
		this.created = true;
		this.update();
	}
	
	/**
	 * Remove the score
	 */
	protected void remove() {
		if(!this.created)
			return;
		
		CommonPacket packet = new CommonPacket(PacketType.SET_SCOREBOARD_SCORE);
		packet.write(PacketFields.SET_SCOREBOARD_SCORE.name, this.name);
		packet.write(PacketFields.SET_SCOREBOARD_SCORE.action, 1);
		PacketUtil.sendPacket(scoreboard.getPlayer(), packet);
		this.created = false;
	}
	
	protected static CommonScore copyFrom(CommonScoreboard board, CommonScore from) {
		CommonScore to = new CommonScore(board, from.name, from.objName);
		to.setValue(from.getValue());
		return to;
	}
}