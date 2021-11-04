package com.bergerkiller.bukkit.common.internal.map;

import java.util.Iterator;

import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;

/**
 * Automatically deletes dragged creative items from the kept cache
 * after the life expires. Avoids memory leaks.
 */
class MapDisplayCreativeDraggedMapItemCleaner extends Task {
    private final CommonMapController controller;
    
    public MapDisplayCreativeDraggedMapItemCleaner(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        synchronized (controller) {
            if (!controller.creativeDraggedMapItems.isEmpty()) {
                Iterator<CreativeDraggedMapItem> iter = controller.creativeDraggedMapItems.values().iterator();
                while (iter.hasNext()) {
                    if ((iter.next().life -= CreativeDraggedMapItem.CACHED_ITEM_CLEAN_INTERVAL) <= 0) {
                        iter.remove();
                    }
                }
            }
        }
    }
}
