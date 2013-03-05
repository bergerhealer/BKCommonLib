package com.bergerkiller.bukkit.common.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

class CommonProtocolLibHandler {
	private static final int MAX_LISTENER_COUNT = 3;
	private static final List<CommonPacketListener> listeners = new ArrayList<CommonPacketListener>(MAX_LISTENER_COUNT);
	private static final List<Integer> watchedPacketTypes = new ArrayList<Integer>(10);

	/**
	 * Registers the packet listener(s) to handle the packet types specified
	 * 
	 * @param packets ids to handle
	 */
	public static void register(int[] packets) {
		// Update the types to register and the new types registered
		Set<Integer> newTypes = new HashSet<Integer>(packets.length);
		for (int packet : packets) {
			newTypes.add(Integer.valueOf(packet));
		}
		register(newTypes);
	}

	/**
	 * Registers the packet listener(s) to handle the packet types specified
	 * 
	 * @param packets ids to handle
	 */
	public static void register(Set<Integer> packets) {
		// Update the types to register and the new types registered
		Collection<Integer> newTypes = packets;
		newTypes.removeAll(watchedPacketTypes);
		if (newTypes.isEmpty()) {
			// No new types to register
			return;
		}
		watchedPacketTypes.addAll(newTypes);

		if (listeners.size() >= MAX_LISTENER_COUNT) {
			// Unregister all previous listeners and combine the Ids for one big listener
			// This avoids having too many listeners spread all over the place
			for (CommonPacketListener listener : listeners) {
				ProtocolLibrary.getProtocolManager().removePacketListener(listener);
			}
			listeners.clear();
			// Register all packets
			newTypes = watchedPacketTypes;
		}
		// Initialize a new packet listener for the packet types
		CommonPacketListener listener = new CommonPacketListener(newTypes);
		// Add it to the listener list
		listeners.add(listener);
		// Register it in ProtocolLib
		ProtocolLibrary.getProtocolManager().addPacketListener(listener);
	}

	public static Collection<Plugin> getListening(int packetId) {
		Integer id = Integer.valueOf(packetId);
		Set<Plugin> plugins = new HashSet<Plugin>();
		for (PacketListener listener : ProtocolLibrary.getProtocolManager().getPacketListeners()) {
			if (listener.getSendingWhitelist().getWhitelist().contains(id)) {
				plugins.add(listener.getPlugin());
			}
		}
		return plugins;
	}

	public static void sendPacket(Player player, Object packet, boolean throughListeners) {
		ProtocolManager pm = ProtocolLibrary.getProtocolManager();
		PacketContainer toSend = new PacketContainer(PacketFields.DEFAULT.packetID.get(packet), packet);
		try {
			pm.sendServerPacket(player, toSend, throughListeners);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Invalid packet target");
		}
	}

	/**
	 * This is a listener class that deals with the sending and receiving of packets.
	 * Supports the handling of multiple packets at once.
	 */
	private static class CommonPacketListener extends PacketAdapter {

		public CommonPacketListener(Collection<Integer> packets) {
			super(CommonPlugin.getInstance(), ConnectionSide.BOTH, packets.toArray(new Integer[0]));
		}

		@Override
		public void onPacketReceiving(PacketEvent event) {
			PacketContainer packet = event.getPacket();
			Player player = event.getPlayer();

			if (!((CommonPlugin) getPlugin()).onPacketReceive(player, packet.getHandle(), event.getPacketID())) {
				event.setCancelled(true);
			}
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			PacketContainer packet = event.getPacket();
			Player player = event.getPlayer();

			if (!((CommonPlugin) getPlugin()).onPacketSend(player, packet.getHandle(), event.getPacketID())) {
				event.setCancelled(true);
			}
		}
	}
}
