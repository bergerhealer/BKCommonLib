package com.bergerkiller.bukkit.common.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager;
import com.bergerkiller.bukkit.common.collections.RunnableConsumer;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
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
    private SyncChunkLoader syncChunkLoader = null;

    // After this number of ticks, force-load the chunk and don't wait for asynchronous loading to finish
    private static final int SYNC_LOAD_AFTER_TICKS = 100;

    public CommonForcedChunkManager(CommonPlugin plugin) {
        this.plugin = plugin;
    }

    public synchronized void enable() {
        this.syncChunkLoader = new SyncChunkLoader(this.plugin);
        this.syncChunkLoader.start(1, 1);
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
        this.syncChunkLoader.stop();
        this.syncChunkLoader = null;
        this.pendingHandler.stop();
        this.pendingHandler = null;
        this.pending.clear();
        while (!this.chunks.isEmpty()) {
            for (Entry e : this.chunks.values().toArray(new Entry[0])) {
                e.disable();
            }
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
            // The syncChunkLoader will sync-load the chunk after a tick delay
            syncChunkLoader.add(entry);
            entry.startLoadingAsync();
        } else {
            // Trigger the server to unload the chunk. It will fire a single
            // ChunkUnloadEvent (which we will handle) to make sure the chunk unloads.
            chunk.world.unloadChunkRequest(chunk.chunkX, chunk.chunkZ);
            entry.resetAsyncLoad();
        }
    }

    private final class Entry implements ForcedChunkEntry, Consumer<Object> {
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
        public Chunk getChunk() {
            if (this.chunkFuture.isDone()) {
                try {
                    return this.chunkFuture.get();
                } catch (Throwable t) {}
            }
            Chunk chunk = this.key.getChunk();
            this.chunkFuture.complete(chunk);
            return chunk;
        }

        @Override
        public CompletableFuture<Chunk> getChunkAsync() {
            return this.chunkFuture;
        }

        @Override
        public void accept(Object chunk) {
            // Either -> Left
            chunk = RunnableConsumer.unpack(chunk);

            // If successful, complete the async chunk loading future
            if (chunk != null) {
                this.chunkFuture.complete(WrapperConversion.toChunk(chunk));
                return;
            }

            // If not successful, and this entry is still forced loaded, try again
            if (this.isForced()) {
                startLoadingAsync();
            }
        }

        /**
         * Asks the chunk loading scheduler to start loading this chunk asynchronously.
         * Once done, the accept callback method is called with the loaded chunk.
         */
        public void startLoadingAsync() {
            // If future already resolved, ignore
            if (this.chunkFuture.isDone()) {
                return;
            }

            // If already loaded, complete it right away!
            WorldServerHandle worldHandle = WorldServerHandle.fromBukkit(this.key.world);
            {
                org.bukkit.Chunk loadedChunk = worldHandle.getChunkIfLoaded(this.key.chunkX, this.key.chunkZ);
                if (loadedChunk != null) {
                    this.chunkFuture.complete(loadedChunk);
                    return;
                }
            }

            // This consumer is called from internal when the chunk is ready
            // Null is returned when loading fails
            final ChunkProviderServerHandle cps_handle = worldHandle.getChunkProviderServer();
            final Executor executor = cps_handle.getAsyncExecutor();
            if (executor == null) {
                cps_handle.getChunkAtAsync(this.key.chunkX, this.key.chunkZ, this);
            } else {
                CompletableFuture.runAsync(() -> cps_handle.getChunkAtAsync(this.key.chunkX, this.key.chunkZ, Entry.this), executor);
            }
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

        public Chunk getChunk() {
            return world.getChunkAt(chunkX, chunkZ);
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

    // Force-loads every chunk kept loading after a 30-ish tick timeout
    // Each entry stores the chunks to load for that tick
    private static class SyncChunkLoadTask {
        public final int tick;
        private final LinkedList<Entry> entries = new LinkedList<Entry>();

        public SyncChunkLoadTask(int ticks) {
            this.tick = ticks;
        }

        public void add(Entry entry) {
            this.entries.add(entry);
        }

        public void load() {
            for (Entry e : entries) {
                if (e.isForced()) {
                    e.getChunk();
                }
            }
        }
    }

    // Main task responsible for loading chunks after a tick delay
    private static class SyncChunkLoader extends Task {
        private final LinkedList<SyncChunkLoadTask> tasks = new LinkedList<SyncChunkLoadTask>();
        private int currentTick = 0;

        public SyncChunkLoader(JavaPlugin plugin) {
            super(plugin);
        }

        public void add(Entry entry) {
            SyncChunkLoadTask task;
            if (tasks.isEmpty() || (task = tasks.peekLast()).tick != currentTick) {
                task = new SyncChunkLoadTask(currentTick);
                tasks.addLast(task);
            }
            task.add(entry);
        }

        @Override
        public void run() {
            currentTick++;
            if (!tasks.isEmpty() && (currentTick-SYNC_LOAD_AFTER_TICKS) >= tasks.peek().tick) {
                tasks.poll().load();
            }
        }
    }
}
