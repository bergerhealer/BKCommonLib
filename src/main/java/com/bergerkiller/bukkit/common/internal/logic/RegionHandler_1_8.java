package com.bergerkiller.bukkit.common.internal.logic;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.RegionFileHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;

/**
 * Handles region-based operations from MC 1.8 to MC 1.13.2
 */
public class RegionHandler_1_8 extends RegionHandler {
    private final Class<?> regionFileCacheType = CommonUtil.getNMSClass("RegionFileCache");

    @SuppressWarnings("unchecked")
    private Map<File, Object> getCache() {
        Map<File, Object> cache = SafeField.get(regionFileCacheType, "cache", Map.class);
        if (cache == null) {
            cache = SafeField.get(regionFileCacheType, "a", Map.class);
        }
        if (cache == null) {
            throw new IllegalStateException("Failed to find RegionFileCache cache field");
        }
        return cache;
    }

    @Override
    public void closeStreams(World world) {
        synchronized (regionFileCacheType) {
            try {
                Map<File, Object> cache = getCache();

                String worldPart = "." + File.separator + world.getName();
                Iterator<Entry<File, Object>> iter = cache.entrySet().iterator();
                Entry<File, Object> entry;
                while (iter.hasNext()) {
                    entry = iter.next();
                    if (!entry.getKey().toString().startsWith(worldPart) || entry.getValue() == null) {
                        continue;
                    }
                    try {
                        RegionFileHandle.createHandle(entry.getValue()).close();
                        iter.remove();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                Logging.LOGGER.log(Level.WARNING, "Exception while closing streams for '" + world.getName() + "'!");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Set<IntVector2> getRegions(World world) {
        // Obtain the region file names
        Set<File> regionFiles = new HashSet<File>();
        File regionFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        if (regionFolder != null) {
            String[] regionFileNames = regionFolder.list();
            for (String regionFileName : regionFileNames) {
                File file = new File(regionFolder, regionFileName);
                if (file.isFile() && file.exists() && file.length() >= 4096) {
                    regionFiles.add(file);
                }
            }
        }

        // Detect any addition Region Files in the cache that are not yet saved
        // Synchronized, since we are going to iterate the files here...unsafe not to do so!
        synchronized (regionFileCacheType) {
            for (File regionFile : getCache().keySet()) {
                if (regionFile != null && regionFile.getParentFile().equals(regionFolder)) {
                    regionFiles.add(regionFile);
                }
            }
        }

        // Parse all found files into the region x and z coordinates
        HashSet<IntVector2> regionIndices = new HashSet<IntVector2>();
        for (File file : regionFiles) {
            IntVector2 coords = getRegionFileCoordinates(file);
            if (coords != null) {
                regionIndices.add(coords);
            }
        }

        // Look at all loaded chunks of the world and add the regions they are inside of
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector2 coords = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            if (!regionIndices.contains(coords)) {
                regionIndices.add(coords);
            }
        }

        return regionIndices;
    }

    @Override
    public BitSet getRegionChunks(World world, int rx, int rz) {
        BitSet chunks = new BitSet(1024);
        File regionFile = getRegionFile(world, rx, rz);
        synchronized (regionFileCacheType) {
            Map<File, Object> cache = this.getCache();
            RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(cache.get(regionFile));
            if (regionFileHandle == null) {
                // Start a new file stream to read the coordinates
                // Creating a new region file is too slow and results in memory leaks
                try {
                    DataInputStream stream = new DataInputStream(new FileInputStream(regionFile));
                    try {
                        for (int coordIndex = 0; coordIndex < 1024; coordIndex++) {
                            if (stream.readInt() > 0) {
                                chunks.set(coordIndex);
                            }
                        }
                    } finally {
                        stream.close();
                    }
                } catch (IOException ex) {
                }
            } else {
                // Use the RegionFile instance to find out what chunks exist
                int coordIndex = 0;
                int cx, cz;
                for (cz = 0; cz < 32; cz++) {
                    for (cx = 0; cx < 32; cx++) {
                        if (regionFileHandle.chunkExists(cx, cz)) {
                            chunks.set(coordIndex);
                        }
                        coordIndex++;
                    }
                }
            }
        }

        return chunks;
    }

}
