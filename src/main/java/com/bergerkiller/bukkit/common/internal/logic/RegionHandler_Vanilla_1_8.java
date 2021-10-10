package com.bergerkiller.bukkit.common.internal.logic;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
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
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.storage.RegionFileHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Handles region-based operations from MC 1.8 to MC 1.13.2
 */
public class RegionHandler_Vanilla_1_8 extends RegionHandlerVanilla {
    private final Class<?> regionFileCacheType = CommonUtil.getClass("net.minecraft.world.level.chunk.storage.RegionFileCache");
    private final FastMethod<Boolean> chunkExists = new FastMethod<Boolean>();
    private final FastField<Map<File, Object>> cacheField = new FastField<Map<File, Object>>();

    public RegionHandler_Vanilla_1_8() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.ChunkProviderServer");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);

        // chunkExists generated method
        {
            String source = SourceDeclaration.preprocess(
                    "public static boolean chunkExists(ChunkProviderServer cps, int cx, int cz) {\n" +
                    "    #require net.minecraft.server.level.ChunkProviderServer private final IChunkLoader chunkLoader;\n" +
                    "    IChunkLoader loader = cps#chunkLoader;\n" +
                    "    if (loader instanceof ChunkRegionLoader) {\n" +
                    "        ChunkRegionLoader crl = (ChunkRegionLoader) loader;\n" +
                    "#if version >= 1.12\n" +
                    "        return crl.chunkExists(cx, cz);\n" +
                    "#elseif version >= 1.11.2\n" +
                    "        return crl.a(cx, cz);\n" +
                    "#else\n" +
                    "        return crl.chunkExists(cps.world, cx, cz);\n" +
                    "#endif\n" +
                    "    } else {\n" +
                    "        return false;\n" +
                    "    }\n" +
                    "}" ,resolver);
            MethodDeclaration chunkExistsMethod = new MethodDeclaration(resolver, source);
            chunkExists.init(chunkExistsMethod);
        }

        // cache map static field
        try {
            Field field;
            try {
                field = regionFileCacheType.getDeclaredField("cache");
            } catch (Throwable t) {
                field = regionFileCacheType.getDeclaredField("a");
            }
            this.cacheField.init(field);
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }

    private Map<File, Object> getCache() {
        Map<File, Object> cache = this.cacheField.get(null);
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
                        RegionFileHandle.createHandle(entry.getValue()).closeStream();
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
    public Set<IntVector3> getRegions3(World world) {
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
        HashSet<IntVector3> regionIndices = new HashSet<IntVector3>();
        for (File file : regionFiles) {
            IntVector2 regionFileCoordinates = getRegionFileCoordinates(file);
            if (regionFileCoordinates == null) {
                continue;
            }
            IntVector3 coords = regionFileCoordinates.toIntVector3(0);
            if (coords != null) {
                regionIndices.add(coords);
            }
        }

        // Look at all loaded chunks of the world and add the regions they are inside of
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector3 coords = new IntVector3(chunk.getX() >> 5, 0, chunk.getZ() >> 5);
            regionIndices.add(coords);
        }

        return regionIndices;
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
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

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        Object cps = WorldServerHandle.T.getChunkProviderServer.raw.invoke(HandleConversion.toWorldHandle(world));
        return chunkExists.invoke(null, cps, cx, cz);
    }

    @Override
    public void forceInitialization() {
        this.chunkExists.forceInitialization();
        this.cacheField.forceInitialization();
    }
}
