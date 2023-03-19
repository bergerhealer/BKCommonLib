package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.proxy.BundleUnwrapperIterator_1_19_4;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBundlePacketHandle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/**
 * Stores packet listeners and monitors registered by plugin and packet type
 */
class PacketHandlerRegistration {
    public final Map<PacketType, List<PacketListener>> listeners = new HashMap<PacketType, List<PacketListener>>();
    public final Map<PacketType, List<PacketMonitor>> monitors = new HashMap<PacketType, List<PacketMonitor>>();
    public final Map<Plugin, List<PacketListener>> listenerPlugins = new HashMap<Plugin, List<PacketListener>>();
    public final Map<Plugin, List<PacketMonitor>> monitorPlugins = new HashMap<Plugin, List<PacketMonitor>>();

    public void forAllListeners(BiConsumer<Plugin, PacketListener> action) {
        for (Map.Entry<Plugin, List<PacketListener>> entry : listenerPlugins.entrySet()) {
            for (PacketListener listener : entry.getValue()) {
                action.accept(entry.getKey(), listener);
            }
        }
    }

    public void forAllMonitors(BiConsumer<Plugin, PacketMonitor> action) {
        for (Map.Entry<Plugin, List<PacketMonitor>> entry : monitorPlugins.entrySet()) {
            for (PacketMonitor monitor : entry.getValue()) {
                action.accept(entry.getKey(), monitor);
            }
        }
    }

    public Collection<Plugin> getListeningPlugins(PacketType type) {
        List<PacketListener> listenerList = listeners.get(type);
        if (listenerList == null) {
            return Collections.emptySet();
        }
        List<Plugin> plugins = new ArrayList<Plugin>();
        for (Map.Entry<Plugin, List<PacketListener>> entry : listenerPlugins.entrySet()) {
            for (PacketListener listener : listenerList) {
                if (entry.getValue().contains(listener)) {
                    plugins.add(entry.getKey());
                    break;
                }
            }
        }
        return plugins;
    }

    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        if (monitor == null) {
            throw new IllegalArgumentException("Monitor is not allowed to be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not allowed to be null");
        }
        // Register the listener
        for (PacketType type : types) {
            // Map to listener array
            List<PacketMonitor> monitorList = monitors.get(type);
            if (monitorList == null) {
                monitorList = new ArrayList<PacketMonitor>();
                monitors.put(type, monitorList);
            }
            monitorList.add(monitor);
            // Map to plugin list
            List<PacketMonitor> list = monitorPlugins.get(plugin);
            if (list == null) {
                list = new ArrayList<PacketMonitor>(2);
                monitorPlugins.put(plugin, list);
            }
            list.add(monitor);
        }
    }

    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener is not allowed to be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not allowed to be null");
        }
        // Register the listener
        for (PacketType type : types) {
            // Map to listener array
            List<PacketListener> listenerList = listeners.get(type);
            if (listenerList == null) {
                listenerList = new ArrayList<PacketListener>();
                listeners.put(type, listenerList);
            }
            listenerList.add(listener);
            // Map to plugin list
            List<PacketListener> list = listenerPlugins.get(plugin);
            if (list == null) {
                list = new ArrayList<PacketListener>(2);
                listenerPlugins.put(plugin, list);
            }
            list.add(listener);
        }
    }

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

    public void removePacketMonitor(PacketMonitor monitor) {
        removePacketMonitor(monitor, true);
    }

    public void removePacketMonitor(PacketMonitor monitor, boolean fromPlugins) {
        if (monitor == null) {
            return;
        }
        for (List<PacketMonitor> monitorList : monitors.values()) {
            monitorList.remove(monitor);
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

    public void removePacketListener(PacketListener listener) {
        removePacketListener(listener, true);
    }

    public void removePacketListener(PacketListener listener, boolean fromPlugins) {
        if (listener == null) {
            return;
        }
        for (List<PacketListener> listenerList : listeners.values()) {
            listenerList.remove(listener);
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

    public boolean handlePacketSend(Player player, PacketType packetType, Object packet, boolean is_silent, boolean wasCancelled) {
        if (packetType == PacketType.OUT_BUNDLE) {
            ClientboundBundlePacketHandle bundle = ClientboundBundlePacketHandle.createHandle(packet);
            if (is_silent) {
                // Only send all sub-packets by the monitors
                for (Object subPacket : BundleUnwrapperIterator_1_19_4.unwrap(bundle.subPackets())) {
                    handlePacketSendMonitor(player, packetType, subPacket);
                }
                return true;
            } else {
                // Filter the packets contained within the bundle packet. If all packets end up filtered,
                // cancel the entire Bundle packet. We skip the monitors as every packet already passes
                // by the monitor.
                return bundle.filterSubPackets(p -> handlePacketSend(player, PacketType.getType(p), p, false, wasCancelled));
            }
        }

        if (!is_silent && !handlePacketSendListener(player, packetType, packet, wasCancelled)) {
            return false;
        }

        // Handle monitors
        handlePacketSendMonitor(player, packetType, packet);
        return true;
    }

    public boolean handlePacketSendListener(Player player, PacketType packetType, Object packet, boolean wasCancelled) {
        List<PacketListener> listenerList = listeners.get(packetType);
        if (listenerList != null) {
            CommonPacket cp = new CommonPacket(packet, packetType);
            PacketSendEvent ev = new PacketSendEvent(player, cp);
            ev.setCancelled(wasCancelled);
            for (PacketListener listener : listenerList) {
                try {
                    listener.onPacketSend(ev);
                } catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onPacketSend handling " + packetType + ":", t);
                }
            }
            if (ev.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    public void handlePacketSendMonitor(Player player, PacketType packetType, Object packet) {
        List<PacketMonitor> monitorList = monitors.get(packetType);
        if (monitorList != null) {
            CommonPacket cp = new CommonPacket(packet, packetType);
            for (PacketMonitor monitor : monitorList) {
                try {
                    monitor.onMonitorPacketSend(cp, player);
                } catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onMonitorPacketSend handling " + packetType + ":", t);
                }
            }
        }
    }

    /**
     * Handles a packet before it is being handled by the server
     *
     * @param player from which the packet came
     * @param packet that is handled
     * @param wasCancelled - True if the packet is allowed to be received, False
     * if not
     * @return True if the packet is allowed to be received, False if not
     */
    public boolean handlePacketReceive(Player player, Object packet, boolean wasCancelled) {
        if (player == null || packet == null) {
            return true;
        }
        // Handle listeners
        PacketType type = PacketType.getType(packet);
        List<PacketListener> listenerList = listeners.get(type);
        if (listenerList != null) {
            CommonPacket cp = new CommonPacket(packet, type);
            PacketReceiveEvent ev = new PacketReceiveEvent(player, cp);
            ev.setCancelled(wasCancelled);
            for (PacketListener listener : listenerList) {
                try {
                    listener.onPacketReceive(ev);
                } catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onPacketReceive handling " + type + ":", t);
                }
            }
            if (ev.isCancelled()) {
                return false;
            }
        }
        // Handle monitors
        List<PacketMonitor> monitorList = monitors.get(type);
        if (monitorList != null) {
            CommonPacket cp = new CommonPacket(packet, type);
            for (PacketMonitor monitor : monitorList) {
                try {
                    monitor.onMonitorPacketReceive(cp, player);
                } catch (Throwable t) {
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error occurred in onMonitorPacketReceive handling " + type + ":", t);
                }
            }
        }
        return true;
    }
}
