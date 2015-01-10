package com.bergerkiller.bukkit.common.internal.network;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.PlayerLoggedOutException;
import com.comphenix.protocol.injector.packet.PacketRegistry;

/**
 * A packet handler implementation that uses ProtocolLib packet listeners
 */
public class ProtocolLibPacketHandler implements PacketHandler {
	public static final String LIB_ROOT = "com.comphenix.protocol.";
	private final List<CommonPacketMonitor> monitors = new ArrayList<CommonPacketMonitor>();
	private final List<CommonPacketListener> listeners = new ArrayList<CommonPacketListener>();

	@Override
	public void onPlayerJoin(Player player) {
	}

	@Override
	public boolean onEnable() {
		// Check whether all required classes are available
		Class<?> manager = CommonUtil.getClass(LIB_ROOT + "ProtocolManager");
		Class<?> packetContainer = CommonUtil.getClass(LIB_ROOT + "events.PacketContainer");
		if (manager == null || packetContainer == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onDisable() {
		return true;
	}

	@Override
	public String getName() {
		return "the ProtocolLib library";
	}

	@Override
	public Collection<Plugin> getListening(PacketType packetType) {
		Set<Plugin> plugins = new HashSet<Plugin>();
		// Obtain all plugins that have a listener (ignore monitors)
		boolean outGoing = packetType.isOutGoing();
		com.comphenix.protocol.PacketType comType = getPacketType(packetType);
		for (com.comphenix.protocol.events.PacketListener listener : ProtocolLibrary.getProtocolManager().getPacketListeners()) {
			final ListeningWhitelist whitelist;
			if (outGoing) {
				whitelist = listener.getSendingWhitelist();
			} else {
				whitelist = listener.getReceivingWhitelist();
			}
			if (whitelist.getPriority() != ListenerPriority.MONITOR && whitelist.getTypes().contains(comType)) {
				plugins.add(listener.getPlugin());
			}
		}
		return plugins;
	}

	@Override
	public void receivePacket(Player player, Object packet) {
		if (isNPCPlayer(player) || PlayerUtil.isDisconnected(player)) {
			return;
		}
		PacketContainer toReceive = new PacketContainer(getPacketType(packet.getClass()), packet);
		try{
			ProtocolLibrary.getProtocolManager().recieveClientPacket(player, toReceive);
		} catch (PlayerLoggedOutException ex) {
			// Ignore
		} catch (Exception e) {
			throw new RuntimeException("Error while receiving packet:", e);
		}
	}

	@Override
	public void sendPacket(Player player, Object packet, boolean throughListeners) {
		if (isNPCPlayer(player) || PlayerUtil.isDisconnected(player)) {
			return;
		}
		PacketContainer toSend = new PacketContainer(getPacketType(packet.getClass()), packet);
		try {
			if (throughListeners) {
				// Send it through the listeners
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, toSend);
			} else {
				// Silent - do not send it through listeners, only through monitors
				sendSilentPacket(player, toSend);
			}
		} catch (PlayerLoggedOutException ex) {
			// Ignore
		} catch (Exception e) {
			throw new RuntimeException("Error while sending packet:", e);
		}
	}

	private boolean isNPCPlayer(Player player) {
		// Is this check still needed?
		Object handle = Conversion.toEntityHandle.convert(player);
		return !handle.getClass().equals(CommonUtil.getNMSClass("EntityPlayer"));
	}

	private void sendSilentPacket(Player player, PacketContainer packet) throws InvocationTargetException {
		ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, null, false);
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
	public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
		CommonPacketListener commonListener = new CommonPacketListener(plugin, listener, types);
		ProtocolLibrary.getProtocolManager().addPacketListener(commonListener);
		this.listeners.add(commonListener);
	}

	@Override
	public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
		CommonPacketMonitor commonMonitor = new CommonPacketMonitor(plugin, monitor, types);
		ProtocolLibrary.getProtocolManager().addPacketListener(commonMonitor);
		this.monitors.add(commonMonitor);
	}

	@Override
	public void transfer(PacketHandler to) {
		for (CommonPacketListener listener : listeners) {
			to.addPacketListener(listener.getPlugin(), listener.listener, listener.types);
		}
		for (CommonPacketMonitor monitor : monitors) {
			to.addPacketMonitor(monitor.getPlugin(), monitor.monitor, monitor.types);
		}
	}

	@Override
	public long getPendingBytes(Player player) {
		return PacketHandlerHooked.calculatePendingBytes(player);
	}

	private static com.comphenix.protocol.PacketType getPacketType(PacketType commonType) {
		return getPacketType(commonType.getType());
	}

	private static com.comphenix.protocol.PacketType getPacketType(Class<?> packetClass) {
		return PacketRegistry.getPacketType(packetClass);
	}

	private static class CommonPacketMonitor extends CommonPacketAdapter {
		public final PacketMonitor monitor;

		public CommonPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
			super(plugin, ListenerPriority.MONITOR, types);
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

		public CommonPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
			super(plugin, ListenerPriority.NORMAL, types);
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
		public final PacketType[] types;
		private final ListeningWhitelist receiving;
		private final ListeningWhitelist sending;

		public CommonPacketAdapter(Plugin plugin, ListenerPriority priority, PacketType[] types) {
			this.plugin = plugin;
			this.types = types;
			this.receiving = getWhiteList(priority, types, true);
			this.sending = getWhiteList(priority, types, false);
		}

		private static ListeningWhitelist getWhiteList(ListenerPriority priority, PacketType[] types, boolean receiving) {
			List<com.comphenix.protocol.PacketType> comTypes = new ArrayList<com.comphenix.protocol.PacketType>();
			for (PacketType type : types) {
				if ((!type.isOutGoing()) != receiving) {
					continue;
				}
				com.comphenix.protocol.PacketType comType = getPacketType(type);
				comTypes.add(comType);
			}
			
			return ListeningWhitelist.newBuilder().priority(priority).types(comTypes)
					.gamePhase(GamePhase.PLAYING).options(new ListenerOptions[0]).build();
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
