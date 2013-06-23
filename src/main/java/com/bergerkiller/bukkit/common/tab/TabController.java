package com.bergerkiller.bukkit.common.tab;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;

public class TabController implements PacketListener, Listener {
	private static final Map<String, PlayerTab> tabs = new HashMap<String, PlayerTab>();

	/**
	 * Check if a player has a custom tab
	 * 
	 * @param player Player to ceck for
	 * @return Player has a custom tab?
	 */
	public static boolean hasCustomTab(Player player) {
		return tabs.containsKey(player.getName());
	}
	
	/**
	 * Get the custom tab from a player
	 * 
	 * @param player Player to egt tab for
	 * @return Tab for player (null if not exists)
	 */
	public static PlayerTab getCustomTab(Player player) {
		return tabs.get(player.getName());
	}
	
	/**
	 * Create a new tab for a player
	 * 
	 * @param player Player to create tab for
	 * @return Old tab if already exist, if not a new one.
	 */
	public static PlayerTab createCustomTab(Player player) {
		PlayerTab tab = getCustomTab(player);
		if(tab != null)
			return tab;
		
		tab = new PlayerTab(player);
		tabs.put(player.getName(), tab);
		return tab;
	}
	
	/**
	 * Remove custom tab and restore it to the default one
	 * 
	 * @param player Player to remove custom tab for
	 * @return removed tab?
	 */
	public boolean removeCustomTab(Player player) {
		PlayerTab tab = tabs.remove(player.getName());
		if(tab != null) {
			tab.restore();
			return true;
		} else
			return false;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		removeCustomTab(player);
	}
	
	@Override
	public void onPacketReceive(PacketReceiveEvent event) {
	}

	@Override
	public void onPacketSend(PacketSendEvent event) {
		Player player = event.getPlayer();
		CommonPacket packet = event.getPacket();
		if(packet.getType() == PacketType.PLAYER_INFO) {
			if(hasCustomTab(player)) {
				event.setCancelled(true);
			}
		}
	}
}