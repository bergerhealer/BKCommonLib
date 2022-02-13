package com.bergerkiller.bukkit.common.internal;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongPredicate;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkLoadTimeoutException;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager;
import com.bergerkiller.bukkit.common.collections.RunnableConsumer;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.generated.net.minecraft.server.level.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;

/**
 * Manages 'forced chunk' logic in a way that allows multiple owners
 * with a reference counter.
 */
public class CommonForcedChunkManager extends ForcedChunkManager {
    private IdentityHashMap<World, ForcedWorld> forcedWorlds = new IdentityHashMap<>(); // Cloned on modify
    private final ChunkUnloadEventListener chunkUnloadListener = new ChunkUnloadEventListener();
    private final CommonPlugin plugin;
    private Task pendingHandler = null;
    private final CommonNextTickExecutor asyncLoadCallbackHandler = new CommonNextTickExecutor();
    private ChunkLoadTimeoutTracker loadTimeoutTracker = null;

    // After this number of ticks, consider the loading of a chunk to have failed
    private static final int FAIL_LOAD_AFTER_SECONDS = 300;
    private static final int FAIL_LOAD_AFTER_TICKS = (20*FAIL_LOAD_AFTER_SECONDS);

    public CommonForcedChunkManager(CommonPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        this.asyncLoadCallbackHandler.setExecutorTask(new ChunkLoadCallbackExecutor(this.plugin));
        this.loadTimeoutTracker = new ChunkLoadTimeoutTracker(this.plugin);
        this.loadTimeoutTracker.start(1, 1);
        this.pendingHandler = new Task(this.plugin) {
            @Override
            public void run() {
                forcedWorlds.values().forEach(ForcedWorld::sync);
            }
        };
        if (CommonCapabilities.CAN_CANCEL_CHUNK_UNLOAD_EVENT) {
            plugin.register(this.chunkUnloadListener);
        }
    }

    public void disable(CommonPlugin plugin) {
        this.loadTimeoutTracker.stop();
        this.loadTimeoutTracker = null;
        this.pendingHandler.stop();
        this.pendingHandler = null;
        this.forcedWorlds.values().forEach(ForcedWorld::disable);
        this.forcedWorlds = new IdentityHashMap<>();
        this.asyncLoadCallbackHandler.setExecutorTask(null);
    }

    /**
     * Same asLong function as used by Minecraft itself in ChunkCoordIntPair.
     * Keeps things simple with the 'isKeptLoaded' check, avoiding the need for
     * unwrapping the long.
     *
     * @param cx
     * @param cz
     * @return key
     */
    private static long makeChunkKey(int cx, int cz) {
        return (long) cx & 4294967295L | ((long) cz & 4294967295L) << 32;
    }

    public int getNumberOfForcedLoadedChunks() {
        int count = 0;
        for (ForcedWorld world : this.forcedWorlds.values()) {
            count += world.numKeptLoaded();
        }
        return count;
    }

    public boolean isForced(Chunk chunk) {
        ForcedWorld forced = forcedWorlds.get(chunk.getWorld());
        return forced != null && forced.isKeptLoaded(chunk.getX(), chunk.getZ());
    }

    @Override
    public ForcedChunkEntry add(World world, int chunkX, int chunkZ) {
        return getOrCreateForcedWorld(world).add(chunkX, chunkZ);
    }

    private ForcedWorld getOrCreateForcedWorld(World world) {
        ForcedWorld forcedWorld;

        if ((forcedWorld = this.forcedWorlds.get(world)) != null) {
            return forcedWorld;
        }

        synchronized (this) {
            if ((forcedWorld = this.forcedWorlds.get(world)) != null) {
                return forcedWorld;
            }

            forcedWorld = new ForcedWorld(world);
            IdentityHashMap<World, ForcedWorld> newForcedWorlds = new IdentityHashMap<>(this.forcedWorlds);
            newForcedWorlds.put(world, forcedWorld);
            this.forcedWorlds = newForcedWorlds;
            return forcedWorld;
        }
    }

