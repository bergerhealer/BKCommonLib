package com.bergerkiller.bukkit.common.chunk;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.ChunkUnloadEntitiesEvent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.LongHashMap;

abstract class ChunkFutureProviderImpl implements ChunkFutureProvider, Listener, Executor {
    protected final Plugin plugin;
    protected final LongHashMap<Chain> entries;
    private World currentlyUnloadingWorld = null; // Protection against memory leaks

    public ChunkFutureProviderImpl(Plugin plugin) {
        this.plugin = plugin;
        this.entries = new LongHashMap<Chain>();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR)
            public void onChunkLoad(ChunkLoadEvent event) {
                handleEvent(event.getChunk(), Mode.CHUNK_LOADED);
            }

            @EventHandler(priority = EventPriority.MONITOR)
            public void onChunkUnload(ChunkUnloadEvent event) {
                handleEvent(event.getChunk(), Mode.CHUNK_UNLOADED);
            }

            @EventHandler(priority = EventPriority.MONITOR)
            public void onChunkLoadEntities(ChunkLoadEntitiesEvent event) {
                handleEvent(event.getChunk(), Mode.CHUNK_ENTITIES_LOADED);
            }

            @EventHandler(priority = EventPriority.MONITOR)
            public void onChunkUnloadEntities(ChunkUnloadEntitiesEvent event) {
                handleEvent(event.getChunk(), Mode.CHUNK_ENTITIES_UNLOADED);
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onWorldUnload(WorldUnloadEvent event) {
                try {
                    currentlyUnloadingWorld = event.getWorld();

                    // First operate on all chunks of the world - the clean way
                    for (Chunk chunk : event.getWorld().getLoadedChunks()) {
                        handleEvent(chunk, Mode.CHUNK_UNLOADED);
                    }

                    // If any entries remain for this world, clean them up by cancelling them
                    for (Chain chain : new ArrayList<Chain>(entries.values())) {
                        for (Entry e = chain.first; e != null; e = e.next) {
                            if (e.world == event.getWorld()) {
                                chain.remove(e);
                                cancelFast(e.future);
                            }
                        }
                    }
                } finally {
                    currentlyUnloadingWorld = null;
                }
            }
        }, plugin);
    }

    @Override
    public CompletableFuture<Chunk> whenAllNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours) {
        final CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();

        // Start tracking
        final ChunkStateTracker tracker = this.trackNeighboursLoaded(mainChunk, neighbours, new ChunkStateListener() {
            @Override
            public void onRegistered(ChunkStateTracker tracker) {
                if (tracker.isLoaded()) {
                    onLoaded(tracker);
                }
            }

            @Override
            public void onCancelled(ChunkStateTracker tracker) {
                cancelFast(future);
            }

            @Override
            public void onLoaded(ChunkStateTracker tracker) {
                future.complete(tracker.getChunk());
                tracker.cancel(); // Calls onCancelled - but future is already completed so is fine
            }

            @Override
            public void onUnloaded(ChunkStateTracker tracker) {
            }
        });

        // When future is cancelled by the user we want to cancel the tracker too
        // Also cancels when internally cancelled (main chunk unloads), but that's fine.
        if (!future.isDone()) {
            future.exceptionallyAsync(t -> {
                if (t instanceof CompletionException) {
                    tracker.cancel();
                }
                return null;
            }, this);
        }

        return future;
    }

    private void handleEvent(Chunk chunk, Mode mode) {
        Chain chain = entries.get(MathUtil.longHashToLong(chunk.getX(), chunk.getZ()));
        if (chain == null) {
            return;
        }

        World world = chunk.getWorld();
        for (Entry e = chain.first; e != null; e = e.next) {
            if (e.world == world && e.mode == mode) {
                chain.remove(e);
                e.future.complete(e.passChunkToFuture ? chunk : null);
            }
        }
    }

    protected <T> CompletableFuture<T> createEntry(Chunk chunk, Mode mode,
            boolean passChunkToFuture, CompletableFuture<T> future)
    {
        return createEntry(chunk.getWorld(), chunk.getX(), chunk.getZ(), mode, passChunkToFuture, future);
    }

    protected <T> CompletableFuture<T> createEntry(World world, int cx, int cz, Mode mode,
            boolean passChunkToFuture, CompletableFuture<T> future)
    {
        // If this world is unloading, don't register anything and cancel right away
        // For unload events, complete them (world is unloaded after all)
        if (world == currentlyUnloadingWorld) {
            cancelFast(future);
            return future;
        }

        long key = MathUtil.longHashToLong(cx, cz);
        Chain chain = entries.get(key);
        Entry entry;
        if (chain == null) {
            chain = new Chain(this, key, world, mode, passChunkToFuture, CommonUtil.unsafeCast(future));
            entry = chain.first;
            entries.put(key, chain);
        } else {
            entry = chain.add(world, mode, passChunkToFuture, CommonUtil.unsafeCast(future));
        }

        // This may randomly instantly remove it if the operation was done asynchronously!
        entry.removeWhenFutureCancelled();

        return future;
    }

    public static final class MainThreadHandler extends ChunkFutureProviderImpl {
        public static final Map<Plugin, ChunkFutureProvider> handlers = new IdentityHashMap<>();

        public MainThreadHandler(Plugin plugin) {
            super(plugin);
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisabled(PluginDisableEvent event) {
            if (event.getPlugin() == this.plugin) {
                handlers.remove(this.plugin);
            }
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ) {
            Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
            if (chunk != null) {
                return CompletableFuture.completedFuture(chunk);
            } else {
                return createEntry(world, chunkX, chunkZ, Mode.CHUNK_LOADED, true, new CompletableFuture<Chunk>());
            }
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(Chunk chunk) {
            if (WorldUtil.isLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ())) {
                return CompletableFuture.completedFuture(chunk);
            } else {
                return createEntry(chunk, Mode.CHUNK_LOADED, true, new CompletableFuture<Chunk>());
            }
        }

        @Override
        public CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ) {
            if (!WorldUtil.isLoaded(world, chunkX, chunkZ)) {
                return CompletableFuture.completedFuture(null);
            } else {
                return createEntry(world, chunkX, chunkZ, Mode.CHUNK_UNLOADED, false, new CompletableFuture<Void>());
            }
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ) {
            Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
            if (chunk != null && WorldUtil.isChunkEntitiesLoaded(chunk)) {
                return CompletableFuture.completedFuture(chunk);
            } else {
                return createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_LOADED, true, new CompletableFuture<Chunk>());
            }
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(Chunk chunk) {
            if (WorldUtil.isChunkEntitiesLoaded(chunk)) {
                return CompletableFuture.completedFuture(chunk);
            } else {
                return createEntry(chunk, Mode.CHUNK_ENTITIES_LOADED, true, new CompletableFuture<Chunk>());
            }
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ) {
            if (!WorldUtil.isChunkEntitiesLoaded(world, chunkX, chunkZ)) {
                return CompletableFuture.completedFuture(null);
            } else {
                return createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_UNLOADED, false, new CompletableFuture<Void>());
            }
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(Chunk chunk) {
            if (!WorldUtil.isChunkEntitiesLoaded(chunk)) {
                return CompletableFuture.completedFuture(null);
            } else {
                return createEntry(chunk, Mode.CHUNK_ENTITIES_UNLOADED, false, new CompletableFuture<Void>());
            }
        }

        @Override
        public ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkStateListener listener) {
            ChunkLoadedTrackerSingleImpl tracker = new ChunkLoadedTrackerSingleImpl(world, chunkX, chunkZ, listener);
            tracker.start(MainThreadHandler.this);
            return tracker;
        }

        @Override
        public ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours, ChunkStateListener listener) {
            final ChunkTrackerListImpl tracker;
            try {
                tracker = (ChunkTrackerListImpl) neighbours;
            } catch (ClassCastException ex) {
                // Who could ever be so stupid?
                throw new IllegalArgumentException("Neighbours argument must be created using ChunkTrackerList.create()");
            }

            // Start and done
            tracker.start(mainChunk, this, listener);
            return tracker;
        }
    }

    public static final class ThreadSafeHandler extends ChunkFutureProviderImpl {
        public static final Map<Plugin, ChunkFutureProvider> handlers = new IdentityHashMap<>();

        public ThreadSafeHandler(Plugin plugin) {
            super(plugin);
        }

        @Override
        public void execute(Runnable command) {
            if (CommonUtil.isMainThread()) {
                // Avoid overhead/tick delay if already on the main thread
                command.run();
            } else if (!plugin.isEnabled()) {
                Logging.LOGGER.warning("Failed to execute task for plugin " + plugin.getName() + " because plugin is disabled");
            } else if (plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, command) == -1) {
                Logging.LOGGER.warning("Failed to execute task for plugin " + plugin.getName() + " because scheduling failed");
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisabled(PluginDisableEvent event) {
            if (event.getPlugin() == this.plugin) {
                synchronized (ThreadSafeHandler.class) {
                    handlers.remove(this.plugin);
                }
            }
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ) {
            final CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();
            execute(() -> {
                Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
                if (chunk != null) {
                    future.complete(chunk);
                } else {
                    createEntry(world, chunkX, chunkZ, Mode.CHUNK_LOADED, true, future);
                }
            });
            return future;
        }

        @Override
        public CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ) {
            final CompletableFuture<Void> future = new CompletableFuture<Void>();
            execute(() -> {
                if (!WorldUtil.isLoaded(world, chunkX, chunkZ)) {
                    future.complete(null);
                } else {
                    createEntry(world, chunkX, chunkZ, Mode.CHUNK_UNLOADED, false, future);
                }
            });
            return future;
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ) {
            final CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();
            execute(() -> {
                Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
                if (chunk != null && WorldUtil.isChunkEntitiesLoaded(chunk)) {
                    future.complete(chunk);
                } else {
                    createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_LOADED, true, future);
                }
            });
            return future;
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ) {
            final CompletableFuture<Void> future = new CompletableFuture<Void>();
            execute(() -> {
                Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
                if (chunk == null || !WorldUtil.isChunkEntitiesLoaded(chunk)) {
                    future.complete(null);
                } else {
                    createEntry(world, chunkX, chunkZ, Mode.CHUNK_ENTITIES_UNLOADED, false, future);
                }
            });
            return future;
        }

        @Override
        public ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkStateListener listener) {
            final ChunkLoadedTrackerSingleImpl tracker = new ChunkLoadedTrackerSingleImpl(world, chunkX, chunkZ, listener);
            execute(() -> tracker.start(ThreadSafeHandler.this));
            return tracker;
        }

        @Override
        public ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours, ChunkStateListener listener) {
            final ChunkTrackerListImpl tracker;
            try {
                tracker = (ChunkTrackerListImpl) neighbours;
            } catch (ClassCastException ex) {
                // Who could ever be so stupid?
                throw new IllegalArgumentException("Neighbours argument must be created using ChunkTrackerList.create()");
            }

            // Start on main thread and done
            execute(() -> tracker.start(mainChunk, ThreadSafeHandler.this, listener));
            return tracker;
        }
    }

    public static final class SyncLoadHandler implements ChunkFutureProvider {
        public static final SyncLoadHandler INSTANCE = new SyncLoadHandler();

        @Override
        public CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ) {
            return CompletableFuture.completedFuture(world.getChunkAt(chunkX, chunkZ));
        }

        @Override
        public CompletableFuture<Chunk> whenLoaded(Chunk chunk) {
            return CompletableFuture.completedFuture(chunk);
        }

        @Override
        public CompletableFuture<Chunk> whenNeighbourLoaded(Chunk mainChunk, int neighbourChunkX, int neighbourChunkZ) {
            if (neighbourChunkX == mainChunk.getX() && neighbourChunkZ == mainChunk.getZ()) {
                return CompletableFuture.completedFuture(mainChunk);
            } else {
                return CompletableFuture.completedFuture(mainChunk.getWorld().getChunkAt(neighbourChunkX, neighbourChunkZ));
            }
        }

        @Override
        public CompletableFuture<Chunk> whenAllNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours) {
            return CompletableFuture.completedFuture(mainChunk);
        }

        @Override
        public CompletableFuture<BlockData> readNeighbourBlockData(Chunk mainChunk, Block block) {
            return CompletableFuture.completedFuture(WorldUtil.getBlockData(block));
        }

        @Override
        public CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkStateListener listener) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }

        @Override
        public ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours, ChunkStateListener listener) {
            throw new UnsupportedOperationException("Only whenLoaded is supported by the Sync ChunkFutureProvider");
        }
    }

    /**
     * Type of chunk-related events to listen for
     */
    public static enum Mode {
        CHUNK_LOADED {
            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk != null && chunk.isLoaded();
            }
        },
        CHUNK_UNLOADED {
            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk == null || !chunk.isLoaded();
            }
        },
        CHUNK_ENTITIES_LOADED {
            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk != null && WorldUtil.isChunkEntitiesLoaded(chunk);
            }
        },
        CHUNK_ENTITIES_UNLOADED {
            @Override
            public boolean isCompleted(Chunk chunk) {
                return chunk == null || !WorldUtil.isChunkEntitiesLoaded(chunk);
            }
        };

        public abstract boolean isCompleted(Chunk chunk);
    }

    private static final class Chain {
        public final ChunkFutureProviderImpl handler;
        public final long key;
        public Entry first;
        public Entry last;

        public Chain(ChunkFutureProviderImpl handler, long key, World world, Mode mode,
                boolean passChunkToFuture, CompletableFuture<Chunk> future)
        {
            this.handler = handler;
            this.key = key;
            this.first = new Entry(this, world, mode, passChunkToFuture, future);
            this.last = this.first;
        }

        public Entry add(World world, Mode mode, boolean passChunkToFuture, CompletableFuture<Chunk> future) {
            Entry entry = new Entry(this, world, mode, passChunkToFuture, future);
            entry.previous = this.last;
            this.last.next = entry;
            this.last = entry;
            return entry;
        }

        public void remove(Entry entry) {
            if (entry == first) {
                // head
                first = entry.next;
                if (first != null) {
                    first.previous = null;
                } else {
                    // Remove chain itself, is empty
                    handler.entries.remove(key);
                }
            } else if (entry == last) {
                // tail
                last = entry.previous;
                last.next = null;
            } else if (entry.previous != null && entry.previous.next == entry) {
                // middle (we do a sanity check in case of double-removal)
                entry.previous.next = entry.next;
                entry.next.previous = entry.previous;
            }
        }
    }

    private static final class Entry {
        public final Chain chain;
        public final World world;
        public final Mode mode;
        public Entry previous;
        public Entry next;
        public final boolean passChunkToFuture;
        public final CompletableFuture<Chunk> future;

        public Entry(Chain chain, World world, Mode mode, boolean passChunkToFuture, CompletableFuture<Chunk> future) {
            this.chain = chain;
            this.world = world;
            this.mode = mode;
            this.previous = null;
            this.next = null;
            this.passChunkToFuture = passChunkToFuture;
            this.future = future;
        }

        public void removeWhenFutureCancelled() {
            future.exceptionallyAsync(err -> {
                if (err instanceof CompletionException) {
                    chain.remove(Entry.this);
                }
                return null;
            }, chain.handler);
        }
    }

    protected static final class ChunkTrackerListFutureImpl extends ChunkTrackerListImpl {
        public final CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();

        public ChunkTrackerListFutureImpl(int numTrackersCapacity) {
            super(numTrackersCapacity);
        }
    }

    /**
     * Implements the ChunkTrackerList as a main-chunk bound tracker for all the
     * chunks added prior. Holds the main future returned.
     */
    protected static class ChunkTrackerListImpl implements ChunkNeighbourList, ChunkStateTracker {
        private static final CompletableFuture<Void> UNLOAD_FUTURE_INITIAL = CompletableFuture.completedFuture(null);
        private final ArrayList<ChunkLoadedTrackerSingleImpl> trackers;
        private final ChunkStateListener neighbourListener; // Notified of changes to neighbours
        private int numLoaded = 0;
        private CompletableFuture<Void> mainChunkUnloadFuture = UNLOAD_FUTURE_INITIAL;
        private Chunk mainChunk = null;
        private ChunkFutureProviderImpl provider = null;
        private boolean isHandlingUnloadEvent = false;
        private boolean cancelled = false;
        private ChunkStateListener listener; // To be registered at start

        public ChunkTrackerListImpl(int numTrackersCapacity) {
            trackers = new ArrayList<>(numTrackersCapacity);
            neighbourListener = new ChunkStateListener() {
                @Override
                public void onRegistered(ChunkStateTracker tracker) {
                    if (tracker.isLoaded()) {
                        ++numLoaded;
                    }
                }

                @Override
                public void onCancelled(ChunkStateTracker tracker) {
                }

                @Override
                public void onLoaded(ChunkStateTracker tracker) {
                    if (++numLoaded == trackers.size()) {
                        fireNeighboursLoaded();
                    }
                }

                @Override
                public void onUnloaded(ChunkStateTracker tracker) {
                    if (numLoaded-- == trackers.size()) {
                        fireNeighboursUnloaded();
                    }
                }
            };
        }

        @Override
        public void add(World world, int chunkX, int chunkZ) {
            this.trackers.add(new ChunkLoadedTrackerSingleImpl(world, chunkX, chunkZ, neighbourListener));
        }

        public void start(final Chunk mainChunk, ChunkFutureProviderImpl provider, ChunkStateListener listener) {
            // May be cancelled before it even starts
            if (cancelled) {
                return;
            }

            // If already called before, skip all this
            if (this.mainChunk != null) {
                throw new IllegalStateException("ChunkTrackerList can only be used once");
            }
            this.mainChunk = mainChunk;
            this.provider = provider;
            this.listener = listener;

            // Start tracking all added neighbour chunks
            // All neighbours may already be loaded, if so, stop right away.
            // It will initialize the chunk counter initial value
            if (!trackers.isEmpty()) {
                for (ChunkLoadedTrackerSingleImpl tracker : trackers) {
                    tracker.start(provider);
                }
            }

            // We now know whether all neighbours are loaded or not, using the counter
            // Let the listener know. The listener may cancel early (!)
            fireNeighboursRegistered();
            if (cancelled) {
                return;
            }

            // When the main chunk unloads in the future, cancel this listener
            // This will, in turn, cancel any trackers for neighbours as well.
            this.mainChunkUnloadFuture = provider.whenUnloaded(mainChunk);
            this.mainChunkUnloadFuture.thenAccept(unused -> {
                cancelled = true;
                trackers.forEach(ChunkLoadedTrackerSingleImpl::cancel);
                if (numLoaded == trackers.size()) {
                    fireNeighboursUnloaded();
                }
                fireCancelled();
            });
        }

        @Override
        public void cancel() {
            if (!cancelled) {
                provider.execute(() -> {
                    if (!cancelled) {
                        cancelled = true;
                        trackers.forEach(ChunkLoadedTrackerSingleImpl::cancel);
                        fireCancelled();
                        cancelFast(mainChunkUnloadFuture);
                    }
                });
            }
        }

        private void fireNeighboursRegistered() {
            try {
                listener.onRegistered(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onRegistered", t);
            }
        }

        private void fireNeighboursLoaded() {
            try {
                listener.onLoaded(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onLoaded", t);
            }
        }

        private void fireNeighboursUnloaded() {
            boolean prevHandlingUnload = isHandlingUnloadEvent;
            try {
                isHandlingUnloadEvent = true;
                listener.onUnloaded(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onUnloaded", t);
            } finally {
                isHandlingUnloadEvent = prevHandlingUnload;
            }
        }

        private void fireCancelled() {
            try {
                listener.onCancelled(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk neighbour tracking failed: Error calling onCancelled", t);
            }
        }

        @Override
        public World getWorld() {
            return mainChunk.getWorld();
        }

        @Override
        public int getChunkX() {
            return mainChunk.getX();
        }

        @Override
        public int getChunkZ() {
            return mainChunk.getZ();
        }

        @Override
        public Chunk getChunk() {
            return (isHandlingUnloadEvent || isLoaded()) ? mainChunk : null;
        }

        @Override
        public boolean isLoaded() {
            return numLoaded == trackers.size();
        }
    }

    private static final class ChunkLoadedTrackerSingleImpl implements ChunkStateTracker {
        private ChunkFutureProviderImpl provider;
        private final World world;
        private final int chunkX;
        private final int chunkZ;
        private boolean loaded;
        private Chunk cachedChunk;
        private final ChunkStateListener listener;
        private final Consumer<Chunk> whenLoaded;
        private final Consumer<Chunk> whenUnloaded;
        private CompletableFuture<?> currentFuture;
        private boolean cancelled;

        public ChunkLoadedTrackerSingleImpl(World world, int chunkX, int chunkZ, ChunkStateListener listener) {
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.listener = listener;
            this.currentFuture = null;
            this.cancelled = false;
            this.loaded = false;
            this.cachedChunk = null;
            this.whenLoaded = c -> {
                if (!cancelled) {
                    onLoaded(c);
                    fireLoaded();
                }
            };
            this.whenUnloaded = c -> {
                if (!cancelled) {
                    onUnloaded(c);
                    fireUnloaded();
                    cachedChunk = null;
                }
            };
        }

        /**
         * Starts tracking until {@link #cancel()} is called
         */
        public void start(ChunkFutureProviderImpl provider) {
            // Protect
            if (this.cancelled) {
                return;
            }

            this.provider = provider;
            this.cancelled = false;
            this.cachedChunk = null;
            Chunk chunk = WorldUtil.getChunk(world, chunkX, chunkZ);
            if (chunk != null) {
                onLoaded(chunk);
            } else {
                onUnloaded(null);
            }

            // Registration completed
            try {
                listener.onRegistered(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onRegistered", t);
            }
        }

        private void onLoaded(Chunk chunk) {
            // Wait for when the chunk unloads again in the future
            this.cachedChunk = chunk;
            this.loaded = true;
            CompletableFuture<Chunk> whenUnloadedFuture = provider.createEntry(world, chunkX, chunkZ,
                    Mode.CHUNK_UNLOADED, true, new CompletableFuture<Chunk>());
            whenUnloadedFuture.thenAccept(whenUnloaded);
            currentFuture = whenUnloadedFuture;
        }

        private void onUnloaded(Chunk chunk) {
            // Wait for when the chunk loads again in the future
            this.cachedChunk = chunk;
            this.loaded = false;
            CompletableFuture<Chunk> whenLoadedFuture = provider.createEntry(world, chunkX, chunkZ,
                    Mode.CHUNK_LOADED, true, new CompletableFuture<Chunk>());
            whenLoadedFuture.thenAccept(whenLoaded);
            currentFuture = whenLoadedFuture;
        }

        private void fireLoaded() {
            try {
                listener.onLoaded(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onLoaded", t);
            }
        }

        private void fireUnloaded() {
            try {
                listener.onUnloaded(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onUnloaded", t);
            }
        }

        private void fireCancelled() {
            try {
                listener.onCancelled(this);
            } catch (Throwable t) {
                provider.plugin.getLogger().log(Level.SEVERE, "Chunk tracking failed: Error calling listener onCancelled", t);
            }
        }

        @Override
        public void cancel() {
            if (!cancelled) {
                provider.execute(() -> {
                    if (!cancelled) {
                        cancelled = true;
                        cancelFast(currentFuture);
                        fireCancelled();
                    }
                });
            }
        }

        @Override
        public World getWorld() {
            return world;
        }

        @Override
        public int getChunkX() {
            return chunkX;
        }

        @Override
        public int getChunkZ() {
            return chunkZ;
        }

        @Override
        public Chunk getChunk() {
            return cachedChunk;
        }

        @Override
        public boolean isLoaded() {
            return loaded;
        }
    }

    private static void cancelFast(CompletableFuture<?> completableFuture) {
        //completableFuture.cancel(false);
        completableFuture.completeExceptionally(FutureCancelledException.INSTANCE);
    }
}
