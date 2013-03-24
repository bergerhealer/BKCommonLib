package com.bergerkiller.bukkit.common.scoreboards;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class CommonScoreboard {
	private Map<String, CommonObjective> objectives = new HashMap<String, CommonObjective>();
	private Player player;
	
	public CommonScoreboard(Player player) {
		this.player = player;
	}
	
	/**
	 * Get the player form the scoreboard
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Get all the objectives
	 * 
	 * @return Objectives
	 */
	public Collection<CommonObjective> getObjectives() {
		return objectives.values();
	}
	
	/**
	 * Get an objective
	 * 
	 * @param name Objective name
	 * @return Objective
	 */
	public CommonObjective getObjective(String name) {
		return objectives.get(name);
	}
	
	/**
	 * Add an onjective
	 * 
	 * @param objective Objective
	 */
	public void addObjective(CommonObjective objective) {
		if(getObjective(objective.getName()) != null)
			throw new IllegalArgumentException("Objective '"+ objective.getName()+"' already exists!");
		
		objectives.put(objective.getName(), objective);
	}
	
	/**
	 * Create an objective and add it
	 * 
	 * @param name Objective name
	 * @param displayName Objective display name
	 * @return Objective
	 */
	public CommonObjective createObjective(String name, String displayName) {
		CommonObjective obj = new CommonObjective(this, name, displayName);
		this.addObjective(obj);
		return obj;
	}
	
	/**
	 * Copy the scoreboard
	 * 
	 * @param player New player
	 * @param from Old scoreboard
	 * @return New scoreboard
	 */
	public static CommonScoreboard copyFrom(Player player, CommonScoreboard from) {
		CommonScoreboard board = new CommonScoreboard(player);
		
		//Copy all objectives
		for(CommonObjective obj : from.getObjectives()) {
			CommonObjective newObj = CommonObjective.copyFrom(board, obj);
			board.addObjective(newObj);
		}
		
		return board;
	}
	
	public static enum Display {
		LIST(0),
		SIDEBAR(1),
		BELOWNAME(2);
		
		private int id;
		
		Display(int id) {
			this.id = id;
		}
		
		public int getId() {
			return this.id;
		}
	}
}