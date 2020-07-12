package com.bergerkiller.bukkit.common.internal;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_BaseImpl;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_9_to_1_15_2;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_16;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_1_8_to_1_8_8;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

/**
 * Does a whole lot of tracking to detect when entities are spawned and despawned for a Player.
 */
public class CommonVehicleMountManager {
    private final Map<Player, VehicleMountHandler_BaseImpl> _players = new IdentityHashMap<Player, VehicleMountHandler_BaseImpl>();
    private final PacketMonitor monitor;
    private final PacketListener listener;
    private final Task cleanupTask;
    private final Function<Player, VehicleMountHandler_BaseImpl> _handlerMaker;
    private final PacketType[] _listenedPackets;

    public CommonVehicleMountManager(CommonPlugin plugin) {
        // Runs occasionally to remove handlers for players that are no longer online
        this.cleanupTask = new Task(plugin) {
            @Override
            public void run() {
                cleanup();
            }
        };

        // Monitors when entities spawn/despawn or the player respawns
        this.monitor = new PacketMonitor() {
            @Override
            public void onMonitorPacketReceive(CommonPacket packet, Player player) {}

            @Override
            public void onMonitorPacketSend(CommonPacket packet, Player player) {
                get(player).handlePacketSend(packet);
            }
        };

        // Listens to packets and calls the handlers
        // These handlers can change the contents of the packet
        this.listener = new PacketListener() {
            @Override
            public void onPacketReceive(PacketReceiveEvent event) {
                get(event.getPlayer()).handlePacketReceive(event.getPacket());
            }

            @Override
            public void onPacketSend(PacketSendEvent event) {
                get(event.getPlayer()).handlePacketSend(event.getPacket());
            }
        };

        // Factory for creating handlers for players
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            this._handlerMaker = VehicleMountHandler_1_16::new;
            this._listenedPackets = VehicleMountHandler_1_16.LISTENED_PACKETS;
        } else if (VehicleMountHandler_BaseImpl.SUPPORTS_MULTIPLE_PASSENGERS) {
            this._handlerMaker = VehicleMountHandler_1_9_to_1_15_2::new;
            this._listenedPackets = VehicleMountHandler_1_9_to_1_15_2.LISTENED_PACKETS;
        } else {
            this._handlerMaker = VehicleMountHandler_1_8_to_1_8_8::new;
            this._listenedPackets = VehicleMountHandler_1_8_to_1_8_8.LISTENED_PACKETS;
        }
    }

    public void enable() {
        this.cleanupTask.start(100, 100);

        PacketUtil.addPacketMonitor(this.cleanupTask.getPlugin(), this.monitor,
                PacketType.OUT_ENTITY_SPAWN,
                PacketType.OUT_ENTITY_SPAWN_LIVING,
                PacketType.OUT_ENTITY_SPAWN_NAMED,
                PacketType.OUT_ENTITY_DESTROY,
                PacketType.OUT_RESPAWN);

        PacketUtil.addPacketListener(this.cleanupTask.getPlugin(), this.listener, this._listenedPackets);
    }

    public void disable() {
        this.cleanupTask.stop();
        PacketUtil.removePacketMonitor(this.monitor);
        PacketUtil.removePacketListener(this.listener);
    }

    public synchronized void cleanup() {
        Iterator<Player> iter = this._players.keySet().iterator();
        while (iter.hasNext()) {
            if (!iter.next().isOnline()) {
                iter.remove();
            }
        }
    }

    public synchronized void remove(Player player) {
        this._players.remove(player);
    }

    public synchronized VehicleMountHandler_BaseImpl get(Player player) {
        return this._players.computeIfAbsent(player, this._handlerMaker);
    }
}
