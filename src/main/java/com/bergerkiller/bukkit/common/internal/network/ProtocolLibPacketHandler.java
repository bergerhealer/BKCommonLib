package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.PlayerConnectionHandle;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.injector.PlayerLoggedOutException;
import com.comphenix.protocol.injector.packet.PacketRegistry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

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
        for (CommonPacketMonitor monitor : this.monitors) {
            ProtocolLibrary.getProtocolManager().removePacketListener(monitor);
        }
        for (CommonPacketListener listener : this.listeners) {
            ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        }
        this.monitors.clear();
        this.listeners.clear();
        if (!CommonUtil.isShuttingDown()) {
            Logging.LOGGER_NETWORK.warning("Reload detected! ProtocolLib does not officially support reloading the server!");
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                Logging.LOGGER_NETWORK.warning("Players are logged in. This is known to cause complete lock-out of the server!");
                Logging.LOGGER_NETWORK.warning("If you must absolutely reload, do so from the server terminal with no players logged in");
            }
        }
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
    public void receivePacket(Player player, PacketType type, Object packet) {
        if (PacketHandlerHooked.getPlayerConnection(player) == null) {
            return; // NPC player is not connected
        }

        type.preprocess(packet);
        PacketContainer toReceive = new PacketContainer(getPacketType(packet.getClass()), packet);
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player, toReceive);
        } catch (PlayerLoggedOutException ex) {
            // Ignore
        } catch (Exception e) {
            throw new RuntimeException("Error while receiving packet:", e);
        }
    }

    @Override
    public void sendPacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        Object connection = PacketHandlerHooked.getPlayerConnection(player);
        if (connection == null) {
            return; // Player is an NPC or isn't connected
        }

        type.preprocess(packet);

        // Simplified logic for sending normally
        if (throughListeners) {
            PlayerConnectionHandle.T.sendPacket.raw.invoke(connection, packet);
            return;
        }

        // Silent - do not send it through listeners, only through monitors
        try {
            PacketContainer toSend = new PacketContainer(getPacketType(packet.getClass()), packet);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, toSend, null, false);
        } catch (PlayerLoggedOutException ex) {
            // Ignore
        } catch (Throwable t) {
            throw new RuntimeException("Error while sending packet:", t);
        }
    }

    @Override
    public void removePacketListeners(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);

        // Remove all listeners of this plugin
        Iterator<CommonPacketListener> list_iter = listeners.iterator();
        while (list_iter.hasNext()) {
            if (list_iter.next().getPlugin() == plugin) {
                list_iter.remove();
            }
        }

        // Remove all monitors of this plugin
        Iterator<CommonPacketMonitor> mon_iter = monitors.iterator();
        while (mon_iter.hasNext()) {
            if (mon_iter.next().getPlugin() == plugin) {
                mon_iter.remove();
            }
        }
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

    private static class CommonPacketListener extends CommonPacketAdapter {

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
                if (type.getType() == null) {
                    continue;
                }
                com.comphenix.protocol.PacketType comType = getPacketType(type);
                if (comType != null) {
                    comTypes.add(comType);
                }
            }
            return ListeningWhitelist.newBuilder().priority(priority).types(comTypes)
                    .gamePhase(GamePhase.PLAYING).options(new ListenerOptions[] { ListenerOptions.ASYNC } ).build();
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
