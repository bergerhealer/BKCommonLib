package com.bergerkiller.bukkit.common.scoreboards;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommonScoreboard {
	private static Map<Player, CommonScoreboard> boards = new WeakHashMap<Player, CommonScoreboard>();
	public static CommonTeam dummyTeam = new CommonTeam("dummy") {
		public void addPlayer(OfflinePlayer player) {}
		public void removePlayer(OfflinePlayer player) {}
		public void show() {}
		public void hide() {}
		public void setDisplayName(String displayName) {}
		public void setPrefix(String prefix) {}
		public void setSuffix(String suffic) {}
		public void setFriendlyFire(FriendlyFireType friendlyFire) {}
		public void send(Player player) {}
	};
	
	public static CommonScoreboard get(Player player) {
		if(boards.containsKey(player))
			return boards.get(player);
		else {
			CommonScoreboard board = new CommonScoreboard(player);
			boards.put(player, board);
			return board;
		}
	}
	
	private CommonTeam team;
	private CommonObjective[] objectives = new CommonObjective[3];
	private Player player;
	
	public CommonScoreboard(Player player) {
		this.team = dummyTeam;
		this.player = player;
		for(int i = 0; i < 3; i++) {
			Display display = Display.fromInt(i);
			objectives[i] = new CommonObjective(this, display);
		}
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
	 * Get the scoreboard form a certain display
	 * 
	 * @param display Display
	 * @return Objective
	 */
	public CommonObjective getObjective(Display display) {
		return this.objectives[display.getId()];
	}
	
	/**
	 * Get the scoreboard team from the player
	 * 
	 * @return Scoreboard team
	 */
	public CommonTeam getTeam() {
		return this.team;
	}
	
	/**
	 * Set the team for the player
	 * 
	 * @param team New team
	 */
	public void setTeam(CommonTeam team) {
		if(team == null)
			throw new IllegalArgumentException("Team cannot be null!");
		
		this.team = team;
	}
	
	/**
	 * Create a new scoreboard team
	 * 
	 * @param name Team name
	 * @return New team
	 */
	public static CommonTeam newTeam(String name) {
		return new CommonTeam(name);
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
		for(int i = 0; i < 3; i++) {
			Display display = Display.fromInt(i);
			board.objectives[i] = CommonObjective.copyFrom(board, from.getObjective(display));
		}
		
		return board;
	}
	
	public static enum Display {
		LIST(0, "list", "List"),
		SIDEBAR(1, "sidebar", "SideBar"),
		BELOWNAME(2, "belowname", "BelowName");
		
		private int id;
		private String name;
		private String displayName;
		
		Display(int id, String name, String displayName) {
			this.id = id;
			this.name = name;
			this.displayName = displayName;
		}
		
		public int getId() {
			return this.id;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getDisplayName() {
			return this.displayName;
		}
		
		public static Display fromInt(int from) {
			for(Display display : values()) {
				if(display.id == from)
					return display;
			}
			
			return null;
		}
	}
}