package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;

import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Base class for packet-related events
 */
public abstract class PacketEvent implements Cancellable {

    private boolean cancelled = false;
    private Player player;
    private CommonPacket packet;

    public PacketEvent(Player player, CommonPacket packet) {
        this.player = player;
        this.packet = packet;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    /**
     * Gets the type of packet that is being received/sent
     *
     * @return the Packet type
     */
    public PacketType getType() {
        return this.packet.getType();
    }

    /**
     * Get the player who is receiving/sending the packets from the server
     *
     * @return the Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the packet that is about to be received/sent
     *
     * @return the Packet
     */
    public CommonPacket getPacket() {
        return packet;
    }

    /**
     * Switches out the current packet with a new one. Can be used when immutable packets
     * need to be changed before being sent to the player / received by the server.
     *
     * @param packet New packet to swap it out with. Not null.
     */
    public void setPacket(CommonPacket packet) {
        if (packet == null) {
            throw new IllegalArgumentException("Cannot set null packet");
        }
        this.packet = packet;
    }

    /**
     * Switches out the current packet with a new one. Can be used when immutable packets
     * need to be changed before being sent to the player / received by the server.
     *
     * @param packet New packet to swap it out with. Not null.
     */
    public void setPacket(PacketHandle packet) {
        if (packet == null) {
            throw new IllegalArgumentException("Cannot set null packet");
        }
        this.packet = packet.toCommonPacket();
    }
}
