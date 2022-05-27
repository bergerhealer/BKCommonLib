package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.World;

/**
 * Handles region-based operations from MC 1.17 onwards
 */
class RegionHandler_Vanilla_1_17 extends RegionHandler_Vanilla_1_14 {

    @Override
    public int getMinHeight(World world) {
        com.bergerkiller.generated.org.bukkit.WorldHandle w;
        w = com.bergerkiller.generated.org.bukkit.WorldHandle.createHandle(world);
        return w.getMinHeight();
    }
}
