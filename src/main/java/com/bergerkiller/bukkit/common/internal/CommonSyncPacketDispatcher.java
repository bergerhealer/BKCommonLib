package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.LockSupport;

import com.bergerkiller.generated.net.minecraft.network.protocol.common.ClientboundKeepAlivePacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.common.ServerboundKeepAlivePacketHandle;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.AsyncTask;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

/**
 * Sends packets to clients asynchronously, synchronized to that client's tick
 * "phase". The phase depends on the client performance compared to that
 * of the server. The dispatcher attempts to send packets at the start of the client's
 * tick, right before other packets are received.<br>
 * <br>
 * <b>UNUSED: This feature did not work as well as we hoped it would. Here for archival purposes</b>
 */
@Deprecated
public class CommonSyncPacketDispatcher extends AsyncTask implements PacketListener {
    private static final long TICK_SPAN_NANOS = 50000000L;
    private static final PacketType[] MONITORED_PACKET_TYPES = new PacketType[] {
            PacketType.IN_KEEP_ALIVE, PacketType.IN_POSITION, PacketType.IN_LOOK, PacketType.IN_POSITION_LOOK
    };

    private final Map<Player, State> _states = new IdentityHashMap<Player, State>();
    private final ArrayList<State> _states_list = new ArrayList<State>();
    private Task _keepAliveTask;
    private CommonPlugin _plugin;

    private synchronized State getState(Player player) {
        return _states.computeIfAbsent(player, State::new);
    }

    /**
     * Queues a packet for sending in the very next client tick. If sent on the main thread,
     * all packets sent during the current tick will be sent in a single send operation to the client.
     * 
     * @param player Player to send the packet to
     * @param packet Packet to send
     */
    public void sendPacket(Player player, CommonPacket packet) {
        getState(player).addPacket(new QueuedPacket(packet.getType(), packet.getHandle()));
    }

    /**
     * Queues a packet for sending in the very next client tick. If sent on the main thread,
     * all packets sent during the current tick will be sent in a single send operation to the client.
     * 
     * @param player Player to send the packet to
     * @param packet Packet to send
     */
    public void sendPacket(Player player, PacketHandle packet) {
        getState(player).addPacket(new QueuedPacket(packet.getPacketType(), packet.getRaw()));
    }

