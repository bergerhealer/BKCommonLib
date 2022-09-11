package com.bergerkiller.bukkit.common.internal.map;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;

/**
 * Updates the players viewing item frames and fires events for them
 */
class MapDisplayFramedMapUpdater extends Task {
    /**
     * How many item frames are polled each tick that had no events or significant
     * changes happen to them. 99.99% of the time the item inside of it has not
     * changed, but if plugins changed it through code, this will catch those
     * and refresh.
     */
    public static final int NUM_IDLE_FRAMES_POLLED_PER_TICK = 50;

    private final CommonMapController controller;
    private final BiConsumer<ItemFrameInfo, Player> handleMapShowEvent;
    private ItemFrameInfo.UpdateEntry firstEntryUpdated = null;
    private ItemFrameInfo.UpdateEntry lastEntryUpdated = null;

    public MapDisplayFramedMapUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
        this.handleMapShowEvent = (currentFrame, newViewer) -> {
            this.controller.handleMapShowEvent(new MapShowEvent(newViewer, currentFrame.itemFrame));
        };
    }

    @Override
    public void run() {
        // Enable the item frame cluster cache while performing these operations
        controller.itemFrameClustersByWorldEnabled = true;
        try {
            synchronized (controller) {
                updateItemFrameItemAndViewers();
                controller.mapsWithItemFrameResolutionChanges.forEachAndClear(MapDisplayInfo::updateItemFrameResolution);
                controller.mapsWithItemFrameViewerChanges.forEachAndClear(MapDisplayInfo::updateItemFrameViewers);
                controller.itemFramesThatNeedItemRefresh.forEachAndClear(c -> c.itemFrameHandle.refreshItem());
            }
        } finally {
            // Disable cache again and wipe
            controller.itemFrameClustersByWorldEnabled = false;
            controller.itemFrameClustersByWorld.clear();
        }
    }

    private void updateItemFrameItemAndViewers() {
        // Iterate all tracked item frames and update them
        ItemFrameInfo.UpdateEntry entry = controller.itemFrameUpdateList.first();

        try {
            // In the beginning are all prioritized entries - process those first
            // Avoids unneeded if-checks later.
            while (entry != null && entry.prioritized) {
                entry.prioritized = false;
                updateItemAndViewers(entry);
                entry = entry.next;
            }

            // Process up to the limit more non-prioritized entries
            int numNonPrioritized = 0;
            while (entry != null && ++numNonPrioritized <= NUM_IDLE_FRAMES_POLLED_PER_TICK) {
                updateItemAndViewers(entry);
                entry = entry.next;
            }

            // Any remaining entries all we update are the viewers and whether it got removed
            // These unfortunately must be tracked every tick to stay up to date
            // In future, this is in need of improvement!
            while (entry != null) {
                updateViewersPassive(entry);
                entry = entry.next;
            }

            // If an entry was updated, move everything from start to there to the end of the chain
            // If there are very little entries then the end is already at the end and nothing happens
            if (firstEntryUpdated != null) {
                controller.itemFrameUpdateList.moveRangeToEnd(firstEntryUpdated, lastEntryUpdated);
            }
        } finally {
            // Clean up
            firstEntryUpdated = null;
            lastEntryUpdated = null;
        }
    }

    private void updateViewersPassive(ItemFrameInfo.UpdateEntry entry) {
        if (!entry.info.handleRemoved()) {
            entry.info.updateViewers(handleMapShowEvent);
            if (!entry.info.handleRemoved()) {
                return;
            }
        }

        // Is removed, so remove it.
        remove(entry);
    }

    private void updateItemAndViewers(ItemFrameInfo.UpdateEntry entry) {
        if (!entry.info.handleRemoved()) {
            entry.info.updateItem();
            entry.info.updateViewers(handleMapShowEvent);
            if (!entry.info.handleRemoved()) {
                lastEntryUpdated = entry;
                if (firstEntryUpdated == null) {
                    firstEntryUpdated = entry;
                }
                return;
            }
        }

        // Is removed, so remove it.
        remove(entry);
    }

    private void remove(ItemFrameInfo.UpdateEntry entry) {
        controller.itemFrameUpdateList.remove(entry);
        controller.itemFrames.remove(entry.info.itemFrameHandle.getId());
    }
}
