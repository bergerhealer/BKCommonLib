package com.bergerkiller.bukkit.common.internal.map;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;

/**
 * Refreshes the input state of maps every tick, when input is intercepted
 */
class MapDisplayInputUpdater extends Task {
    private final CommonMapController controller;

    public MapDisplayInputUpdater(JavaPlugin plugin, CommonMapController controller) {
        super(plugin);
        this.controller = controller;
    }

    @Override
    public void run() {
        Iterator<Map.Entry<Player, MapPlayerInput>> iter = controller.playerInputs.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Player, MapPlayerInput> entry = iter.next();
            if (entry.getKey().isOnline()) {
                entry.getValue().onTick();
            } else {
                entry.getValue().onDisconnected();
                iter.remove();
            }
        }
    }
}
