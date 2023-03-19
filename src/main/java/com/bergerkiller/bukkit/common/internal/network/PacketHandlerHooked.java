package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.server.network.PlayerConnectionHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

/**
 * Basic packet handler implementation for handling packets using a send/receive
 * hook. The
 * {@link #handlePacketSend(Player, Object, boolean) handlePacketSend(player, packet, wasCancelled)}
 * and
 * {@link #handlePacketReceive(Player, Object, boolean) handlePacketReceive(player, packet, wasCancelled)}
 * methods should be called by an additional listener hook.
 */
public abstract class PacketHandlerHooked implements PacketHandler {

    private final PacketHandlerRegistration handlers = new PacketHandlerRegistration();
    private final ClassMap<SafeMethod<?>> receiverMethods = new ClassMap<SafeMethod<?>>();
    private final SilentPacketQueue silentQueue = new SilentPacketQueue();

    @Override
    public boolean onEnable() {
        // Initialize all receiver methods
        Class<?> packetType = PacketHandle.T.getType();
        for (Method method : PlayerConnectionHandle.T.getType().getDeclaredMethods()) {
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
        handlers.removePacketListeners(plugin);
    }

    @Override
    public void removePacketMonitor(PacketMonitor monitor) {
        handlers.removePacketMonitor(monitor);
    }

    @Override
    public void removePacketListener(PacketListener listener) {
        handlers.removePacketListener(listener);
    }

    @Override
    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        handlers.addPacketMonitor(plugin, monitor, types);
    }

    @Override
    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        handlers.addPacketListener(plugin, listener, types);
    }

    @Override
    public void receivePacket(final Player player, final PacketType type, final Object packet) {
        // If not main thread, schedule a next-tick task to run it
        if (!CommonUtil.isMainThread()) {
            CommonUtil.nextTick(() -> receivePacket(player, type, packet));
            return;
        }

        // Handle receiving (main thread)
        type.preprocess(packet);
        SafeMethod<?> method = this.receiverMethods.get(packet);
        if (method == null) {
        	Logging.LOGGER_NETWORK.log(Level.WARNING, "Could not find suitable packet handler for " + packet.getClass().getSimpleName());
        } else {
            PlayerConnectionHandle connection = PlayerConnectionHandle.forPlayer(player);
            if (connection != null) {
                // We are bypassing the hook - make sure to handle receive packet listener
                if (this.handlePacketReceive(player, packet, false)) {
                    method.invoke(connection.getRaw(), packet);
                }
            }
        }
    }

    @Override
    public void sendPacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        type.preprocess(packet);
        PlayerConnectionHandle connection = PlayerConnectionHandle.forPlayer(player);
        if (connection == null) {
            return;
        }

        if (!throughListeners) {
            this.silentQueue.add(player, packet);
        }

        connection.sendPacket(packet);
    }

    @Override
    public void queuePacket(Player player, PacketType type, Object packet, boolean throughListeners) {
        type.preprocess(packet);
        PlayerConnectionHandle connection = PlayerConnectionHandle.forPlayer(player);
        if (connection == null) {
            return;
        }

        if (!throughListeners) {
            this.silentQueue.add(player, packet);
        }

        connection.queuePacket(packet);
    }

    @Override
    public Collection<Plugin> getListening(PacketType type) {
        return handlers.getListeningPlugins(type);
    }

    @Override
    public void transfer(PacketHandler to) {
        handlers.forAllListeners((p, listener) -> to.addPacketListener(p, listener, getListenerTypes(listener)));
        handlers.forAllMonitors((p, monitor) -> to.addPacketMonitor(p, monitor, getMonitorTypes(monitor)));
    }

    private PacketType[] getListenerTypes(PacketListener listener) {
        ArrayList<PacketType> list = new ArrayList<PacketType>();
        for (Map.Entry<PacketType, List<PacketListener>> entry : handlers.listeners.entrySet()) {
            if (entry.getValue().contains(listener)) {
                list.add(entry.getKey());
            }
        }
        return list.toArray(new PacketType[list.size()]);
    }

    private PacketType[] getMonitorTypes(PacketMonitor listener) {
        ArrayList<PacketType> list = new ArrayList<PacketType>();
        for (Map.Entry<PacketType, List<PacketMonitor>> entry : handlers.monitors.entrySet()) {
            if (entry.getValue().contains(listener)) {
                list.add(entry.getKey());
            }
        }
        return list.toArray(new PacketType[list.size()]);
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
        if (player == null || packet == null) {
            return true;
        }

        // Check if silent
        boolean is_silent = this.silentQueue.take(player, packet);

        // Handle listeners
        PacketType type = PacketType.getType(packet);

        return handlers.handlePacketSend(player, type, packet, is_silent, wasCancelled);
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
        return handlers.handlePacketReceive(player, packet, wasCancelled);
    }
}
