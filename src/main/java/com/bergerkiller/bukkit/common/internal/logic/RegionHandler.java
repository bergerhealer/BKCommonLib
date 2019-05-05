package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.util.BitSet;
import java.util.Set;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;

public abstract class RegionHandler {
    public static final RegionHandler INSTANCE;

    static {
        if (Common.evaluateMCVersion(">=", "1.14")) {
            INSTANCE = new RegionHandler_1_14();
        } else {
            INSTANCE = new RegionHandler_1_8();
        }
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

    /**
     * Closes all the open files for a world, so that the files can be
     * moved or deleted on the filesystem with no problems.
     * 
     * @param world
     */
    public abstract void closeStreams(World world);

    /**
     * Gets all region indices for loadable regions of a world
     * 
     * @param world
     * @return region indices
     */
    public abstract Set<IntVector2> getRegions(World world);

    /**
     * Gets a bitset of length 1024 containing a True/False of which chunks
     * in a region exists.
     * 
     * @param world
     * @param rx - region X-coordinate
     * @param rz - region Z-coordinate
     * @return bitset of all chunks in a region that exist
     */
    public abstract BitSet getRegionChunks(World world, int rx, int rz);
}
