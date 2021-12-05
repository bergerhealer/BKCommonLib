package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;

/**
 * Vanilla Minecraft region file behavior
 */
abstract class RegionHandlerVanilla extends RegionHandler {

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return true;
    }

    @Override
    public Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        // Figure out the minimum/maximum region y coordinate
        // Since Minecraft 1.17 there can be more than one region (32 chunks) vertically
        WorldHandle worldHandle = WorldHandle.fromBukkit(world);
        int minRegionY = worldHandle.getMinBuildHeight() >> 9;
        int maxRegionY = (worldHandle.getMaxBuildHeight()-1) >> 9;

        Set<IntVector3> result = new HashSet<IntVector3>(regionXZCoordinates.size());
        for (IntVector2 coord : regionXZCoordinates) {
            for (int ry = minRegionY; ry <= maxRegionY; ry++) {
                result.add(coord.toIntVector3(ry));
            }
        }
        return result;
    }

    protected IntVector2 getRegionFileCoordinates(File regionFile) {
        String regionFileName = regionFile.getName();

        // Parse r.0.0.mca
        // Step one: verify starts with r. and ends with .mca
        if (!regionFileName.startsWith("r.") || !regionFileName.endsWith(".mca")) {
            return null;
        }

        // Find dot between coordinates
        int coord_sep_idx = regionFileName.indexOf('.', 2);
        if (coord_sep_idx == -1 || coord_sep_idx >= regionFileName.length() - 4) {
            return null;
        }

        // Parse the two numbers as integers - should succeed
        try {
            int rx = Integer.parseInt(regionFileName.substring(2, coord_sep_idx));
            int rz = Integer.parseInt(regionFileName.substring(coord_sep_idx + 1, regionFileName.length() - 4));
            return new IntVector2(rx, rz);
        } catch (Exception ex) {
        }
        return null;
    }

    protected File getRegionFile(World world, int rx, int rz) {
        File regionsFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        StringBuilder fileName = new StringBuilder();
        fileName.append("r.").append(rx).append('.').append(rz).append(".mca");
        return new File(regionsFolder, fileName.toString());
    }
}
