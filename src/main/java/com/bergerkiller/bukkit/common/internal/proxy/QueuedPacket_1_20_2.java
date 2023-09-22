package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.generated.net.minecraft.network.NetworkManagerHandle;

import java.util.function.Consumer;

/**
 * Used on Minecraft 1.20.2+ when queuing a packet. This is used
 * when the default common packet handler is used and a packet needs to be
 * sent while handling a packet being listened/monitored.
 */
public class QueuedPacket_1_20_2 implements Consumer<Object> {
    private final Object packet;
    private final Object packetsendlistener;
    private final boolean flush;

    public QueuedPacket_1_20_2(Object packet, Object packetsendlistener, boolean flush) {
        this.packet = packet;
        this.packetsendlistener = packetsendlistener;
        this.flush = flush;
    }

    @Override
    public void accept(Object networkmanager) {
        NetworkManagerHandle.T.queue_sendPacketImpl.raw.invoke(networkmanager, packet, packetsendlistener, flush);
    }
}
