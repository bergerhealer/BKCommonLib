package com.bergerkiller.bukkit.common.internal.logic;

import java.util.IdentityHashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketBlockStateChangeListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * Detects and processes the block state changes contained within
 * various types of packets. This includes the TileEntityChange packet,
 * map chunk packets or the 1.18 variants of these.
 *
 * The UpdateSign packet of Minecraft 1.9.2 and before is also supported.
 */
public abstract class BlockStateChangePacketHandler implements LibraryComponent {
    public static final BlockStateChangePacketHandler INSTANCE = LibraryComponentSelector.forModule(BlockStateChangePacketHandler.class)
            .addVersionOption(null, "1.9.2", BlockStateChangePacketHandler_1_8_to_1_9_2::new)
            .addVersionOption("1.9.3", null, BlockStateChangePacketHandler_1_9_3::new)
            .update();

    private final Handler _noopHandler = (player, packet, listener) -> true;
    private final Map<PacketType, Handler> _handlers = new IdentityHashMap<>();

    @Override
    public void disable() {
    }

    /**
     * Gets the packet types that should be listened to, for proper functionality
     *
     * @return listened packet types
     */
    public final PacketType[] listenedTypes() {
        return _handlers.keySet().stream().toArray(PacketType[]::new);
    }

    /**
     * Processes all the Block States contained within a packet, if any.
     * Does nothing if the packet does not change block states.
     *
     * @param player Player to which a packet was sent
     * @param packet Packet to process
     * @param listener Listener whose callback to call for every contained BlockState
     *                 inside the packet, if any.
     * @return True if the packet should be sent to the Player (not cancelled by the listener),
     *         False if the packet should not be sent. (cancelled)
     */
    public final boolean process(Player player, CommonPacket packet, PacketBlockStateChangeListener listener) {
        return _handlers.getOrDefault(packet.getType(), _noopHandler).handle(player, packet, listener);
    }

    protected void register(PacketType packetType, Handler handler) {
        _handlers.put(packetType, handler);
    }

    @FunctionalInterface
    protected static interface Handler {
        boolean handle(Player player, CommonPacket packet, PacketBlockStateChangeListener listener);
    }
}
