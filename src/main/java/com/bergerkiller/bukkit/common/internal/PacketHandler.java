package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * All the methods needed for internally handling the packet sending and
 * receiving
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

    /**
     * Sends a packet right now. Should not be used from packet monitor or listener callbacks,
     * as that can cause a deadlock.
     *
     * @param player
     * @param type
     * @param packet
     * @param throughListeners
     */
    public void sendPacket(Player player, PacketType type, Object packet, boolean throughListeners);

    /**
     * Sends a packet after the handling of the current packet. This should be called on
     * the thread currently executing a packet monitor or listener. If not currently
     * handling a packet, behaves the same as
     * {@link #sendPacket(Player, PacketType, Object, boolean)}.
     *
     * @param player
     * @param type
     * @param packet
     * @param throughListeners
     */
    public void queuePacket(Player player, PacketType type, Object packet, boolean throughListeners);

    public void receivePacket(Player player, PacketType type, Object packet);

    public Collection<Plugin> getListening(PacketType packetType);

    public void transfer(PacketHandler to);

    /**
     * Gets the name of this type of Packet Handler
     *
     * @return packet handler name
     */
    public String getName();

    /**
     * Called when this Packet Handler has to be enabled. This method should
     * take care of registering packet hooks or listeners.
     *
     * @return True if the handler successfully enabled, False if not
     */
    public boolean onEnable();

    /**
     * Called when this Packet Handler has to be disabled. This method should
     * take care of removing any packet hooks or listeners.
     *
     * @return True if the handler successfully disabled, False if not
     */
    public boolean onDisable();

    /**
     * Called when a new player joins the server and potentially needs a
     * listener hook
     *
     * @param player that joined
     */
    public void onPlayerJoin(Player player);
}
