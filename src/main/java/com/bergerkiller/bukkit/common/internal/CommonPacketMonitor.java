package com.bergerkiller.bukkit.common.internal;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * Packet listener of BKCommonLib to keep track of send chunks.
 * This is used to keep the 'chunks a player can see' up-to-date.
 */
class CommonPacketMonitor implements PacketMonitor {
	public static final PacketType[] TYPES = {PacketType.MAP_CHUNK, PacketType.MAP_CHUNK_BULK, PacketType.VEHICLE_SPAWN, PacketType.ATTACH_ENTITY, PacketType.DESTROY_ENTITY};

	@Override
	public void onMonitorPacketReceive(CommonPacket packet, Player player) {
	}

	@Override
	public void onMonitorPacketSend(CommonPacket packet, Player player) {
		// Keep track of chunk loading and unloading at clients
		if (packet.getType() == PacketType.MAP_CHUNK) {
			// Update it for a single chunk
			boolean visible = packet.read(PacketFields.MAP_CHUNK.chunkDataBitMap) != 0;
			int chunkX = packet.read(PacketFields.MAP_CHUNK.x);
			int chunkZ = packet.read(PacketFields.MAP_CHUNK.z);
			CommonPlugin.getInstance().setChunkVisible(player, chunkX, chunkZ, visible);
		} else if (packet.getType() == PacketType.MAP_CHUNK_BULK) {
			// Update it for multiple chunks at once
			// This type of packet only makes new chunks visible - it never unloads
			int[] chunkX = packet.read(PacketFields.MAP_CHUNK_BULK.bulk_x);
			int[] chunkZ = packet.read(PacketFields.MAP_CHUNK_BULK.bulk_z);
			CommonPlugin.getInstance().setChunksAsVisible(player, chunkX, chunkZ);
		} else {
			System.out.println(packet);
			Thread.dumpStack();
		}
	}
}
