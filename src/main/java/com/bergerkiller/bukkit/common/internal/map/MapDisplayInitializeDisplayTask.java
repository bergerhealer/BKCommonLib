package com.bergerkiller.bukkit.common.internal.map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.map.MapDisplay;

/**
 * Initializes a MapDisplay using a task of the owning plugin, so that this
 * shows up in timings correctly.
 */
class MapDisplayInitializeDisplayTask extends Task {
    private final MapDisplay display;
    private final ItemStack mapItem;

    public MapDisplayInitializeDisplayTask(JavaPlugin plugin, MapDisplay display, ItemStack mapItem) {
        super(plugin);
        this.display = display;
        this.mapItem = mapItem;
    }

    @Override
    public void run() {
        display.initialize(this.getPlugin(), mapItem);
    }
}
