package com.bergerkiller.bukkit.common.offline;

import java.util.UUID;
import java.util.function.Supplier;

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

/**
 * Listener-based tracker of the loaded world of an OfflineWorld instance.
 * Internal use only!
 */
final class OfflineWorldLoadedChangeListener implements LibraryComponent {
    private final Plugin plugin;
    private BukkitTask asyncClearTask;

    public OfflineWorldLoadedChangeListener(Plugin plugin) {
        this.plugin = plugin;
        this.asyncClearTask = null;
    }

    @Override
    public void enable() throws Throwable {
        OfflineWorld.setLoadedWorldSupplier(WorldSupplier::new);

        // Listen for when worlds load and unload
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onWorldInit(WorldInitEvent event) {
                Supplier<World> supplier = OfflineWorld.of(event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier) supplier).loadedWorld = event.getWorld();
                }
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onWorldUnload(WorldUnloadEvent event) {
                Supplier<World> supplier = OfflineWorld.of(event.getWorld()).loadedWorldSupplier;
                if (supplier instanceof WorldSupplier) {
                    ((WorldSupplier) supplier).loadedWorld = null;
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
        OfflineWorld.setLoadedWorldSupplier(OfflineWorld.toWorldSupplierFuncDefault);
    }

    private static final class WorldSupplier implements Supplier<World> {
        public World loadedWorld;

        public WorldSupplier(UUID worldUUID) {
            this.loadedWorld = Bukkit.getWorld(worldUUID);
        }

        @Override
        public World get() {
            return this.loadedWorld;
        }
    }
}