    @Override
    public synchronized void onPacketReceive(PacketReceiveEvent event) {
        State s = _states.get(event.getPlayer());
        if (s == null) {
            return;
        }

        if (event.getType() == PacketType.IN_KEEP_ALIVE) {
            // Latency measurement
            if (!s.syncTimeSentActive) {
                return;
            }
            if (s.syncTimeSent != ServerboundKeepAlivePacketHandle.createHandle(event.getPacket().getHandle()).getKey()) {
                return;
            }
            s.syncTimeSentActive = false;
            s.latency = (getTime() - s.syncTimeSent) >> 1;
            event.setCancelled(true);
        } else {
            // Phase measurement
            long new_phase = Math.floorMod(System.nanoTime() - 10000000L - s.latency, TICK_SPAN_NANOS);
            if (s.hasSync) {
                long diff = new_phase - s.syncTime;
                if (diff < -(TICK_SPAN_NANOS/2)) {
                    diff += TICK_SPAN_NANOS;
                } else if (diff > (TICK_SPAN_NANOS/2)) {
                    diff -= TICK_SPAN_NANOS;
                }
                s.syncTime += diff / 10;
            } else {
                s.hasSync = true;
                s.syncTime = new_phase;
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
    }

    public void enable(CommonPlugin plugin) {
        _plugin = plugin;
        plugin.getPacketHandler().addPacketListener(plugin, this, MONITORED_PACKET_TYPES);
        this.start(true);
        _keepAliveTask = new Task(plugin) {
            @Override
            public void run() {
                Invoker<?> createKeepAlive = ((Template.StaticMethod<?>) ClientboundKeepAlivePacketHandle.T.createNew.raw).invoker;
                synchronized (CommonSyncPacketDispatcher.this) {
                    for (State s : _states.values()) {
                        s.syncTimeSent = getTime();
                        s.syncTimeSentActive = true;
                        s.sendPacket(PacketType.OUT_KEEP_ALIVE, createKeepAlive.invoke(null, Long.valueOf(s.syncTimeSent)));
                    }
                }
            }
        }.start(0, 20);
    }

    public void disable(CommonPlugin plugin) {
        plugin.getPacketHandler().removePacketListener(this);
        this.stop();
        this.waitFinished();
        Task.stop(_keepAliveTask);
        _keepAliveTask = null;
        _plugin = null;
    }

    @Override
    public void run() {
        // Send a keep-alive packet to all clients every so often
        synchronized (this) {
            _states_list.clear();

            Iterator<State> iter = _states.values().iterator();
            while (iter.hasNext()) {
                State s = iter.next();
                if (s.isValid()) {
                    _states_list.add(s);
                } else {
                    iter.remove();
                }
            }
        }

        while (!_states_list.isEmpty()) {
            long now = System.nanoTime();
            State next = null;
            long nanosUntilSync = TICK_SPAN_NANOS;
            int nextIndex = 0;
            if (_states_list.size() == 1) {
                next = _states_list.get(0);
                nanosUntilSync = next.getNanosUntilSync(now);
            } else {
                Iterator<State> iter = _states_list.iterator();
                int i = 0;
                do {
                    State s = iter.next();
                    long n = s.getNanosUntilSync(now);
                    if (n <= nanosUntilSync) {
                        nanosUntilSync = n;
                        next = s;
                        nextIndex = i;
                    }
                    i++;
                } while (iter.hasNext());
            }

            _states_list.remove(nextIndex);

            // Wait for nanos
            long remaining = nanosUntilSync;
            do {
                LockSupport.parkNanos(remaining);
                remaining = nanosUntilSync - (System.nanoTime() - now);
            } while (remaining > 0);

            // Send all packets
            synchronized (next) {
                next.unsafe_sync();
                for (QueuedPacket packet : next.packets) {
                    next.sendPacket(packet.type, packet.packet);
                }
                next.packets.clear();
            }
        }
    }

    private static int getTime() {
        return (int) System.nanoTime();
    }

    private final class State {
        public final Player player;
        public final Queue<QueuedPacket> packets;
        public final Queue<QueuedPacket> packets_sync;
        public boolean syncTimeSentActive = false;
        public int syncTimeSent = 0;
        public long syncTime;
        public boolean hasSync = false;
        public int latency = 0;
        public volatile int tick;
        public long lastSendPacketTime;

        public long getNanosUntilSync(long nanos_now) {
            long nanos = Math.floorMod(nanos_now - syncTime, TICK_SPAN_NANOS);
            return TICK_SPAN_NANOS - nanos;
        }

        public State(Player player) {
            this.player= player;
            this.syncTime = getTime();
            this.packets = new LinkedList<QueuedPacket>();
            this.packets_sync = new LinkedList<QueuedPacket>();
            this.tick = CommonUtil.getServerTicks();
            this.lastSendPacketTime = System.currentTimeMillis();
        }

        public void unsafe_sync() {
            int curr_tick = CommonUtil.getServerTicks();
            if (this.tick != curr_tick) {
                this.tick = curr_tick;
                this.packets.addAll(this.packets_sync);
                this.packets_sync.clear();
            }
        }

        public synchronized void addPacket(QueuedPacket packet) {
            unsafe_sync();
            this.packets_sync.add(packet);
            this.lastSendPacketTime = System.currentTimeMillis();
        }

        public void sendPacket(PacketType type, Object packet) {
            _plugin.getPacketHandler().sendPacket(this.player, type, packet, false);
        }

        public boolean isValid() {
            if (!this.player.isOnline()) {
                return false;
            }
            if ((System.currentTimeMillis() - this.lastSendPacketTime) > 60000) {
                return false;
            }
            return true;
        }
    }

    private static final class QueuedPacket {
        public final PacketType type;
        public final Object packet;

        public QueuedPacket(PacketType type, Object packet) {
            this.type = type;
            this.packet = packet;
        }
    }
}
