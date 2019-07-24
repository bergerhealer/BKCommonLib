package com.bergerkiller.bukkit.common.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;

/**
 * Manages 'forced chunk' logic in a way that allows multiple owners
 * with a reference counter.
 */
public class CommonForcedChunkManager extends ForcedChunkManager {
    private final Map<ChunkKey, Entry> chunks = new HashMap<ChunkKey, Entry>();
    private final Set<ChunkKey> pending = new HashSet<ChunkKey>();
    private final ChunkUnloadEventListener chunkUnloadListener = new ChunkUnloadEventListener();
    private final CommonPlugin plugin;
    private Task pendingHandler = null;

    public CommonForcedChunkManager(CommonPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized void enable() {
        this.pendingHandler = new Task(this.plugin) {
            @Override
            public void run() {
                synchronized (CommonForcedChunkManager.this) {
                    synchronized (pending) {
                        for (ChunkKey key : pending) {
                            Entry entry = chunks.get(key);
                            if (entry != null) {
                                boolean forced = entry.isForced();
                                refreshChunk(entry, forced);
                                if (!forced) {
                                    chunks.remove(key);
                                }
                            }
                        }
                        pending.clear();
                    }
                }
            }
        };
        if (CommonCapabilities.CAN_CANCEL_CHUNK_UNLOAD_EVENT) {
            plugin.register(this.chunkUnloadListener);
        }
    }

    public synchronized void disable(CommonPlugin plugin) {
        this.pendingHandler.stop();
        this.pendingHandler = null;
        this.pending.clear();
        for (Entry entry : this.chunks.values()) {
            entry.disable();
        }
        this.chunks.clear();
    }

    public synchronized int getNumberOfForcedLoadedChunks() {
        return chunks.size();
    }

    public synchronized boolean isForced(Chunk chunk) {
        return chunks.containsKey(new ChunkKey(chunk.getWorld(), chunk.getX(), chunk.getZ()));
    }

    protected void setForced(Entry entry, boolean forced) {
        if (CommonUtil.isMainThread()) {
            // Verify forced while synchronized around this.
            // Remove the entry when indeed, the chunk is no longer forced
            if (!forced) {
                synchronized (this) {
                    if (entry.isForced()) {
                        return;
                    }
                    chunks.remove(entry.getKey());
                }
            }

            // Refresh
            refreshChunk(entry, forced);
        } else {
            // Refresh 'forced' state later in the future
            synchronized (pending) {
                if (pending.isEmpty()) {
                    pendingHandler.start();
                }
                pending.add(entry.getKey());
            }
        }
    }

    @Override
    public synchronized ForcedChunkEntry add(World world, int chunkX, int chunkZ) {
        ChunkKey key = new ChunkKey(world, chunkX, chunkZ);
        Entry entry = this.chunks.get(key);
        if (entry == null) {
            entry = new Entry(key);
            this.chunks.put(key, entry);
        }
        entry.add();
        return entry;
    }

    /**
     * Main method that interfaces with Bukkit. Can only be run
     * on the main thread.
     * 
     * @param entry
     * @param forced
     */
    private void refreshChunk(Entry entry, boolean forced) {
        ChunkKey chunk = entry.getKey();

        // This performs chunk loading/unloading automatically using 'tickets' in NMS ChunkMapDistance
        // This method is available on 1.13.1+
        // The ChunkUnloadEvent is not used for this, then
        if (WorldServerHandle.T.setForceLoadedAsync.isAvailable()) {
            WorldServerHandle.T.setForceLoadedAsync.invoke(
                    HandleConversion.toWorldHandle(chunk.world),
                    Integer.valueOf(chunk.chunkX),
                    Integer.valueOf(chunk.chunkZ),
                    this.plugin,
                    Boolean.valueOf(forced)
            );
        }

        // Load/unload the chunk
        if (forced) {
            // Request the chunk to be loaded asynchronously
            WorldUtil.getChunkAsync(chunk.world, chunk.chunkX, chunk.chunkZ).thenAccept(entry);
        } else {
            // Trigger the server to unload the chunk. It will fire a single
            // ChunkUnloadEvent (which we will handle) to make sure the chunk unloads.
            chunk.world.unloadChunkRequest(chunk.chunkX, chunk.chunkZ);
            entry.resetAsyncLoad();
        }
    }

    private final class Entry implements ForcedChunkEntry, Consumer<Chunk> {
        private final ChunkKey key;
        private final AtomicInteger counter;
        private CompletableFuture<Chunk> chunkFuture;

        public Entry(ChunkKey key) {
            this.key = key;
            this.counter = new AtomicInteger();
            this.resetAsyncLoad();
        }

        public void resetAsyncLoad() {
            this.chunkFuture = new CompletableFuture<Chunk>();
        }
        
        public boolean isForced() {
            return this.counter.get() > 0;
        }

        public void disable() {
            if (this.counter.getAndSet(0) > 0) {
                setForced(this, false);
            }
        }

        @Override
        public void add() {
            if (this.counter.incrementAndGet() == 1) {
                setForced(this, true);
            }
        }

        @Override
        public void remove() {
            if (this.counter.decrementAndGet() == 0) {
                setForced(this, false);
            }
        }

        public ChunkKey getKey() {
            return this.key;
        }

        @Override
        public World getWorld() {
            return this.key.world;
        }

        @Override
        public int getX() {
            return this.key.chunkX;
        }

        @Override
        public int getZ() {
            return this.key.chunkZ;
        }

        @Override
        public CompletableFuture<Chunk> getChunkAsync() {
            return this.chunkFuture;
        }

        @Override
        public void accept(Chunk chunk) {
            this.chunkFuture.complete(chunk);
        }
    }

    private static final class ChunkKey {
        public final World world;
        public final int chunkX;
        public final int chunkZ;

        public ChunkKey(World world, int chunkX, int chunkZ) {
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public boolean equals(Object o) {
            ChunkKey other = (ChunkKey) o;
            return other.world == world &&
                   other.chunkX == chunkX &&
                   other.chunkZ == chunkZ;
        }

        @Override
        public int hashCode() {
            return chunkX * 31 + chunkZ;
        }

        @Override
        public String toString() {
            return "Chunk{world=" + this.world.getName() + ",x=" + this.chunkX + ",z=" + this.chunkZ + "}";
        }
    }

    // Used on MC 1.13.2 and before, where the event could still be cancelled
    private class ChunkUnloadEventListener implements Listener {

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onChunkUnload(ChunkUnloadEvent event) {
            if (isForced(event.getChunk())) {
                ((Cancellable) event).setCancelled(true);
            }
        }
    }
}