    private final class Entry implements ForcedChunkEntry, Consumer<Object> {
        private final ForcedWorld world;
        private final long key;
        private final int cx, cz;
        private final AtomicInteger asyncCounter; // tracks state on other threads
        private int counter; // updated on main thread only
        private CompletableFuture<Chunk> chunkFuture;

        public Entry(ForcedWorld world, long key, int cx, int cz) {
            this.world = world;
            this.key = key;
            this.cx = cx;
            this.cz = cz;
            this.asyncCounter = new AtomicInteger();
            this.counter = 0;
            this.resetAsyncLoad();
        }

        public void resetAsyncLoad() {
            this.chunkFuture = new CompletableFuture<Chunk>();
        }

        public boolean isForced() {
            return this.counter > 0;
        }

        // Only called from main thread during disabling
        public void disable() {
            this.asyncCounter.set(0);
            if (this.counter > 0) {
                this.counter = 0;
                world.setForced(this, false);
            }
        }

        public void sync() {
            if (this.counter <= 0) {
                this.counter += this.asyncCounter.getAndSet(0);
                if (this.counter > 0) {
                    world.setForced(this, true);
                }
            } else {
                this.counter += this.asyncCounter.getAndSet(0);
                if (this.counter <= 0) {
                    world.setForced(this, false);
                }
            }
        }

        @Override
        public void add() {
            int new_async = this.asyncCounter.incrementAndGet();
            if (CommonUtil.isMainThread()) {
                this.sync();
            } else if (new_async == 1) {
                // Schedule a sync very soon on the main thread
                world.scheduleUpdate(this);
            }
        }

        @Override
        public void remove() {
            int new_async = this.asyncCounter.decrementAndGet();
            if (CommonUtil.isMainThread()) {
                this.sync();
            } else if (new_async == -1) {
                // Schedule a sync very soon on the main thread
                world.scheduleUpdate(this);
            }
        }

        public long getKey() {
            return this.key;
        }

        @Override
        public World getWorld() {
            return this.world.world;
        }

        @Override
        public int getX() {
            return this.cx;
        }

        @Override
        public int getZ() {
            return this.cz;
        }

        @Override
        public Chunk getChunk() {
            if (this.chunkFuture.isDone()) {
                try {
                    return this.chunkFuture.get();
                } catch (Throwable t) {}
            }
            Chunk chunk = world.world.getChunkAt(cx, cz);
            this.chunkFuture.complete(chunk);
            return chunk;
        }

        @Override
        public CompletableFuture<Chunk> getChunkAsync() {
            return this.chunkFuture;
        }

        /**
         * Aborts a pending chunk load with a ForcedChunkLoadTimeoutException if the chunk isn't loaded yet,
         * but is kept force-loaded.
         */
        public void abortIfNotLoaded() {
            if (this.isForced() && !this.chunkFuture.isDone()) {
                this.chunkFuture.completeExceptionally(new ForcedChunkLoadTimeoutException(world.world, cx, cz));
            }
        }

        @Override
        public void accept(Object result) {
            // Either -> Left
            result = RunnableConsumer.unpack(result);

            // If successful, complete the async chunk loading future
            if (result != null) {
                final org.bukkit.Chunk chunk = WrapperConversion.toChunk(result);
                asyncLoadCallbackHandler.execute(() -> this.chunkFuture.complete(chunk));
                return;
            }

            // If not successful, and this entry is still forced loaded, try again
            if (this.isForced()) {
                asyncLoadCallbackHandler.execute(this::startLoadingAsync);
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
            {
                org.bukkit.Chunk loadedChunk = this.world.handle.getChunkIfLoaded(this.cx, this.cz);
                if (loadedChunk != null) {
                    this.chunkFuture.complete(loadedChunk);
                    return;
                }
            }

            // This consumer is called from internal when the chunk is ready
            // Null is returned when loading fails
            final ChunkProviderServerHandle cps_handle = this.world.handle.getChunkProviderServer();
            final Executor executor = cps_handle.getAsyncExecutor();
            if (executor == null) {
                cps_handle.getChunkAtAsync(this.cx, this.cz, this);
            } else {
                CompletableFuture.runAsync(() -> cps_handle.getChunkAtAsync(this.cx, this.cz, Entry.this), executor);
            }
        }
    }

