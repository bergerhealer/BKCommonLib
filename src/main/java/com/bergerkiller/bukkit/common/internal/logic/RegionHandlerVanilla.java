package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Vanilla Minecraft region file behavior
 */
public abstract class RegionHandlerVanilla extends RegionHandler {

    @Override
    public boolean isSupported(World world) {
        return true;
    }

    protected IntVector3 getRegionFileCoordinates(File regionFile) {
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
            return new IntVector3(rx, 0, rz);
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
