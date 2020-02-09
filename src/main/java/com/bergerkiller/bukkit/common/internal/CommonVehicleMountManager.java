package com.bergerkiller.bukkit.common.internal;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_BaseImpl;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_MultiplePassengers;
import com.bergerkiller.bukkit.common.internal.mounting.VehicleMountHandler_SinglePassenger;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutAttachEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMountHandle;

/**
 * Does a whole lot of tracking to detect when entities are spawned and despawned for a Player.
 */
public class CommonVehicleMountManager {
    private final Map<Player, VehicleMountHandler_BaseImpl> _players = new IdentityHashMap<Player, VehicleMountHandler_BaseImpl>();
    private final PacketMonitor monitor;
    private final PacketListener listener;
    private final Task cleanupTask;

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
                get(player).onPacketReceive(packet);
            }
        };

        // Listens for mount/attach packets, making corrections if needed
        if (VehicleMountHandler_BaseImpl.SUPPORTS_MULTIPLE_PASSENGERS) {
            // Listen for mount packet
            this.listener = new PacketListener() {
                @Override
                public void onPacketReceive(PacketReceiveEvent event) {
                }

                @Override
                public void onPacketSend(PacketSendEvent event) {
                    PacketPlayOutMountHandle packet = PacketPlayOutMountHandle.createHandle(event.getPacket().getHandle());
                    VehicleMountHandler_MultiplePassengers handler = (VehicleMountHandler_MultiplePassengers) get(event.getPlayer());
                    handler.processMountPacket(packet);
                }
            };
        } else {
            // Listen for attach packet (when leash is false)
            this.listener = new PacketListener() {
                @Override
                public void onPacketReceive(PacketReceiveEvent event) {
                }

                @Override
                public void onPacketSend(PacketSendEvent event) {
                    PacketPlayOutAttachEntityHandle packet = PacketPlayOutAttachEntityHandle.createHandle(event.getPacket().getHandle());
                    if (!packet.isLeash()) {
                        VehicleMountHandler_SinglePassenger handler = (VehicleMountHandler_SinglePassenger) get(event.getPlayer());
                        handler.processAttachEntityPacket(packet);
                    }
                }
            };
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

        if (VehicleMountHandler_BaseImpl.SUPPORTS_MULTIPLE_PASSENGERS) {
            PacketUtil.addPacketListener(this.cleanupTask.getPlugin(), this.listener, PacketType.OUT_MOUNT);
        } else {
            PacketUtil.addPacketListener(this.cleanupTask.getPlugin(), this.listener, PacketType.OUT_ENTITY_ATTACH);
        }
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
        VehicleMountHandler_BaseImpl handler = this._players.get(player);
        if (handler == null) {
            if (VehicleMountHandler_BaseImpl.SUPPORTS_MULTIPLE_PASSENGERS) {
                handler = new VehicleMountHandler_MultiplePassengers(player);
            } else {
                handler = new VehicleMountHandler_SinglePassenger(player);
            }
            this._players.put(player, handler);
        }
        return handler;
    }
}
