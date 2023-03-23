package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundBundlePacketHandle;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Used by the ProtocolLib handler on 1.19.4 and beyond. Listens for bundle packets,
 * and processes them using all listeners/monitors that have been registered.
 */
class ProtocolLibBundlePacketHandler extends PacketHandlerRegistration {

    private final ProtocolLibPacketHandler protocolLibHandler;
    private final Plugin plugin;
    private final Handler listenerHandler;
    private final Handler monitorHandler;

    public ProtocolLibBundlePacketHandler(ProtocolLibPacketHandler protocolLibHandler, Plugin plugin) {
        this.protocolLibHandler = protocolLibHandler;
        this.plugin = plugin;
        this.listenerHandler = new Handler(ListenerPriority.LOWEST) {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {
                // Filter Bundle packets (if not already cancelled. Too complex to solve that.)
                if (!packetEvent.isPlayerTemporary() && !packetEvent.isCancelled()) {
                    ClientboundBundlePacketHandle bundle = ClientboundBundlePacketHandle.createHandle(packetEvent.getPacket().getHandle());
                    final Player player = packetEvent.getPlayer();
                    if (!bundle.filterSubPackets(packet -> handleSendBundlePacket(packetEvent, player, packet))) {
                        packetEvent.setCancelled(true);
                    }
                }
            }
        };
        this.monitorHandler = new Handler(ListenerPriority.MONITOR) {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {
                // Notify all packets contained within bundle packets
                if (!packetEvent.isPlayerTemporary()) {
                    Iterable<Object> subPackets = PacketHandle.tryUnwrapBundlePacket(packetEvent.getPacket().getHandle());
                    for (Object raw_packet : subPackets) {
                        handlePacketSendMonitor(packetEvent.getPlayer(), PacketType.getType(raw_packet), raw_packet);
                    }
                }
            }
        };
    }

    public void register() {
        ProtocolLibPacketHandler.getProtocolManager().addPacketListener(listenerHandler);
        ProtocolLibPacketHandler.getProtocolManager().addPacketListener(monitorHandler);
    }

    public void unregister() {
        ProtocolLibPacketHandler.getProtocolManager().removePacketListener(listenerHandler);
        ProtocolLibPacketHandler.getProtocolManager().removePacketListener(monitorHandler);
    }

    private boolean handleSendBundlePacket(PacketEvent event, Player player, Object raw_packet) {
        ClientboundBundlePacketHandle bundle = ClientboundBundlePacketHandle.createHandle(raw_packet);
        PacketType packetType = PacketType.OUT_BUNDLE;

        return bundle.filterSubPackets(p -> protocolLibHandler.handleSendDuring(event, () ->
                handlePacketSend(player, packetType, p, false, false)));
    }

    private abstract class Handler implements com.comphenix.protocol.events.PacketListener {
        private final ListeningWhitelist receiving;
        private final ListeningWhitelist sending;

        private Handler(ListenerPriority priority) {
            PacketType[] types = new PacketType[] { PacketType.OUT_BUNDLE };
            this.receiving = ProtocolLibPacketHandler.getWhiteList(priority, new PacketType[0], true);
            this.sending = ProtocolLibPacketHandler.getWhiteList(priority, types, false);
        }

        @Override
        public Plugin getPlugin() {
            return plugin;
        }

        @Override
        public ListeningWhitelist getReceivingWhitelist() {
            return receiving;
        }

        @Override
        public ListeningWhitelist getSendingWhitelist() {
            return sending;
        }

        @Override
        public void onPacketReceiving(PacketEvent packetEvent) {
            // Client doesn't send bundles
        }
    }
}
