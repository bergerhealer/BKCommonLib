package com.bergerkiller.bukkit.common.internal.mounting;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * Used on MC 1.17 and later, where the vehicle exit bug of 1.16 was patched again.
 */
public class VehicleMountHandler_1_17 extends VehicleMountHandler_1_9_to_1_15_2 {

    public VehicleMountHandler_1_17(CommonPlugin plugin, Player player) {
        super(plugin, player);
    }
}
