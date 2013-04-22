package com.bergerkiller.bukkit.common.scoreboards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

public class CommonTeam {
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
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getSuffix() {
		return this.suffix;
	}
	
	public FriendlyFireType getFriendlyFireType() {
		return this.friendlyFire;
	}
	
	public List<String> getPlayers() {
		return this.players;
	}
	
	public boolean shouldSendToAll() {
		return this.sendToAll;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		this.update();
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
		this.update();
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		this.update();
	}
	
	public void setFriendlyFire(FriendlyFireType friendlyFire) {
		this.friendlyFire = friendlyFire;
		this.update();
	}
	
	public void setSendToAll(boolean sendToAll) {
		this.sendToAll = sendToAll;
	}
	
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
	
	public void send(Player player) {
		PacketUtil.sendPacket(player, this.getPacket(0));
	}
	
	private CommonPacket getPacket(int action) {
		CommonPacket packet = new CommonPacket(PacketType.SET_SCOREBOARD_TEAM);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.team, this.name);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.display, this.displayName);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.prefix, this.prefix);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.suffix, this.suffix);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.players, this.players);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.friendlyFire, this.friendlyFire.rawInt);
		packet.write(PacketFields.SET_SCOREBOARD_TEAM.mode, action);
		return packet;
	}
	
	public static enum FriendlyFireType {
		OFF(0),
		ON(1),
		INVICIBLE(2);
		
		private int rawInt;
		
		FriendlyFireType(int rawInt) {
			this.rawInt = rawInt;
		}
		
		public int getRawInt() {
			return this.rawInt;
		}
	}
}