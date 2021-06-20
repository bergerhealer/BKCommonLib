package com.bergerkiller.bukkit.common.internal.logic;

import java.util.BitSet;
import java.util.Set;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Selects the most appropriate region handler for a world, then
 * forwards calls to that handler.
 */
public final class RegionHandlerSelector extends RegionHandler {
    private final RegionHandler fallback;
    private final RegionHandler cubicchunks;

    public RegionHandlerSelector() {
        // Vanilla fallback
        if (Common.evaluateMCVersion(">=", "1.15")) {
            fallback = LogicUtil.tryCreate(RegionHandler_Vanilla_1_15::new, RegionHandlerDisabled::new);
        } else if (Common.evaluateMCVersion(">=", "1.14")) {
            fallback = LogicUtil.tryCreate(RegionHandler_Vanilla_1_14::new, RegionHandlerDisabled::new);
        } else {
            fallback = LogicUtil.tryCreate(RegionHandler_Vanilla_1_8::new, RegionHandlerDisabled::new);
        }

        // Cubic chunks existence check, then initialize
        if (CommonUtil.getClass("io.github.opencubicchunks.cubicchunks.api.world.ICube") != null) {
            cubicchunks = LogicUtil.tryCreate(RegionHandler_CubicChunks_1_12_2::new, RegionHandlerDisabled::new);
        } else {
            cubicchunks = new RegionHandlerDisabled(null);
        }
    }

    @Override
    public boolean isSupported(World world) {
        return getHandler(world).isSupported(world);
    }

    @Override
    public void forceInitialization() {
        this.fallback.forceInitialization();
        if (!(this.cubicchunks instanceof RegionHandlerDisabled)) {
            this.cubicchunks.forceInitialization();
        }
    }

    private RegionHandler getHandler(World world) {
        if (cubicchunks.isSupported(world)) {
            return cubicchunks;
        } else {
            return fallback;
        }
    }

    @Override
    public void closeStreams(World world) {
        getHandler(world).closeStreams(world);
    }

    @Override
    public Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        return getHandler(world).getRegions3ForXZ(world, regionXZCoordinates);
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        return getHandler(world).getRegions3(world);
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        return getHandler(world).getRegionChunks3(world, rx, ry, rz);
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        return getHandler(world).isChunkSaved(world, cx, cz);
    }
}
