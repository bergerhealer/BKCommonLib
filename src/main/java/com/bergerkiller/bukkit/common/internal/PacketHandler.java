package com.bergerkiller.bukkit.common.internal;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;

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
	
	public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types);

	public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types);

	public void sendPacket(Player player, Object packet, boolean throughListeners);

	public void receivePacket(Player player, Object packet);

	public Collection<Plugin> getListening(PacketType packetType);

	public void transfer(PacketHandler to);

	/**
	 * Gets the name of this type of Packet Handler
	 * 
	 * @return packet handler name
	 */
	public String getName();

	/**
	 * Called when this Packet Handler has to be enabled.
	 * This method should take care of registering packet hooks or listeners.
	 * 
	 * @return True if the handler successfully enabled, False if not
	 */
	public boolean onEnable();

	/**
	 * Called when this Packet Handler has to be disabled.
	 * This method should take care of removing any packet hooks or listeners.
	 * 
	 * @return True if the handler successfully disabled, False if not
	 */
	public boolean onDisable();

	/**
	 * Called when a new player joins the server and potentially needs a listener hook
	 * 
	 * @param player that joined
	 */
	public void onPlayerJoin(Player player);

	/**
	 * Gets the amount of bytes of packet data still pending to be sent to the player
	 * 
	 * @param player to get the size for
	 * @return pending packet queue byte size
	 */
	public long getPendingBytes(Player player);
}
