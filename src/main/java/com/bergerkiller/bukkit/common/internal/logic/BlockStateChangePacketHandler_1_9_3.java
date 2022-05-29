package com.bergerkiller.bukkit.common.internal.logic;

import java.util.Iterator;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapChunkHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutTileEntityDataHandle;

/**
 * Listens for the PacketPlayOutTileEntityData and PacketPlayOutMapChunk
 * packets and processes the tile entities stored inside. On Minecraft 1.18
 * and later, the map chunk packet is remapped to an alternative packet,
 * but the behavior is the same.
 */
class BlockStateChangePacketHandler_1_9_3 extends BlockStateChangePacketHandler {

    @Override
    public void enable() {
        register(PacketType.OUT_TILE_ENTITY_DATA, (player, commonPacket, listener) -> {
            final PacketPlayOutTileEntityDataHandle packet = PacketPlayOutTileEntityDataHandle.createHandle(commonPacket.getHandle());

            // Parse BlockState type. If unknown/invalid, ignore the packet.
            BlockStateType tileType = packet.getType();
            if (tileType == null) {
                return true;
            }

            CommonTagCompound metadata = packet.getData();
            if (metadata != null) {
                // There's metadata, nothing special needs to be done
                BlockStateChange change = BlockStateChange.deferred(packet.getPosition(), tileType,
                        LogicUtil.constantSupplier(metadata), () -> true);

                // Handle it, if false, cancel the packet entirely
                if (!listener.onBlockChange(player, change)) {
                    return false;
                }
            } else {
                // Initialize metadata on first use
                final DeferredSupplier<CommonTagCompound> metadataSupplier = DeferredSupplier.of(CommonTagCompound::new);
                BlockStateChange change = BlockStateChange.deferred(packet.getPosition(), tileType,
                        metadataSupplier, metadataSupplier::isInitialized);

                // Handle it, if false, cancel the packet entirely
                if (!listener.onBlockChange(player, change)) {
                    return false;
                }

                // If metadata was created and it's not empty, apply it to the packet
                if (metadataSupplier.isInitialized() && !metadataSupplier.get().isEmpty()) {
                    packet.setData(metadataSupplier.get());
                }
            }

            return true;
        });
        register(PacketType.OUT_MAP_CHUNK, (player, commonPacket, listener) -> {
            PacketPlayOutMapChunkHandle packet = PacketPlayOutMapChunkHandle.createHandle(commonPacket.getHandle());

            Iterator<BlockStateChange> iter = packet.getBlockStates().iterator();
            while (iter.hasNext()) {
                BlockStateChange change = iter.next();
                if (!listener.onBlockChange(player, change)) {
                    iter.remove();
                }
            }
            return true;
        });
    }
}
