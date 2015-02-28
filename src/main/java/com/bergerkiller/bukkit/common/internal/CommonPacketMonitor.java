package com.bergerkiller.bukkit.common.internal;

import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * Packet listener of BKCommonLib to keep track of send chunks. This is used to
 * keep the 'chunks a player can see' up-to-date.
 */
class CommonPacketMonitor implements PacketMonitor {

    public static final PacketType[] TYPES = {PacketType.OUT_MAP_CHUNK, PacketType.OUT_MAP_CHUNK_BULK, PacketType.OUT_RESPAWN};
    private boolean listenError = false;

    @Override
    public void onMonitorPacketReceive(CommonPacket packet, Player player) {
    }

    @Override
    public void onMonitorPacketSend(CommonPacket packet, Player player) {
        // Keep track of chunk loading and unloading at clients
        CommonPlayerMeta meta = CommonPlugin.getInstance().getPlayerMeta(player);
        if (packet.getType() == PacketType.OUT_MAP_CHUNK) {
            // Update it for a single chunk
            boolean visible = packet.read(PacketType.OUT_MAP_CHUNK.chunkDataBitMap) != null;
            int chunkX = packet.read(PacketType.OUT_MAP_CHUNK.x);
            int chunkZ = packet.read(PacketType.OUT_MAP_CHUNK.z);
            meta.setChunkVisible(chunkX, chunkZ, visible);
        } else if (packet.getType() == PacketType.OUT_MAP_CHUNK_BULK) {
			// Update it for multiple chunks at once
            // This type of packet only makes new chunks visible - it never unloads
            int[] chunkX = packet.read(PacketType.OUT_MAP_CHUNK_BULK.bulk_x);
            int[] chunkZ = packet.read(PacketType.OUT_MAP_CHUNK_BULK.bulk_z);
            meta.setChunksAsVisible(chunkX, chunkZ);
        } else if (packet.getType() == PacketType.OUT_RESPAWN) {
            // Clear all known chunks
            meta.clearVisibleChunks();
        } else if (!listenError) {
            listenError = true;
            CommonPlugin.LOGGER_NETWORK.log(Level.WARNING, "Packet entered listener that the listener was not registered for: type=" + packet.getType());
        }
    }
}
