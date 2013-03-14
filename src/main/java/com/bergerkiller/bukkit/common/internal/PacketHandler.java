package com.bergerkiller.bukkit.common.internal;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;

/**
 * All the methods needed for internally handling the packet sending and receiving
 */
public interface PacketHandler {

	/**
	 * Removes all monitors and listeners belonging to a plugin
	 * 
	 * @param plugin to remove for
	 */
	public void removePacketListeners(Plugin plugin);

	public void removePacketListener(PacketListener listener);

	public void removePacketMonitor(PacketMonitor monitor);
	
	public void addPacketListener(Plugin plugin, PacketListener listener, int[] ids);

	public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, int[] ids);

	public void sendPacket(Player player, Object packet, boolean throughListeners);

	public Collection<Plugin> getListening(int id);

	public void transfer(PacketHandler to);
}
