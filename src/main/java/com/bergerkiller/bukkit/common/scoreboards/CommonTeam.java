package com.bergerkiller.bukkit.common.scoreboards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

public class CommonTeam implements Serializable {
	private static final long serialVersionUID = -2928363541719230386L;
	private String name;
	private String displayName;
	private String prefix;
	private String suffix;
	private FriendlyFireType friendlyFire;
	private List<String> players;
	private boolean sendToAll;

	//Do NOT construct teams from outside
	//Use CommonScoreboard.newTeam(String name)
	protected CommonTeam(String name) {
		this.name = name;
		this.displayName = name;
		this.prefix = "";
		this.suffix = "";
		this.friendlyFire = FriendlyFireType.ON;
		this.players = new ArrayList<String>();
	}

	/**
	 * Gets the name of team
	 * 
	 * @return Name of team
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the display name of the team
	 * 
	 * @return Display name of team
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Gets the prefix of the team
	 * 
	 * @return Prefix of teeam
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Gets the suffix of the team
	 * 
	 * @return Suffix of team
	 */
	public String getSuffix() {
		return this.suffix;
	}

	/**
	 * Gets the friendly fire type for the team
	 * - ON, pvp enabled
	 * - OFF, pvp disabled
	 * - INVICIBLE, everyone looks like ghosts ;)
	 * 
	 * @return Friendly fire type of the team
	 */
	public FriendlyFireType getFriendlyFireType() {
		return this.friendlyFire;
	}

	/**
	 * Gets all the players in the team
	 * 
	 * @return Players in team
	 */
	public List<String> getPlayers() {
		return this.players;
	}

	/**
	 * Wether or not the team stats should be sent to all players
	 * 
	 * @return Sent to everyone?
	 */
	public boolean shouldSendToAll() {
		return this.sendToAll;
	}

	/**
	 * Sets the display name for the team
	 * 
	 * @param displayName of the team
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		this.update();
	}

	/**
	 * Sets the prefix for the team
	 * 
	 * @param prefix of the team
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		this.update();
	}

	/**
	 * Sets the suffix for the team
	 * 
	 * @param suffix of the team
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		this.update();
	}

	/**
	 * Chanegs the freindly fire type for the team
	 * - ON, pvp enabled
	 * - OFF, pvp disabled
	 * - INVICIBLE, everyone looks like ghosts ;)
	 * 
	 * @param friendlyFire of the team
	 */
	public void setFriendlyFire(FriendlyFireType friendlyFire) {
		this.friendlyFire = friendlyFire;
		this.update();
	}

	/**
	 * Set if we shall sedn the data to all players
	 * 
	 * @param sendToAll Send to all?
	 */
	public void setSendToAll(boolean sendToAll) {
		this.sendToAll = sendToAll;
	}

	/**
	 * Add a player to the team
	 * 
	 * @param player to add
	 */
	public void addPlayer(OfflinePlayer player) {
		players.add(player.getName());

		if(this.sendToAll) {
			for(Player p : Bukkit.getServer().getOnlinePlayers())
				PacketUtil.sendPacket(p, this.getPacket(3));
		} else {
			if(player != null && player.isOnline()) {
				PacketUtil.sendPacket((Player) player, this.getPacket(0));
			}

			for(String user : players) {
				Player p = Bukkit.getPlayer(user);
				if(p != null && p.isOnline()) {
					PacketUtil.sendPacket(p, this.getPacket(3));
				}
			}
		}
	}

	/**
	 * Remove a player from the team
	 * 
	 * @param player to remove
	 */
	public void removePlayer(OfflinePlayer player) {
		players.remove(player.getName());

		if(this.sendToAll) {
			for(Player p : Bukkit.getServer().getOnlinePlayers())
				PacketUtil.sendPacket(p, this.getPacket(4));
		} else {
			if(player != null && player.isOnline()) {
				PacketUtil.sendPacket((Player) player, this.getPacket(1));
			}

			for(String user : players) {
				Player p = Bukkit.getPlayer(user);
				if(p != null && p.isOnline()) {
					PacketUtil.sendPacket(p, this.getPacket(4));
				}
			}
		}
	}

	/**
	 * Dispaly (create) the team
	 */
	public void show() {
		if(this.sendToAll) {
			for(Player p : Bukkit.getServer().getOnlinePlayers())
				PacketUtil.sendPacket(p, this.getPacket(0));
		} else {
			for(String user : players) {
				Player p = Bukkit.getPlayer(user);
				if(p != null && p.isOnline()) {
					PacketUtil.sendPacket(p, this.getPacket(0));
				}
			}
		}
	}

	/**
	 * Hide (remove) the team
	 */
	public void hide() {
		if(this.sendToAll) {
			for(Player p : Bukkit.getServer().getOnlinePlayers())
				PacketUtil.sendPacket(p, this.getPacket(1));
		} else {
			for(String user : players) {
				Player p = Bukkit.getPlayer(user);
				if(p != null && p.isOnline()) {
					PacketUtil.sendPacket(p, this.getPacket(1));
				}
			}
		}
	}

	/**
	 * Send chanegs to the team
	 */
	private void update() {
		if(this.sendToAll) {
			for(Player p : Bukkit.getServer().getOnlinePlayers())
				PacketUtil.sendPacket(p, this.getPacket(2));
		} else {
			for(String user : players) {
				Player p = Bukkit.getPlayer(user);
				if(p != null && p.isOnline()) {
					PacketUtil.sendPacket(p, this.getPacket(2));
				}
			}
		}
	}

	/**
	 * Send all team data to a speciafic player
	 * 
	 * @param player to receive team data
	 */
	public void send(Player player) {
		PacketUtil.sendPacket(player, this.getPacket(0));
	}

	/**
	 * Get the team packet with a cusotm action
	 * 
	 * @param action of the packet
	 * @return Packet
	 */
	private CommonPacket getPacket(int action) {
		CommonPacket packet = new CommonPacket(PacketType.OUT_SCOREBOARD_TEAM);
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.team, this.name);
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.display, this.displayName);
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.prefix, this.prefix);
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.suffix, this.suffix);
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.players, this.players);
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.friendlyFire, this.friendlyFire.getRawInt());
		packet.write(PacketType.OUT_SCOREBOARD_TEAM.mode, action);
		return packet;
	}

	public static enum FriendlyFireType {
		/**
		 * PVP disabled
		 */
		OFF(),
		/**
		 * PVP enabled
		 */
		ON(),
		/**
		 * Players are invisible
		 */
		INVICIBLE();

		public int getRawInt() {
			return this.ordinal();
		}
	}
}