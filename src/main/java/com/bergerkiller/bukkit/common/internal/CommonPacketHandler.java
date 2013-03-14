package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

/**
 * Basic packet handler implementation that uses a replaced PlayerConnection for events.
 * Note: Contains a lot of duplicated code, might need some improvements.
 */
public class CommonPacketHandler implements PacketHandler {
	@SuppressWarnings("unchecked")
	private final List<PacketListener>[] listeners = new ArrayList[256];
	@SuppressWarnings("unchecked")
	private final List<PacketMonitor>[] monitors = new ArrayList[256];
	private final Map<Plugin, List<PacketListener>> listenerPlugins = new HashMap<Plugin, List<PacketListener>>();
	private final Map<Plugin, List<PacketMonitor>> monitorPlugins = new HashMap<Plugin, List<PacketMonitor>>();

	@Override
	public void removePacketListeners(Plugin plugin) {
		// Listeners
		List<PacketListener> listeners = listenerPlugins.get(plugin);
		if (listeners != null) {
			for (PacketListener listener : listeners) {
				removePacketListener(listener, false);
			}
		}
		// Monitors
		List<PacketMonitor> monitors = monitorPlugins.get(plugin);
		if (monitors != null) {
			for (PacketMonitor monitor : monitors) {
				removePacketMonitor(monitor, false);
			}
		}
	}

	@Override
	public void removePacketMonitor(PacketMonitor monitor) {
		removePacketMonitor(monitor, true);
	}

	private void removePacketMonitor(PacketMonitor monitor, boolean fromPlugins) {
		if (monitor == null) {
			return;
		}
		for(int i = 0; i < monitors.length; i++) {
			if (!LogicUtil.nullOrEmpty(monitors[i])) {
				monitors[i].remove(monitor);
			}
		}
		if (fromPlugins) {
			// Remove from plugin list
			for (Plugin plugin : monitorPlugins.keySet().toArray(new Plugin[0])) {
				List<PacketMonitor> list = monitorPlugins.get(plugin);
				// If not null, remove the monitor, if empty afterwards remove the entire entry
				if (list != null && list.remove(monitor) && list.isEmpty()) {
					monitorPlugins.remove(plugin);
				}
			}
		}
	}

	@Override
	public void removePacketListener(PacketListener listener) {
		removePacketListener(listener, true);
	}

	private void removePacketListener(PacketListener listener, boolean fromPlugins) {
		if (listener == null) {
			return;
		}
		for(int i = 0; i < listeners.length; i++) {
			if (!LogicUtil.nullOrEmpty(listeners[i])) {
				listeners[i].remove(listener);
			}
		}
		if (fromPlugins) {
			// Remove from plugin list
			for (Plugin plugin : listenerPlugins.keySet().toArray(new Plugin[0])) {
				List<PacketListener> list = listenerPlugins.get(plugin);
				// If not null, remove the listener, if empty afterwards remove the entire entry
				if (list != null && list.remove(listener) && list.isEmpty()) {
					listenerPlugins.remove(plugin);
				}
			}
		}
	}

