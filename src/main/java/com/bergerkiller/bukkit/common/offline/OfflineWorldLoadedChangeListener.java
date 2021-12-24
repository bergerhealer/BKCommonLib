package com.bergerkiller.bukkit.common.offline;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.offline.OfflineWorld.BukkitWorldSupplier;

/**
 * Listener-based tracker of the loaded world of an OfflineWorld instance.
 * Internal use only!
 */
final class OfflineWorldLoadedChangeListener implements LibraryComponent, OfflineWorld.BukkitWorldSupplierHandler {
    private final Plugin plugin;
    private BukkitTask asyncClearTask;

    public OfflineWorldLoadedChangeListener(Plugin plugin) {
        this.plugin = plugin;
        this.asyncClearTask = null;
    }

    @Override
    public void enable() throws Throwable {
        OfflineWorld.setLoadedWorldSupplier(this);

        // Listen for when worlds load and unload
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onWorldInit(WorldInitEvent event) {
                OfflineWorld.BukkitWorldSupplier supplier = OfflineWorld.of(event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier) supplier).update(event.getWorld());
                }
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onWorldUnload(WorldUnloadEvent event) {
                OfflineWorld.BukkitWorldSupplier supplier = OfflineWorld.of(event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier) supplier).update(null);
                }
                OfflineWorld.clearByBukkitWorldCache(); // Just in case!
            }
        }, plugin);

        // Periodically clear the by-Bukkit world mapping to avoid memory leaks - just in case!
        // No need to run this on the main thread as it's all synchronized anyway
        final int clearInterval = 1200; // 10 minutes
        this.asyncClearTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, OfflineWorld::clearByBukkitWorldCache,
                clearInterval, clearInterval);
    }

    @Override
    public void disable() {
        this.asyncClearTask.cancel();
        this.asyncClearTask = null;
        OfflineWorld.setLoadedWorldSupplier(OfflineWorld.DefaultBukkitWorldSupplierHandler.INSTANCE);
    }

    @Override
    public BukkitWorldSupplier createBukkitWorldSupplier(UUID worldUUID) {
        return new WorldSupplier(worldUUID);
    }

    @Override
    public boolean cacheByBukkitWorld() {
        return true;
    }

    private static final class WorldSupplier implements OfflineWorld.BukkitWorldSupplier {
        private World loadedWorld;
        private Object comparer;

        public WorldSupplier(UUID worldUUID) {
            this.loadedWorld = Bukkit.getWorld(worldUUID);
            this.comparer = this.loadedWorld;
        }

        public void update(World world) {
            this.loadedWorld = world;
            this.comparer = (world == null) ? new Object() : world;
        }

        @Override
        public World get() {
            return this.loadedWorld;
        }

        @Override
        public boolean isWorld(World world) {
            return world == this.comparer;
        }
    }
}
