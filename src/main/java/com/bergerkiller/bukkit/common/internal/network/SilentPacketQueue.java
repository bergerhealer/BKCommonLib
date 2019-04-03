package com.bergerkiller.bukkit.common.internal.network;

import java.util.LinkedList;
import java.util.ListIterator;

import org.bukkit.entity.Player;

/**
 * Temporarily stores packets that are not supposed to be handled by
 * listeners. When wanting to send a packet silently, it is added to this queue.
 * When the packet is later encountered again handling a packet send event,
 * the event can be properly ignored, and the packet is removed from the queue.
 * If the event never occurs, a timeout will make sure the packet is eventually
 * cleaned up again.
 * This class is thread-safe.
 */
public class SilentPacketQueue {
    private final LinkedList<SilentPacket> _queue = new LinkedList<SilentPacket>();

    /**
     * Adds a packet, sent to a player, to the queue
     * 
     * @param player
     * @param packet
     */
    public synchronized void add(Player player, Object packet) {
        this._queue.add(new SilentPacket(player, packet));
    }

    /**
     * Takes a queued packet, sent to a player, from the queue.
     * Also cleans up old packets that have timed out.
     * Returns true if the player-packet combination was contained.
     * 
     * @param player
     * @param packet
     * @return True if taken
     */
    public synchronized boolean take(Player player, Object packet) {
        if (this._queue.isEmpty()) {
            return false;
        }

        long time = System.currentTimeMillis();
        ListIterator<SilentPacket> iter = this._queue.listIterator();
        while (iter.hasNext()) {
            SilentPacket sp = iter.next();
            if (time >= sp.timeout) {
                iter.remove();
            } else if (sp.player == player && sp.packet == packet) {
                iter.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Removes queued packets that have timed out.
     * Returns true once the queue is empty.
     * 
     * @return True if the queue is empty
     */
    public synchronized boolean cleanup() {
        // Already empty
        if (this._queue.isEmpty()) {
            return true;
        }

        // Cleanup
        long time = System.currentTimeMillis();
        ListIterator<SilentPacket> iter = this._queue.listIterator();
        while (iter.hasNext()) {
            SilentPacket sp = iter.next();
            if (time >= sp.timeout) {
                iter.remove();
            }
        }
        return this._queue.isEmpty();
    }

    private static class SilentPacket {
        public final Player player;
        public final Object packet;
        public final long timeout;

        public SilentPacket(Player player, Object packet) {
            this.player = player;
            this.packet = packet;
            this.timeout = System.currentTimeMillis() + 5000; // remove after 5s
        }
    }
}
