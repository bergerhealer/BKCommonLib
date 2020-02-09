package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityMetadataHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutNamedEntitySpawnHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityLivingHandle;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class PacketUtil {

    /**
     * Fakes a packet sent from the Client to the Server for a certain Player.
     *
     * @param player to receive a packet for
     * @param packet to receive
     */
    public static void receivePacket(Player player, PacketHandle packet) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().receivePacket(player, packet.getPacketType(), packet.getRaw());
        }
    }

    /**
     * Fakes a packet sent from the Client to the Server for a certain Player.
     *
     * @param player to receive a packet for
     * @param packet to receive
     */
    public static void receivePacket(Player player, CommonPacket packet) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().receivePacket(player, packet.getType(), packet.getHandle());
        }
    }

    /**
     * Fakes a packet sent from the Client to the Server for a certain Player.<br>
     * <b>Deprecated: Please avoid using raw packet types</b>
     *
     * @param player to receive a packet for
     * @param packet to receive (raw, or a wrapper)
     */
    @Deprecated
    public static void receivePacket(Player player, Object packet) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket) packet).getHandle();
        } else if (packet instanceof PacketHandle) {
            packet = ((PacketHandle) packet).getRaw();
        }
        if (packet == null) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().receivePacket(player, PacketType.getType(packet), packet);
    }

    /**
     * Sends a packet to a player. Should not be used from inside a packet listener/monitor as it will cause packet loss,
     * or unpredictable packet order.
     * The packet is sent through listeners.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     */
    public static void sendPacket(Player player, CommonPacket packet) {
        sendPacket(player, packet, true);
    }

    /**
     * Sends a packet to a player. Should not be used from inside a packet listener/monitor as it will cause packet loss,
     * or unpredictable packet order.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     * @param throughListeners Whether to send the packet through listeners
     */
    public static void sendPacket(Player player, CommonPacket packet, boolean throughListeners) {
        if (packet != null) {
            Object rawPacket = packet.getHandle();
            if (rawPacket != null) {
                CommonPlugin.getInstance().getPacketHandler().sendPacket(player, packet.getType(), rawPacket, throughListeners);
            }
        }
    }

    /**
     * Sends a packet to a player. Should not be used from inside a packet listener/monitor as it will cause packet loss,
     * or unpredictable packet order.
     * The packet is sent through listeners.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     */
    public static void sendPacket(Player player, PacketHandle packet) {
        sendPacket(player, packet, true);
    }

    /**
     * Sends a packet to a player. Should not be used from inside a packet listener/monitor as it will cause packet loss,
     * or unpredictable packet order.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     * @param throughListeners Whether to send the packet through listeners
     */
    public static void sendPacket(Player player, PacketHandle packet, boolean throughListeners) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().sendPacket(player, packet.getPacketType(), packet.getRaw(), throughListeners);
        }
    }

    /**
     * Queues a packet so that it is sent after all packets already in the queue are sent first.
     * Can be used from inside a packet listener/monitor to reliably send a packet after the current packet.
     * The packet is sent through listeners.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     */
    public static void queuePacket(Player player, CommonPacket packet) {
        queuePacket(player, packet, true);
    }

    /**
     * Queues a packet so that it is sent after all packets already in the queue are sent first.
     * Can be used from inside a packet listener/monitor to reliably send a packet after the current packet.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     * @param throughListeners Whether to send the packet through listeners
     */
    public static void queuePacket(Player player, CommonPacket packet, boolean throughListeners) {
        if (packet != null) {
            Object rawPacket = packet.getHandle();
            if (rawPacket != null) {
                CommonPlugin.getInstance().getPacketHandler().queuePacket(player, packet.getType(), rawPacket, throughListeners);
            }
        }
    }

    /**
     * Queues a packet so that it is sent after all packets already in the queue are sent first.
     * Can be used from inside a packet listener/monitor to reliably send a packet after the current packet.
     * The packet is sent through listeners.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     */
    public static void queuePacket(Player player, PacketHandle packet) {
        queuePacket(player, packet, true);
    }

    /**
     * Queues a packet so that it is sent after all packets already in the queue are sent first.
     * Can be used from inside a packet listener/monitor to reliably send a packet after the current packet.
     * 
     * @param player The player to send the packet to
     * @param packet The packet to send
     * @param throughListeners Whether to send the packet through listeners
     */
    public static void queuePacket(Player player, PacketHandle packet, boolean throughListeners) {
        if (packet != null) {
            CommonPlugin.getInstance().getPacketHandler().queuePacket(player, packet.getPacketType(), packet.getRaw(), throughListeners);
        }
    }

    /**
     * Sends the spawn packet for a living entity. On MC 1.15 and later the metadata for the entity is sent separate
     * from the spawn packet of the living entity.
     * 
     * @param player
     * @param packet
     * @param metadata
     */
    @SuppressWarnings("deprecation")
    public static void sendEntityLivingSpawnPacket(Player player, PacketPlayOutSpawnEntityLivingHandle packet, DataWatcher metadata) {
        if (packet.hasDataWatcherSupport()) {
            packet.setDataWatcher(metadata);
            sendPacket(player, packet);
        } else {
            sendPacket(player, packet);
            sendPacket(player, PacketPlayOutEntityMetadataHandle.createNew(packet.getEntityId(), metadata, true));
        }
    }

    /**
     * Sends the spawn packet for a named entity. On MC 1.15 and later the metadata for the entity is sent separate
     * from the spawn packet of the named entity.
     * 
     * @param player
     * @param packet
     * @param metadata
     */
    @SuppressWarnings("deprecation")
    public static void sendNamedEntitySpawnPacket(Player player, PacketPlayOutNamedEntitySpawnHandle packet, DataWatcher metadata) {
        if (packet.hasDataWatcherSupport()) {
            packet.setDataWatcher(metadata);
            sendPacket(player, packet);
        } else {
            sendPacket(player, packet);
            sendPacket(player, PacketPlayOutEntityMetadataHandle.createNew(packet.getEntityId(), metadata, true));
        }
    }

    /**
     * Sends a raw packet to a player. All wrapper types for Packet are supported as well.<br>
     * <b>Deprecated: Please avoid using raw packet types</b>
     * 
     * @param player to send to
     * @param packet to send (raw, or a wrapper)
     */
    @Deprecated
    public static void sendPacket(Player player, Object packet) {
        sendPacket(player, packet, true);
    }

    /**
     * Sends a raw packet to a player. All wrapper types for Packet are supported as well.<br>
     * <b>Deprecated: Please avoid using raw packet types</b>
     * 
     * @param player to send to
     * @param packet to send
     * @param throughListeners whether to let packet listeners see this packet
     */
    @Deprecated
    public static void sendPacket(Player player, Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket) packet).getHandle();
        } else if (packet instanceof PacketHandle) {
            packet = ((PacketHandle) packet).getRaw();
        }
        if (packet == null) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().sendPacket(player, PacketType.getType(packet), packet, throughListeners);
    }

    public static void broadcastBlockPacket(Block block, Object packet, boolean throughListeners) {
        broadcastBlockPacket(block.getWorld(), block.getX(), block.getZ(), packet, throughListeners);
    }

    public static void broadcastBlockPacket(org.bukkit.World world, final int x, final int z, Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket) packet).getHandle();
        }
        if (world == null || packet == null) {
            return;
        }
        for (Player player : WorldUtil.getPlayers(world)) {
            if (EntityUtil.isNearBlock(player, x, z, CommonUtil.BLOCKVIEW)) {
                sendPacket(player, packet, throughListeners);
            }
        }
    }

    public static void broadcastChunkPacket(org.bukkit.Chunk chunk, Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket) packet).getHandle();
        }
        if (chunk == null || packet == null) {
            return;
        }

        for (Player player : WorldUtil.getPlayers(chunk.getWorld())) {
            if (EntityUtil.isNearChunk(player, chunk.getX(), chunk.getZ(), CommonUtil.VIEW)) {
                sendPacket(player, packet, throughListeners);
            }
        }
    }

    public static void broadcastPacket(Object packet, boolean throughListeners) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket) packet).getHandle();
        }
        for (Player player : CommonUtil.getOnlinePlayers()) {
            sendPacket(player, packet, throughListeners);
        }
    }

    /**
     * Sends a packet relating a certain entity to all players that can see it.
     * If the entity is a player itself, he also receives the packet.
     * 
     * @param entity the packet is about
     * @param packet to send
     */
    public static void broadcastEntityPacket(Entity entity, CommonPacket packet) {
        broadcastEntityPacket(entity, packet, true);
    }

    /**
     * Sends a packet relating a certain entity to all players that can see it
     * 
     * @param entity the packet is about
     * @param packet to send
     * @param sendToSelf whether to also send to the player itself, if the entity is a player
     */
    public static void broadcastEntityPacket(Entity entity, CommonPacket packet, boolean sendToSelf) {
        if (entity == null || packet == null) return;

        EntityTracker tracker = WorldUtil.getTracker(entity.getWorld());
        EntityTrackerEntryHandle entry = tracker.getEntry(entity);
        if (entry != null) {
            for (Player viewer : entry.getViewers()) {
                sendPacket(viewer, packet);
            }
        }
        if (sendToSelf && entity instanceof Player) {
            sendPacket((Player) entity, packet);
        }
    }

    /**
     * Adds a single packet monitor. Packet monitors only monitor (not change)
     * packets.
     *
     * @param plugin to register for
     * @param monitor to register
     * @param packets to register for
     */
    public static void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType... packets) {
        if (monitor == null || LogicUtil.nullOrEmpty(packets)) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().addPacketMonitor(plugin, monitor, packets);
    }

    /**
     * Adds a single packet listener. Packet listeners can modify packets.
     *
     * @param plugin to register for
     * @param listener to register
     * @param packets to register for
     */
    public static void addPacketListener(Plugin plugin, PacketListener listener, PacketType... packets) {
        if (listener == null || LogicUtil.nullOrEmpty(packets)) {
            return;
        }
        CommonPlugin.getInstance().getPacketHandler().addPacketListener(plugin, listener, packets);
    }

    /**
     * Removes all packet listeners AND monitors of a plugin
     *
     * @param plugin to remove the registered monitors and listeners of
     */
    public static void removePacketListeners(Plugin plugin) {
        CommonPlugin.getInstance().getPacketHandler().removePacketListeners(plugin);
    }

    /**
     * Removes a single registered packet listener
     *
     * @param listener to remove
     */
    public static void removePacketListener(PacketListener listener) {
        CommonPlugin.getInstance().getPacketHandler().removePacketListener(listener);
    }

    /**
     * Removes a single registered packet monitor
     *
     * @param monitor to remove
     */
    public static void removePacketMonitor(PacketMonitor monitor) {
        CommonPlugin.getInstance().getPacketHandler().removePacketMonitor(monitor);
    }

    public static void broadcastPacketNearby(Location location, double radius, CommonPacket packet) {
        broadcastPacketNearby(location, radius, packet.getHandle());
    }

    public static void broadcastPacketNearby(Location location, double radius, PacketHandle packet) {
        broadcastPacketNearby(location, radius, packet.getRaw());
    }

    public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, CommonPacket packet) {
        broadcastPacketNearby(world, x, y, z, radius, packet.getHandle());
    }

    public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, PacketHandle packet) {
        broadcastPacketNearby(world, x, y, z, radius, packet.getRaw());
    }

    @Deprecated
    public static void broadcastPacketNearby(Location location, double radius, Object packet) {
        broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
    }

    @Deprecated
    public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, Object packet) {
        if (packet instanceof CommonPacket) {
            packet = ((CommonPacket) packet).getHandle();
        }
        CommonNMS.getPlayerList().sendRawPacketNearby(world, x, y, z, radius, packet);
    }

    /**
     * Obtains a collection of all plugins currently listening for the Packet
     * type specified. Packets of this type can be expected to be handled by
     * these plugins when sending it.
     *
     * @param packetType to get the listening plugins for
     * @return collection of listening plugins
     */
    public static Collection<Plugin> getListenerPlugins(PacketType packetType) {
        return CommonPlugin.getInstance().getPacketHandler().getListening(packetType);
    }
}
