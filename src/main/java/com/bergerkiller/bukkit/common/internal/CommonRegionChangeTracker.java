package com.bergerkiller.bukkit.common.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.events.MultiBlockChangeEvent;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionBlockChangeChunkCoordinate;
import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTracker;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Uses the Region change handler module to track large-scale block changes.
 * These changes result in the {@link MultiBlockChangeEvent} to be fired,
 * if handlers are registered.<br>
 * <br>
 * <a href="https://github.com/bergerhealer/BKCommonLib-RegionChangeTracker">Project Page</a>
 */
class CommonRegionChangeTracker extends RegionChangeTracker implements LibraryComponent {

    public CommonRegionChangeTracker(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void notifyChanges(World world, Collection<RegionBlockChangeChunkCoordinate> chunks) {
        if (CommonUtil.hasHandlers(MultiBlockChangeEvent.getHandlerList())) {
            Set<IntVector2> conv = new HashSet<IntVector2>(chunks.size());
            for (RegionBlockChangeChunkCoordinate chunk : chunks) {
                conv.add(new IntVector2(chunk.x, chunk.z));
            }
            CommonUtil.callEvent(new MultiBlockChangeEvent(world, conv));
        }
    }
}
