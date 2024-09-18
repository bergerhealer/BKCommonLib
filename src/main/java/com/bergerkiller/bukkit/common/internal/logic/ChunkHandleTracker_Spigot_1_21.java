package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftChunkHandle;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Spigot 1.21 is busted. Getting the chunk handle in the ChunkUnloadEvent
 * doesn't work. This tracker keeps the handle cached in memory so that this
 * works properly.
 */
class ChunkHandleTracker_Spigot_1_21 implements ChunkHandleTracker {
    private final ConcurrentHashMap<Chunk, CachedChunkHandle> cache = new ConcurrentHashMap<>();
    private final FastTrackedUpdateSet<CachedChunkHandle> pendingRemoval = new FastTrackedUpdateSet<>();

    private final Listener listener = new Listener() {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onChunkLoad(ChunkLoadEvent event) {
            CachedChunkHandle previous = cache.put(event.getChunk(),
                    new CachedChunkHandle(event.getChunk(), pendingRemoval));
            if (previous != null) {
                previous.tracker.set(false);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChunkUnload(ChunkUnloadEvent event) {
            CachedChunkHandle cached = cache.get(event.getChunk());
            if (cached != null) {
                cached.tracker.set(true);
            }
        }
    };
    private Task cleanupTask;

    @Override
    public void enable() throws Throwable {
    }

    @Override
    public void disable() throws Throwable {
    }

    @Override
    public void startTracking(CommonPlugin plugin) {
        plugin.register(this.listener);

        cleanupTask = new Task(plugin) {
            @Override
            public void run() {
                pendingRemoval.forEachAndClear(c -> cache.remove(c.chunk, c));
            }
        }.start(1, 1);

        // Cache all currently loaded chunks
        try {
            for (World world : Bukkit.getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    getChunkHandle(chunk);
                }
            }
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to cache chunk handles", t);
        }
    }

    @Override
    public void stopTracking() {
        CommonUtil.unregisterListener(this.listener);
        Task.stop(cleanupTask);
        cleanupTask = null;
        cache.clear();
        pendingRemoval.clear();
    }

    @Override
    public Object getChunkHandle(Chunk chunk) {
        try {
            return cache.computeIfAbsent(chunk, c -> {
                if (cleanupTask == null) {
                    throw new DisabledCacheException();
                } else {
                    return new CachedChunkHandle(c, pendingRemoval);
                }
            }).handle;
        } catch (DisabledCacheException ex) {
            return getHandle(chunk); // No cache
        }
    }

    private static class CachedChunkHandle {
        public final Chunk chunk;
        public final Object handle;
        public final FastTrackedUpdateSet.Tracker<CachedChunkHandle> tracker;

        public CachedChunkHandle(Chunk chunk, FastTrackedUpdateSet<CachedChunkHandle> set) {
            this.chunk = chunk;
            this.handle = getHandle(chunk);
            this.tracker = set.track(this);
        }
    }

    private static Object getHandle(Chunk chunk) {
        try {
            return CraftChunkHandle.T.getHandle.invoker.invoke(chunk);
        } catch (RuntimeException ex) {
            if (CraftChunkHandle.T.isAssignableFrom(chunk)) {
                throw ex;
            } else {
                return null;
            }
        }
    }

    private static class DisabledCacheException extends RuntimeException {
        /**
         * Static singleton instance of this exception, to avoid overhead of construction
         */
        public static final DisabledCacheException INSTANCE = new DisabledCacheException();

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
