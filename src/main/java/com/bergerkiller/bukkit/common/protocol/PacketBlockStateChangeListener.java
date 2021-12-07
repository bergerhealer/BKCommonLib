package com.bergerkiller.bukkit.common.protocol;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.logic.BlockStateChangePacketHandler;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;

/**
 * Packet listener that allows for listening for block state changes
 * sent to players. Handles the various different protocol changes in
 * different versions of Minecraft automatically.
 *
 * For Minecraft 1.9.2 and before, only the UpdateSign packet is supported,
 * so only sign updates can be listened to. Minecraft versions 1.9.4 and later
 * allow for listening to all types of block state changes.
 */
public interface PacketBlockStateChangeListener extends PacketListener {
    public static PacketType[] LISTENED_TYPES = BlockStateChangePacketHandler.INSTANCE.listenedTypes();

    /**
     * Callback method called for every packet
     * 
     * @param player Player to which the block state change is sent
     * @param blockChange Details about the state change
     * @return True if the block state change should be allowed, False to omit
     *         the block state change and leave it unchanged (or uninitialized)
     *         for the client.
     */
    abstract boolean onBlockChange(Player player, BlockStateChange blockChange);

    @Override
    default void onPacketReceive(PacketReceiveEvent event) {
        // No-op
    }

    @Override
    default void onPacketSend(PacketSendEvent event) {
        if (!BlockStateChangePacketHandler.INSTANCE.process(event.getPlayer(), event.getPacket(), this)) {
            event.setCancelled(true);
        }
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
    public static boolean process(Player player, CommonPacket packet, PacketBlockStateChangeListener listener) {
        return BlockStateChangePacketHandler.INSTANCE.process(player, packet, listener);
    }
}