    /**
     * Everything about a Bukkit World where chunks are kept (force) loaded
     */
    private final class ForcedWorld {
        public final World world;
        public final WorldServerHandle handle;
        public final LongHashMap<Entry> chunks = new LongHashMap<Entry>();
        public final LongHashSet pending = new LongHashSet();
        private boolean disabled = false;

        public ForcedWorld(World world) {
            this.world = world;
            this.handle = WorldServerHandle.fromBukkit(world);
            if (CommonCapabilities.CHUNK_TICKETS_NOT_TICKED) {
                this.enableTickListsHook();
            }
        }

        public synchronized void disable() {
            disabled = true;
            pending.clear();
            Entry[] entries;
            while ((entries = chunks.values().toArray(new Entry[0])).length > 0) {
                for (Entry e : entries) {
                    e.disable();
                }
            }
            if (CommonCapabilities.CHUNK_TICKETS_NOT_TICKED) {
                this.disableTickListsHook();
            }
        }

        public synchronized void sync() {
            LongHashSet.LongIterator iter = pending.longIterator();
            while (iter.hasNext()) {
                Entry entry = chunks.get(iter.next());
                if (entry != null) {
                    entry.sync();
                }
            }
            pending.clear();

            if (this.chunks.size() == 0) {
                this.disabled = true;

                // Cleanup
                if (CommonCapabilities.CHUNK_TICKETS_NOT_TICKED) {
                    this.disableTickListsHook();
                }

                // Un-register from the by-world mapping
                synchronized (CommonForcedChunkManager.this) {
                    IdentityHashMap<World, ForcedWorld> newForcedWorlds = new IdentityHashMap<>(forcedWorlds);
                    if (newForcedWorlds.remove(this.world) == this) {
                        forcedWorlds = newForcedWorlds;
                    }
                }
            }
        }

        public synchronized Entry add(int cx, int cz) {
            if (this.disabled) {
                // Re-roll it.
                ForcedWorld defer = getOrCreateForcedWorld(this.world);
                if (this == defer) {
                    throw new IllegalStateException("ForcedWorld is disabled, but still stored in lookup table");
                } else {
                    return defer.add(cx, cz);
                }
            }

            long key = makeChunkKey(cx, cz);
            Entry entry = this.chunks.get(key);
            if (entry == null) {
                entry = new Entry(this, key, cx, cz);
                this.chunks.put(key, entry);
            }
            entry.add();
            return entry;
        }

        public synchronized void scheduleUpdate(Entry entry) {
            if (pending.isEmpty()) {
                pendingHandler.start();
            }
            pending.add(entry.getKey());
        }

        public synchronized int numKeptLoaded() {
            return chunks.size();
        }

        public synchronized boolean isKeptLoaded(int x, int z) {
            return this.chunks.contains(x, z);
        }

        public synchronized boolean isKeptLoaded(long key) {
            return this.chunks.contains(key);
        }

