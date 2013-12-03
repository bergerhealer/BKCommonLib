package com.bergerkiller.bukkit.common.internal.network;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.NetworkManagerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;

/**
 * Basic packet handler implementation for handling packets using a send/receive hook.
 * The {@link #handlePacketSend(Player, Object, boolean) handlePacketSend(player, packet, wasCancelled)}
 * and {@link #handlePacketReceive(Player, Object, boolean) handlePacketReceive(player, packet, wasCancelled)} methods should be called
 * by an additional listener hook.
 */
public abstract class PacketHandlerHooked implements PacketHandler {
	@SuppressWarnings("unchecked")
	private final List<PacketListener>[] listeners = new ArrayList[256];
	@SuppressWarnings("unchecked")
	private final List<PacketMonitor>[] monitors = new ArrayList[256];
	private final Map<Plugin, List<PacketListener>> listenerPlugins = new HashMap<Plugin, List<PacketListener>>();
	private final Map<Plugin, List<PacketMonitor>> monitorPlugins = new HashMap<Plugin, List<PacketMonitor>>();
	private final ClassMap<SafeMethod<?>> receiverMethods = new ClassMap<SafeMethod<?>>();

	@Override
	public boolean onEnable() {
		// Initialize all receiver methods
		Class<?> packetType = PacketFields.DEFAULT.getType();
		for (Method method : PlayerConnectionRef.TEMPLATE.getType().getDeclaredMethods()) {
			if (method.getReturnType() != void.class || method.getParameterTypes().length != 1 
					|| !Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			Class<?> arg = method.getParameterTypes()[0];
			if (!packetType.isAssignableFrom(arg) || arg == packetType) {
				continue;
			}
			receiverMethods.put(arg, new SafeMethod<Void>(method));
		}
		return true;
	}

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
			if (id == -1) {
				continue;
			}
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
			if (id == -1) {
				continue;
			}
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
	public void receivePacket(Player player, Object packet) {
		SafeMethod<?> method = this.receiverMethods.get(packet);
		if (method == null) {
			CommonPlugin.LOGGER_NETWORK.log(Level.WARNING, "Could not find suitable packet handler for " + packet.getClass().getSimpleName());
		} else {
			method.invoke(getPlayerConnection(player), packet);
		}
	}

	public abstract void sendSilentPacket(Player player, Object packet);

	@Override
	public void sendPacket(Player player, Object packet, boolean throughListeners) {
		Object handle = Conversion.toEntityHandle.convert(player);
		if (!handle.getClass().equals(CommonUtil.getNMSClass("EntityPlayer"))) {
			return;
		}
		if (!PacketFields.DEFAULT.isInstance(packet) || PlayerUtil.isDisconnected(player)) {
			return;
		}
		if (throughListeners) {
			final Object connection = EntityPlayerRef.playerConnection.get(handle);
			PlayerConnectionRef.sendPacket(connection, packet);
		} else {
			handlePacketSendMonitor(player, PacketFields.DEFAULT.packetID.get(packet), packet);
			sendSilentPacket(player, packet);
		}
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

	protected Object getPlayerConnection(Player player) {
		return EntityPlayerRef.playerConnection.get(Conversion.toEntityHandle.convert(player));
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

	/**
	 * Handles a packet before it is being sent to a player
	 * 
	 * @param player for which the packet was meant
	 * @param packet that is handled
	 * @param wasCancelled - True if it was originally cancelled, False if not
	 * @return True if the packet is allowed to be sent, False if not
	 */
	public boolean handlePacketSend(Player player, Object packet, boolean wasCancelled) {
		if(player == null || packet == null) {
			return true;
		}
		// Handle listeners
		final int id = PacketFields.DEFAULT.packetID.get(packet);
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
		// Handle monitors
		handlePacketSendMonitor(player, id, packet);
		return true;
	}

	private void handlePacketSendMonitor(Player player, int packetId, Object packet) {
		if (!LogicUtil.nullOrEmpty(monitors[packetId])) {
			CommonPacket cp = new CommonPacket(packet, packetId);
			for (PacketMonitor monitor : monitors[packetId]) {
				monitor.onMonitorPacketSend(cp, player);
			}
		}
	}

	/**
	 * Handles a packet before it is being handled by the server
	 * 
	 * @param player from which the packet came
	 * @param packet that is handled
	 * @param wasCancelled - True if the packet is allowed to be received, False if not
	 * @return True if the packet is allowed to be received, False if not
	 */
	public boolean handlePacketReceive(Player player, Object packet, boolean wasCancelled) {
		if(player == null || packet == null) {
			return true;
		}
		// Handle listeners
		final int id = PacketFields.DEFAULT.packetID.get(packet);
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
		// Handle monitors
		if (!LogicUtil.nullOrEmpty(monitors[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			for (PacketMonitor monitor : monitors[id]) {
				monitor.onMonitorPacketReceive(cp, player);
			}
		}
		return true;
	}

	protected static long calculatePendingBytes(Player player) {
		final Object playerHandle = Conversion.toEntityHandle.convert(player);
		final Object playerConnection = EntityPlayerRef.playerConnection.get(playerHandle);
		final Object nm = PlayerConnectionRef.networkManager.get(playerConnection);
		// We can only work on Network manager implementations, INetworkManager implementations are unknown to us
		if (!NetworkManagerRef.TEMPLATE.isInstance(nm)) {
			return 0L;
		}
		Object lockObject = NetworkManagerRef.lockObject.get(nm);
		if (lockObject == null) {
			return 0L;
		}
		List<Object> low = NetworkManagerRef.lowPriorityQueue.get(nm);
		List<Object> high = NetworkManagerRef.highPriorityQueue.get(nm);
		if (low == null || high == null) {
			return 0L;
		}
		long queuedsize = 0;
		synchronized (lockObject) {
			for (Object p : low) {
				queuedsize += PacketFields.DEFAULT.getPacketSize(p) + 1;
			}
			for (Object p : high) {
				queuedsize += PacketFields.DEFAULT.getPacketSize(p) + 1;
			}
		}
		return queuedsize;
	}
}
