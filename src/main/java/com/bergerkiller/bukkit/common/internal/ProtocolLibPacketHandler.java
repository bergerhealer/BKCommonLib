package com.bergerkiller.bukkit.common.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

class ProtocolLibPacketHandler implements PacketHandler {
	private final List<CommonPacketMonitor> monitors = new ArrayList<CommonPacketMonitor>();
	private final List<CommonPacketListener> listeners = new ArrayList<CommonPacketListener>();

	@Override
	public Collection<Plugin> getListening(int packetId) {
		Integer id = Integer.valueOf(packetId);
		Set<Plugin> plugins = new HashSet<Plugin>();
		// Obtain all plugins that have a listener (ignore monitors)
		for (com.comphenix.protocol.events.PacketListener listener : ProtocolLibrary.getProtocolManager().getPacketListeners()) {
			if (listener.getSendingWhitelist().getWhitelist().contains(id) && listener.getSendingWhitelist().getPriority() != ListenerPriority.MONITOR) {
				plugins.add(listener.getPlugin());
			}
		}
		return plugins;
	}

	@Override
	public void sendPacket(Player player, Object packet, boolean throughListeners) {
		PacketContainer toSend = new PacketContainer(PacketFields.DEFAULT.packetID.get(packet), packet);
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, toSend, throughListeners);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Error while sending packet!", e);
		}
	}

	@Override
	public void removePacketListeners(Plugin plugin) {
		ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
	}

	@Override
	public void removePacketListener(PacketListener listener) {
		Iterator<CommonPacketListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			CommonPacketListener cpl = iter.next();
			if (cpl.listener == listener) {
				ProtocolLibrary.getProtocolManager().removePacketListener(cpl);
				iter.remove();
			}
		}
	}

	@Override
	public void removePacketMonitor(PacketMonitor monitor) {
		Iterator<CommonPacketMonitor> iter = monitors.iterator();
		while (iter.hasNext()) {
			CommonPacketMonitor cpm = iter.next();
			if (cpm.monitor == monitor) {
				ProtocolLibrary.getProtocolManager().removePacketListener(cpm);
				iter.remove();
			}
		}
	}

	@Override
	public void addPacketListener(Plugin plugin, PacketListener listener, int[] ids) {
		ProtocolLibrary.getProtocolManager().addPacketListener(new CommonPacketListener(plugin, listener, ids));
	}

	@Override
	public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, int[] ids) {
		ProtocolLibrary.getProtocolManager().addPacketListener(new CommonPacketMonitor(plugin, monitor, ids));
	}

	@Override
	public void transfer(PacketHandler to) {
		for (CommonPacketListener listener : listeners) {
			to.addPacketListener(listener.getPlugin(), listener.listener, listener.ids);
		}
		for (CommonPacketMonitor monitor : monitors) {
			to.addPacketMonitor(monitor.getPlugin(), monitor.monitor, monitor.ids);
		}
	}

	private static class CommonPacketMonitor extends CommonPacketAdapter {
		public final PacketMonitor monitor;

		public CommonPacketMonitor(Plugin plugin, PacketMonitor monitor, int[] ids) {
			super(plugin, ListenerPriority.MONITOR, ids);
			this.monitor = monitor;
		}

		@Override
		public void onPacketReceiving(PacketEvent event) {
			monitor.onMonitorPacketReceive(new CommonPacket(event.getPacket().getHandle()), event.getPlayer());
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			monitor.onMonitorPacketSend(new CommonPacket(event.getPacket().getHandle()), event.getPlayer());
		}
	}

	private static class CommonPacketListener extends CommonPacketAdapter  {
		public final PacketListener listener;

		public CommonPacketListener(Plugin plugin, PacketListener listener, int[] ids) {
			super(plugin, ListenerPriority.NORMAL, ids);
			this.listener = listener;
		}

		@Override
		public void onPacketReceiving(PacketEvent event) {
			CommonPacket packet = new CommonPacket(event.getPacket().getHandle());
			PacketReceiveEvent receiveEvent = new PacketReceiveEvent(event.getPlayer(), packet);
			receiveEvent.setCancelled(event.isCancelled());
			listener.onPacketReceive(receiveEvent);
			event.setCancelled(receiveEvent.isCancelled());
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			CommonPacket packet = new CommonPacket(event.getPacket().getHandle());
			PacketSendEvent sendEvent = new PacketSendEvent(event.getPlayer(), packet);
			sendEvent.setCancelled(event.isCancelled());
			listener.onPacketSend(sendEvent);
			event.setCancelled(sendEvent.isCancelled());
		}
	}

	private static abstract class CommonPacketAdapter implements com.comphenix.protocol.events.PacketListener {
		private final Plugin plugin;
		public final int[] ids;
		private final ListeningWhitelist receiving;
		private final ListeningWhitelist sending;

		public CommonPacketAdapter(Plugin plugin, ListenerPriority priority, int[] ids) {
			this.plugin = plugin;
			this.ids = ids;
			this.receiving = getWhiteList(priority, ids, true);
			this.sending = getWhiteList(priority, ids, false);
		}

		private static ListeningWhitelist getWhiteList(ListenerPriority priority, int[] packets, boolean receiving) {
			List<Integer> supportedPackets = new ArrayList<Integer>();
			for (int packet : packets) {
				if ((receiving && Packets.Client.isSupported(packet)) || (!receiving && Packets.Server.isSupported(packet))) {
					supportedPackets.add(packet);
				}
			}
			return new ListeningWhitelist(priority, supportedPackets.toArray(new Integer[0]));
		}
	
		@Override
		public Plugin getPlugin() {
			return plugin;
		}

		@Override
		public ListeningWhitelist getReceivingWhitelist() {
			return receiving;
		}

		@Override
		public ListeningWhitelist getSendingWhitelist() {
			return sending;
		}
	}

}
