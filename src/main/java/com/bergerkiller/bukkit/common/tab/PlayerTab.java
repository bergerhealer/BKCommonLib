package com.bergerkiller.bukkit.common.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

public class PlayerTab {
	private String[][] slots = new String[3][20];
	private Player player;
	
	protected PlayerTab(Player player) {
		this.player = player;
		for(Player p : Bukkit.getOnlinePlayers())
			this.sendSlot(p.getName(), false, 0);
	}
	
	protected void restore() {
		this.clearTab();
		for(Player p : Bukkit.getOnlinePlayers())
			this.sendSlot(p.getName(), true, PlayerUtil.getPing(p));
	}
	
	private void resend(int row) {
		//We resend all rows after this one to succesfully change
		//Clear them first
		for(int y = 0; y < 3; y++) {
			for(int x = row; x < 20; x++) {
				String slot = slots[y][x];
				if(slot != null) {
					slot = slot.split("%%%")[0];
					this.sendSlot(slot, false, 0);
				}
			}
		}
		
		//Now send the updated slots back
		for(int y = 0; y < 3; y++) {
			for(int x = row; x < 20; x++) {
				String slot = slots[y][x];
				if(slot != null) {
					String[] data= slot.split("%%%");
					String name = data[0];
					int ping = Integer.valueOf(data[1]);
					this.sendSlot(name, true, ping);
				}
			}
		}
	}
	
	/**
	 * Clear the current tab
	 */
	public void clearTab() {
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 20; x++) {
				String slot = slots[y][x];
				if(slot != null) {
					slot = slot.split("%%%")[0];
					this.sendSlot(slot, false, 0);
					slots[y][x] = null;
				}
			}
		}
	}
	
	/**
	 * Get a slot name from the tab
	 * 
	 * @param x Slot x
	 * @param y Slot y
	 * @return Slot name (null if not found)
	 */
	public String getSlotName(int x, int y) {
		String slot = slots[y][x];
		if(slot != null)
			return slot.split("%%%")[0];
		else
			return null;
	}
	
	/**
	 * Get a slot ping from the tab
	 * 
	 * @param x SLot x
	 * @param y Slot y
	 * @return Slot ping (-1 if not found)
	 */
	public int getSlotPing(int x, int y) {
		String slot = slots[y][x];
		if(slot != null)
			return Integer.parseInt(slot.split("%%%")[1]);
		else
			return -1;
	}
	
	/**
	 * Add/remove player from tab with the correct ping
	 * 
	 * @param x Slot x
	 * @param y Slot y
	 * @param player Player to add/remove
	 * @param register Add/remove player
	 */
	public void changeSlot(int x, int y, Player player, boolean register) {
		this.changeSlot(x, y, player.getName(), register, PlayerUtil.getPing(player));
	}
	
	/**
	 * Change tab slot for a player
	 * 
	 * @param x Slot x
	 * @param y Slot y
	 * @param name Slot name
	 * @param register Add/remove slot
	 */
	public void changeSlot(int x, int y, String name, boolean register) {
		this.changeSlot(x, y, name, register, 0);
	}
	
	/**
	 * Change a tab slot for a player
	 * 
	 * @param x Slot x
	 * @param y Slot y
	 * @param name Slot name
	 * @param register Add/remove slot
	 * @param ping Ping: 0 is full
	 */
	public void changeSlot(int x, int y, String name, boolean register, int ping) {
		if(register) {
			slots[y][x] = name + "%%%" + ping;
			this.resend(x);
		} else {
			slots[y][x] = null;
			this.sendSlot(name, false, ping);
		}
	}
	
	private void sendSlot(String name, boolean register, int ping) {
		CommonPacket packet = new CommonPacket(PacketType.PLAYER_INFO);
		packet.write(PacketFields.PLAYER_INFO.playerName, name);
		packet.write(PacketFields.PLAYER_INFO.online, register);
		packet.write(PacketFields.PLAYER_INFO.ping, ping);
		PacketUtil.sendPacket(player, packet, false);
	}
}