        // This method is only called on the main thread!
        public void setForced(Entry entry, boolean forced) {
            // Verify forced while synchronized around this.
            // Remove the entry when indeed, the chunk is no longer forced
            if (!forced) {
                synchronized (this) {
                    chunks.remove(entry.getKey());
                }
            }

            // This performs chunk loading/unloading automatically using 'tickets' in NMS ChunkMapDistance
            // This method is available on 1.13.1+
            // The ChunkUnloadEvent is not used for this, then
            if (CommonCapabilities.HAS_CHUNK_TICKET_API) {
                WorldServerHandle.fromBukkit(world).setForceLoadedAsync(
                        entry.getX(), entry.getZ(), plugin, forced);
            }

            // Load/unload the chunk
            if (forced) {
                // Request the chunk to be loaded asynchronously
                // The loadTimeoutTracker will error the chunk load if timed out
                loadTimeoutTracker.add(entry);
                entry.startLoadingAsync();
            } else {
                // Trigger the server to unload the chunk. It will fire a single
                // ChunkUnloadEvent (which we will handle) to make sure the chunk unloads.
                world.unloadChunkRequest(entry.getX(), entry.getZ());
                entry.resetAsyncLoad();
            }
        }

        private LongPredicate hookTickPredicate(LongPredicate previous) {
            if (previous instanceof ChunkTickingPredicate) {
                previous = ((ChunkTickingPredicate) previous).base; // avoid stack
            }
            return new ChunkTickingPredicate(this, previous);
        }

        private LongPredicate unhookTickPredicate(LongPredicate previous) {
            if (previous instanceof ChunkTickingPredicate) {
                return ((ChunkTickingPredicate) previous).base;
            } else {
                return previous;
            }
        }

        private void enableTickListsHook() {
            handle.transactBlockTicksChunkPredicate(this::hookTickPredicate);
            handle.transactFluidTicksChunkPredicate(this::hookTickPredicate);
        }

        private void disableTickListsHook() {
            handle.transactBlockTicksChunkPredicate(this::unhookTickPredicate);
            handle.transactFluidTicksChunkPredicate(this::unhookTickPredicate);
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

    // Tracks the ongoing load of a Chunk for a single timeout period
    private static class PendingChunkLoadTask {
        public final int tick;
        private final LinkedList<Entry> entries = new LinkedList<Entry>();

        public PendingChunkLoadTask(int ticks) {
            this.tick = ticks;
        }

        public void add(Entry entry) {
            this.entries.add(entry);
        }

        public void abortAllIfNotLoaded() {
            while (!entries.isEmpty()) {
                entries.poll().abortIfNotLoaded();
            }
        }
    }

    // Main task responsible for timing out chunk loads
    private static class ChunkLoadTimeoutTracker extends Task {
        private final LinkedList<PendingChunkLoadTask> tasks = new LinkedList<PendingChunkLoadTask>();
        private int currentTick = 0;

        public ChunkLoadTimeoutTracker(JavaPlugin plugin) {
            super(plugin);
        }

        public void add(Entry entry) {
            PendingChunkLoadTask task;
            if (tasks.isEmpty() || (task = tasks.peekLast()).tick != currentTick) {
                task = new PendingChunkLoadTask(currentTick);
                tasks.addLast(task);
            }
            task.add(entry);
        }

        @Override
        public void run() {
            currentTick++;

            // Load a single chunk (that started loading 10s ago) per tick
            // This makes sure all chunks are eventually force-loaded
            while (!tasks.isEmpty() && (currentTick-FAIL_LOAD_AFTER_TICKS) >= tasks.peek().tick) {
                tasks.poll().abortAllIfNotLoaded();
            }
        }
    }

    /**
     * Executes the load callbacks after a chunk finishes loading asynchronously
     */
    private static final class ChunkLoadCallbackExecutor extends CommonNextTickExecutor.ExecutorTask {

        public ChunkLoadCallbackExecutor(JavaPlugin plugin) {
            super(plugin);
        }
    }

    /**
     * Used on Minecraft 1.18 and later to replace the old predicate, to allow
     * forced chunks to tick blocks/fluids.
     */
    private static final class ChunkTickingPredicate implements LongPredicate {
        private final ForcedWorld world;
        private final LongPredicate base;

        public ChunkTickingPredicate(ForcedWorld world, LongPredicate base) {
            this.base = base;
            this.world = world;
        }

        @Override
        public boolean test(long value) {
            return base.test(value) || world.isKeptLoaded(value);
        }
    }
}