	@Override
	public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, int[] ids) {
		if (monitor == null) {
			throw new IllegalArgumentException("Monitor is not allowed to be null");
		} else if (plugin == null) {
			throw new IllegalArgumentException("Plugin is not allowed to be null");
		}
		// Register the listener
		for (int id : ids) {
			if (id < 0 || id >= monitors.length) {
				throw new IllegalArgumentException("Unknown packet type Id: " + id);
			}
			// Map to listener array
			if (monitors[id] == null) {
				monitors[id] = new ArrayList<PacketMonitor>();
			}
			monitors[id].add(monitor);
			// Map to plugin list
			List<PacketMonitor> list = monitorPlugins.get(plugin);
			if (list == null) {
				list = new ArrayList<PacketMonitor>(2);
				monitorPlugins.put(plugin, list);
			}
			list.add(monitor);
		}
	}

	@Override
	public void addPacketListener(Plugin plugin, PacketListener listener, int[] ids) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener is not allowed to be null");
		} else if (plugin == null) {
			throw new IllegalArgumentException("Plugin is not allowed to be null");
		}
		// Register the listener
		for (int id : ids) {
			if (id < 0 || id >= listeners.length) {
				throw new IllegalArgumentException("Unknown packet type Id: " + id);
			}
			// Map to listener array
			if (listeners[id] == null) {
				listeners[id] = new ArrayList<PacketListener>();
			}
			listeners[id].add(listener);
			// Map to plugin list
			List<PacketListener> list = listenerPlugins.get(plugin);
			if (list == null) {
				list = new ArrayList<PacketListener>(2);
				listenerPlugins.put(plugin, list);
			}
			list.add(listener);
		}
	}

	@Override
	public void sendPacket(Player player, Object packet, boolean throughListeners) {
		if (!PacketFields.DEFAULT.isInstance(packet) || PlayerUtil.isDisconnected(player)) {
			return;
		}
		if (!throughListeners) {
			packet = new CommonSilentPacket(packet);
		}
		final Object connection = EntityPlayerRef.playerConnection.get(Conversion.toEntityHandle.convert(player));
		PlayerConnectionRef.sendPacket(connection, packet);
	}

	@Override
	public Collection<Plugin> getListening(int id) {
		if (!LogicUtil.isInBounds(listeners, id)) {
			return Collections.emptySet();
		}
		List<PacketListener> list = listeners[id];
		if (LogicUtil.nullOrEmpty(list)) {
			return Collections.emptySet();
		}
		List<Plugin> plugins = new ArrayList<Plugin>();
		for (Entry<Plugin, List<PacketListener>> entry : listenerPlugins.entrySet()) {
			for (PacketListener listener : list) {
				if (entry.getValue().contains(listener)) {
					plugins.add(entry.getKey());
					break;
				}
			}
		}
		return plugins;
	}

	@Override
	public void transfer(PacketHandler to) {
		for (Entry<Plugin, List<PacketListener>> entry : listenerPlugins.entrySet()) {
			for (PacketListener listener : entry.getValue()) {
				to.addPacketListener(entry.getKey(), listener, getListenerIds(listener));
			}
		}
		for (Entry<Plugin, List<PacketMonitor>> entry : monitorPlugins.entrySet()) {
			for (PacketMonitor listener : entry.getValue()) {
				to.addPacketMonitor(entry.getKey(), listener, getMonitorIds(listener));
			}
		}
	}

	private int[] getListenerIds(PacketListener listener) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] != null && listeners[i].contains(listener)) {
				list.add(i);
			}
		}
		return Conversion.toIntArr.convert(list);
	}

	private int[] getMonitorIds(PacketMonitor listener) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < monitors.length; i++) {
			if (monitors[i] != null && monitors[i].contains(listener)) {
				list.add(i);
			}
		}
		return Conversion.toIntArr.convert(list);
	}

	public boolean onPacketSend(Player player, Object packet, boolean wasCancelled) {
		if(player == null || packet == null) {
			return true;
		}
		final int id;
		if (packet instanceof CommonSilentPacket) {
			// Do not send to listeners, but unwrap for monitors
			packet = ((CommonSilentPacket) packet).packet;
			id = PacketFields.DEFAULT.packetID.get(packet);
		} else {
			// Handle listeners
			id = PacketFields.DEFAULT.packetID.get(packet);
			if (!LogicUtil.nullOrEmpty(listeners[id])) {
				CommonPacket cp = new CommonPacket(packet, id);
				PacketSendEvent ev = new PacketSendEvent(player, cp);
				ev.setCancelled(wasCancelled);
				for (PacketListener listener : listeners[id]) {
					listener.onPacketSend(ev);
				}
				if (ev.isCancelled()) {
					return false;
				}
			}
		}
		// Handle monitors
		if (!LogicUtil.nullOrEmpty(monitors[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			for (PacketMonitor monitor : monitors[id]) {
				monitor.onMonitorPacketSend(cp, player);
			}
		}
		return true;
	}

	public boolean onPacketReceive(Player player, Object packet, boolean wasCancelled) {
		if(player == null || packet == null) {
			return true;
		}
		final int id;
		if (packet instanceof CommonSilentPacket) {
			// Do not send to listeners, but unwrap for monitors
			packet = ((CommonSilentPacket) packet).packet;
			id = PacketFields.DEFAULT.packetID.get(packet);
		} else {
			// Handle listeners
			id = PacketFields.DEFAULT.packetID.get(packet);
			if (!LogicUtil.nullOrEmpty(listeners[id])) {
				CommonPacket cp = new CommonPacket(packet, id);
				PacketReceiveEvent ev = new PacketReceiveEvent(player, cp);
				ev.setCancelled(wasCancelled);
				for (PacketListener listener : listeners[id]) {
					listener.onPacketReceive(ev);
				}
				if (ev.isCancelled()) {
					return false;
				}
			}
		}
		// Handle monitors
		if (!LogicUtil.nullOrEmpty(monitors[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			for (PacketMonitor monitor : monitors[id]) {
				monitor.onMonitorPacketReceive(cp, player);
			}
		}
		return true;
	}
}
