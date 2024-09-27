package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
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
 * Spigot 1.21 & older 1.21.1 are busted. Getting the chunk handle in the ChunkUnloadEvent
 * doesn't work. This tracker keeps the handle cached in memory so that this
 * works properly.<br>
 * <br>
 * <a href="https://hub.spigotmc.org/jira/browse/SPIGOT-7780">This issue</a> was fixed at a later
 * 1.21.1 version, so do check during chunk unload whether this issue still exists once.
 */
class ChunkHandleTracker_Spigot_1_21 implements ChunkHandleTracker {
    private final ConcurrentHashMap<Chunk, CachedChunkHandle> cache = new ConcurrentHashMap<>();
    private final FastTrackedUpdateSet<CachedChunkHandle> pendingRemoval = new FastTrackedUpdateSet<>();
    private boolean cacheEnabled = true;
    private boolean bugDetectOccurred = false;

    private final Listener listener = new Listener() {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onChunkLoad(ChunkLoadEvent event) {
            if (!cacheEnabled) {
                return;
            }

            CachedChunkHandle previous = cache.put(event.getChunk(),
                    new CachedChunkHandle(event.getChunk(), pendingRemoval));
            if (previous != null) {
                previous.tracker.set(false);
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChunkUnload(ChunkUnloadEvent event) {
            if (!cacheEnabled) {
                return;
            }

            if (!bugDetectOccurred) {
                bugDetectOccurred = true;
                try {
                    ChunkHandleTracker_Default.getHandle(event.getChunk());

                    // Nope, bug is fixed!
                    cacheEnabled = false;
                    stopTracking();
                    return;
                } catch (Throwable t) {
                    // Yup!
                    cacheEnabled = true;
                }
            }

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
        cacheEnabled = true;
        bugDetectOccurred = false;

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
        cacheEnabled = false;
        CommonUtil.unregisterListener(this.listener);
        Task.stop(cleanupTask);
        cleanupTask = null;
        cache.clear();
        pendingRemoval.clear();
    }

    @Override
    public Object getChunkHandle(Chunk chunk) {
        if (cacheEnabled) {
            return cache.computeIfAbsent(chunk, c -> new CachedChunkHandle(c, pendingRemoval)).handle;
        } else {
            try {
                return ChunkHandleTracker_Default.getHandle(chunk); // No cache
            } catch (Throwable t) {
                // Still bugged out for some reason. Re-enable the cache I guess to try and help the situation
                bugDetectOccurred = true;
                cacheEnabled = true;

                // I guess we'll miss this one :(
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }
    }

    private static class CachedChunkHandle {
        public final Chunk chunk;
        public final Object handle;
        public final FastTrackedUpdateSet.Tracker<CachedChunkHandle> tracker;

        public CachedChunkHandle(Chunk chunk, FastTrackedUpdateSet<CachedChunkHandle> set) {
            this.chunk = chunk;
            this.handle = ChunkHandleTracker_Default.getHandle(chunk);
            this.tracker = set.track(this);
        }
    }
}
