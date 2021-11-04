package com.bergerkiller.bukkit.common.internal.map;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Updates the players viewing item frames and fires events for them
 */
class MapDisplayFramedMapUpdater extends Task {
    private final CommonMapController controller;
    private ItemFrameInfo info = null;
    private final LogicUtil.ItemSynchronizer<Player, Player> synchronizer = new LogicUtil.ItemSynchronizer<Player, Player>() {
        @Override
        public boolean isItem(Player item, Player value) {
            return item == value;
        }

        @Override
        public Player onAdded(Player player) {
            controller.handleMapShowEvent(new MapShowEvent(player, info.itemFrame));
            return player;
        }

        @Override
        public void onRemoved(Player player) {
            //TODO!!!
            //CommonUtil.callEvent(new HideFramedMapEvent(player, info.itemFrame));
        }
    };

    public MapDisplayFramedMapUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        // Enable the item frame cluster cache
        controller.itemFrameClustersByWorldEnabled = true;

        // Iterate all tracked item frames and update them
        synchronized (controller) {
            Iterator<ItemFrameInfo> itemFrames_iter = controller.itemFrames.values().iterator();
            while (itemFrames_iter.hasNext()) {
                info = itemFrames_iter.next();
                if (info.handleRemoved()) {
                    itemFrames_iter.remove();
                    continue;
                }

                info.updateItemAndViewers(synchronizer);

                // May find out it's removed during the update
                if (info.handleRemoved()) {
                    itemFrames_iter.remove();
                    continue;
                }
            }
        }

        // Update the player viewers of all map displays
        for (MapDisplayInfo map : controller.mapsValues.cloneAsIterable()) {
            map.updateViewersAndResolution();
        }

        for (ItemFrameInfo info : controller.itemFrames.values()) {
            // Resend Item Frame item (metadata) when the UUID changes
            // UUID can change when the relative tile displayed changes
            // This happens when a new item frame is placed left/above a display
            if (info.needsItemRefresh) {
                info.needsItemRefresh = false;
                info.itemFrameHandle.refreshItem();
            }
        }

        // Disable cache again and wipe
        controller.itemFrameClustersByWorldEnabled = false;
        controller.itemFrameClustersByWorld.clear();
    }
}
