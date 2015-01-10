package com.bergerkiller.bukkit.common.scoreboards;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.scoreboards.CommonScoreboard.Display;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

public class CommonObjective {
	private Display display;
	private CommonScoreboard scoreboard;
	private String name;
	private String displayName;
	private boolean displayed;
	private Map<String, CommonScore> scores = new HashMap<String, CommonScore>();

	protected CommonObjective(CommonScoreboard scoreboard, Display display) {
		this.display = display;
		this.scoreboard = scoreboard;
		this.name = display.getName();
		this.displayName = display.getDisplayName();
	}

	/**
	 * Get the objective unique id
	 * 
	 * @return Unique id
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the objective display name
	 * 
	 * @return Display name
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Get the display from the objective
	 * 
	 * @return Display
	 */
	public Display getDisplay() {
		return this.display;
	}

	/**
	 * Change the display name from the objective
	 * 
	 * @param value
	 */
	public void setDisplayName(String value) {
		this.displayName = value;
		this.update();
	}

	/**
	 * Get the scores
	 * 
	 * @return Scores
	 */
	public Collection<CommonScore> getScores() {
		return scores.values();
	}

	/**
	 * Get a score by name
	 * 
	 * @param name Score name
	 * @return Score
	 */
	public CommonScore getScore(String name) {
		return scores.get(name);
	}

	/**
	 * Add a score to the list
	 * 
	 * @param name of the Score
	 * @param score Score
	 */
	public void addScore(String name, CommonScore score) {
		CommonScore old = this.getScore(name);
		if (old != null) {
			old.remove();
		}
		score.create();
		scores.put(name, score);
	}

	/**
	 * Remove a score form the list
	 * 
	 * @param name of the Score
	 */
	public void removeScore(String name) {
		CommonScore score = this.getScore(name);
		if (score!= null) {
			score.remove();
		}
		scores.remove(name);
	}

	/**
	 * Create a score and add it to the list
	 * 
	 * @param name for the Score
	 * @param displayName (initial) of the Score to create
	 * @param value (initial) of the Score to create
	 * @return Score
	 */
	public CommonScore createScore(String name, String displayName, int value) {
		CommonScore score = new CommonScore(this.scoreboard, displayName, this.name);
		score.setValue(value);
		this.addScore(name, score);
		return score;
	}

	/**
	 * Clear all scores
	 */
	public void clearScores() {
		Iterator<String> it = scores.keySet().iterator();
		while(it.hasNext()) {
			CommonScore score = this.getScore(it.next());
			score.remove();
			it.remove();
		}
	}

	/**
	 * Update the display text
	 */
	public void update() {
		if(!this.displayed)
			return;

		this.handle(2);
	}

	/**
	 * Show this objective
	 */
	public void show() {
		if (!this.displayed) {
			this.handle(0);
		}
		this.display();
		this.displayed = true;
	}

	/**
	 * Hide this objective
	 */
	public void hide() {
		if (!this.displayed) {
			return;
		}
		this.handle(1);
		this.displayed = false;
	}

	private void handle(int type) {
		CommonPacket packet = new CommonPacket(PacketType.OUT_SCOREBOARD_OBJECTIVE);
		packet.write(PacketType.OUT_SCOREBOARD_OBJECTIVE.name, this.name);
		packet.write(PacketType.OUT_SCOREBOARD_OBJECTIVE.displayName, this.displayName);
		packet.write(PacketType.OUT_SCOREBOARD_OBJECTIVE.action, type);
		PacketUtil.sendPacket(this.scoreboard.getPlayer(), packet);
	}

	private void display() {
		CommonPacket packet = new CommonPacket(PacketType.OUT_SCOREBOARD_DISPLAY_OBJECTIVE);
		packet.write(PacketType.OUT_SCOREBOARD_DISPLAY_OBJECTIVE.name, this.name);
		packet.write(PacketType.OUT_SCOREBOARD_DISPLAY_OBJECTIVE.display, this.display.getId());
		PacketUtil.sendPacket(this.scoreboard.getPlayer(), packet);
	}

	protected static CommonObjective copyFrom(CommonScoreboard board, CommonObjective objective) {
		CommonObjective obj = new CommonObjective(board, objective.display);

		//Copy all scores
		for(Map.Entry<String, CommonScore> entry : objective.scores.entrySet()) {
			String id = entry.getKey();
			CommonScore score = entry.getValue();
			CommonScore newScore = CommonScore.copyFrom(board, score);
			obj.addScore(id, newScore);
		}

		return obj;
	}
}