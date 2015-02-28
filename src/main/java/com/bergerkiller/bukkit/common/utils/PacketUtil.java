package com.bergerkiller.bukkit.common.utils;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.server.v1_8_R1.Packet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRef;

public class PacketUtil {

	/**
	 * Sends all the packets required to properly display a chunk to a player
	 * 
	 * @param player to send to
	 * @param chunk to send the information of
	 */
	public static void sendChunk(Player player, org.bukkit.Chunk chunk) {
		sendChunk(player, chunk, true);
	}

	/**
	 * Sends all the packets required to properly display a chunk to a player.
	 * To only send (Tile)Entity related information, use a 'sendPayload' of False.
	 * 
	 * @param player to send to
	 * @param chunk to send the information of
	 * @param sendPayload - whether the block data is sent
	 */
	public static void sendChunk(final Player player, final org.bukkit.Chunk chunk, boolean sendPayload) {
		final Object chunkHandle = Conversion.toChunkHandle.convert(chunk);
		ChunkRef.seenByPlayer.set(chunkHandle, true);

		// Send payload
		if (sendPayload) {
			//sendPacket(player, PacketType.OUT_MAP_CHUNK_BULK.newInstance(Arrays.asList(chunk), 47));
		}
		// Tile entities
		CommonPacket packet;
		for (Object tile : ChunkRef.tileEntities.get(chunkHandle).values()) {
			if ((packet = BlockUtil.getUpdatePacket(tile)) != null) {
				PacketUtil.sendPacket(player, packet);
			}
		}

		// Entity spawn messages
		CommonUtil.nextTick(new Runnable() {
                        @Override
			public void run() {
				WorldUtil.getTracker(player.getWorld()).spawnEntities(player, chunk);
			}
		});
	}

	/**
	 * Fakes a packet sent from the Client to the Server for a certain Player.
	 * 
	 * @param player to receive a packet for
	 * @param packet to receive
	 */
	public static void receivePacket(Player player, CommonPacket packet) {
		receivePacket(player, (Object) packet);
	}

	/**
	 * Fakes a packet sent from the Client to the Server for a certain Player.
	 * 
	 * @param player to receive a packet for
	 * @param packet to receive
	 */
	public static void receivePacket(Player player, Object packet) {
		if (packet instanceof CommonPacket) {
			packet = ((CommonPacket) packet).getHandle();
		}
		if (packet == null) {
			return;
		}
		CommonPlugin.getInstance().getPacketHandler().receivePacket(player, packet);
	}

	public static void sendPacket(Player player, CommonPacket packet) {
		sendPacket(player, packet, true);
	}

	public static void sendPacket(Player player, Object packet) {
		sendPacket(player, packet, true);
	}

	public static void sendPacket(Player player, CommonPacket packet, boolean throughListeners) {
		sendPacket(player, (Object) packet, throughListeners);
	}

	public static void sendPacket(Player player, Object packet, boolean throughListeners) {
		if (packet instanceof CommonPacket) {
			packet = ((CommonPacket) packet).getHandle();
		}
		if (packet == null) {
			return;
		}
		CommonPlugin.getInstance().getPacketHandler().sendPacket(player, packet, throughListeners);
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
	 * Adds a single packet monitor. Packet monitors only monitor (not change) packets.
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

	public static void broadcastPacketNearby(Location location, double radius, Object packet) {
		broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
	}

	public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, Object packet) {
		if (packet instanceof CommonPacket) {
			packet = ((CommonPacket) packet).getHandle();
		}
		CommonNMS.getPlayerList().sendPacketNearby(x, y, z, radius, WorldUtil.getDimension(world), (Packet) packet);
	}

	/**
	 * Obtains a collection of all plugins currently listening for the Packet type specified.
	 * Packets of this type can be expected to be handled by these plugins when sending it.
	 * 
	 * @param packetType to get the listening plugins for
	 * @return collection of listening plugins
	 */
	public static Collection<Plugin> getListenerPlugins(PacketType packetType) {
		return CommonPlugin.getInstance().getPacketHandler().getListening(packetType);
	}

	/**
	 * Gets the total amount of bytes of packet data that still has to be sent to the player
	 * 
	 * @param player to get the pending bytes of
	 * @return pending bytes
	 */
	public static long getPendingBytes(Player player) {
		return CommonPlugin.getInstance().getPacketHandler().getPendingBytes(player);
	}
}
