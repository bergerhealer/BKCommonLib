package com.bergerkiller.bukkit.common.internal.logic;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.PlayerChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.storage.RegionFileHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Handles region-based operations from MC 1.15 to MC 1.17
 */
public class RegionHandler_Vanilla_1_15 extends RegionHandlerVanilla {
    private final FastMethod<Object> findRegionFileCache = new FastMethod<Object>();
    private final FastMethod<Collection<Object>> findCacheRegionFileInstances = new FastMethod<Collection<Object>>();
    private final FastMethod<Collection<IntVector3>> findCacheRegionFileCoordinates = new FastMethod<Collection<IntVector3>>();
    private final FastMethod<Object> findRegionFileAt = new FastMethod<Object>();

    public RegionHandler_Vanilla_1_15() {
        ClassResolver resolver = new ClassResolver();
        resolver.addImport("net.minecraft.world.level.ChunkCoordIntPair");
        resolver.addImport("net.minecraft.server.level.PlayerChunkMap");
        resolver.addImport("net.minecraft.server.level.ChunkProviderServer");
        resolver.addImport("net.minecraft.server.level.WorldServer");
        resolver.addImport("net.minecraft.world.level.chunk.storage.IChunkLoader");
        resolver.addImport("net.minecraft.world.level.chunk.storage.RegionFile");
        resolver.setDeclaredClassName("net.minecraft.world.level.chunk.storage.RegionFileCache");
        resolver.setVariable("version", Common.MC_VERSION);

        // Initialize runtime generated method to obtain the RegionFileCache cache map instance of a World
        // This is slightly different on PaperSpigot, where they changed the IChunkLoader to extend RegionFileCache, rather than adding a field
        Class<?> t_RegionFileCache = CommonUtil.getClass("net.minecraft.world.level.chunk.storage.RegionFileCache");
        if (t_RegionFileCache.isAssignableFrom(PlayerChunkMapHandle.T.getType())) {
            // PaperMC
            MethodDeclaration findRegionFileCacheMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public static it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap findRegionFileCache(WorldServer world) {\n" +
                    "    ChunkProviderServer cps = world.getChunkProvider();\n" +
                    "#if version >= 1.17\n" +
                    "    PlayerChunkMap pcm = cps.chunkMap;\n" +
                    "#else\n" +
                    "    PlayerChunkMap pcm = cps.playerChunkMap;\n" +
                    "#endif\n" +
                    "    RegionFileCache rfc = (RegionFileCache) pcm;\n" +
                    "#if version >= 1.17\n" +
                    "    return rfc.regionCache;\n" +
                    "#else\n" +
                    "    return rfc.cache;\n" +
                    "#endif\n" +
                    "}", resolver));
            findRegionFileCache.init(findRegionFileCacheMethod);
        } else {
            // Spigot/CraftBukkit/vanilla NMS
            MethodDeclaration findRegionFileCacheMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public static it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap findRegionFileCache(WorldServer world) {\n" +
                    "    ChunkProviderServer cps = world.getChunkProvider();\n" +
                    "#if version >= 1.17\n" +
                    "    PlayerChunkMap pcm = cps.chunkMap;\n" +
                    "#else\n" +
                    "    PlayerChunkMap pcm = cps.playerChunkMap;\n" +
                    "#endif\n" +
                    "    IChunkLoader icl = (IChunkLoader) pcm;\n" +
                    "#if exists net.minecraft.world.level.chunk.storage.IChunkLoader protected final RegionFileCache regionFileCache;\n" +
                    /*   Paperspigot compatible code  */
                    "    #require net.minecraft.world.level.chunk.storage.IChunkLoader protected final RegionFileCache regionFileCache;\n" +
                    "    RegionFileCache rfc = icl#regionFileCache;\n" +
                    "#else\n" +
                    /*   Normal Spigot code */
                    "  #if version >= 1.17\n" +
                    "    #require net.minecraft.world.level.chunk.storage.IChunkLoader private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;\n" +
                    "  #else\n" +
                    "    #require net.minecraft.world.level.chunk.storage.IChunkLoader private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:a;\n" +
                    "  #endif\n" +
                    "    IOWorker ioworker = icl#ioworker;\n" +
                    "  #if version >= 1.17\n" +
                    "    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:storage;\n" +
                    "  #elseif version >= 1.16\n" +
                    "    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:d;\n" +
                    "  #else\n" +
                    "    #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:e;\n" +
                    "  #endif\n" +
                    "    RegionFileCache rfc = ioworker#cache;\n" +
                    "#endif\n" +
                    "#if version >= 1.17\n" +
                    "    return rfc.regionCache;\n" +
                    "#else\n" +
                    "    return rfc.cache;\n" +
                    "#endif\n" +
                    "}", resolver));
            findRegionFileCache.init(findRegionFileCacheMethod);
        }

        // Initialize runtime generated method to obtain the RegionFile instances of a World
        {
            MethodDeclaration findCacheRegionFileInstancesMethod = new MethodDeclaration(resolver,
                    "public static Collection<RegionFile> findWorldRegionFileInstances(it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap cache) {\n" +
                    "    return cache.values();\n" +
                    "}");
            findCacheRegionFileInstances.init(findCacheRegionFileInstancesMethod);
        }

        // Initialize method to obtain all the region coordinates of regions loaded
        {
            MethodDeclaration findCacheRegionFileCoordinatesMethod = new MethodDeclaration(resolver,
                    "public static Collection<com.bergerkiller.bukkit.common.bases.IntVector3> " +
                    "findWorldRegionFileInstances(it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap cache) {\n" +
                    "    it.unimi.dsi.fastutil.longs.LongSet coordSet;\n" +
                    "    it.unimi.dsi.fastutil.longs.LongIterator iter;\n" +
                    "\n" +
                    "    coordSet = cache.keySet();\n" +
                    "    java.util.ArrayList result = new java.util.ArrayList(coordSet.size());\n" +
                    "    iter = coordSet.iterator();\n" +
                    "    while (iter.hasNext()) {\n" +
                    "        long coord = iter.nextLong();\n" +
                    "        int coord_x = ChunkCoordIntPair.getX(coord);\n" +
                    "        int coord_z = ChunkCoordIntPair.getZ(coord);\n" +
                    "        result.add(new com.bergerkiller.bukkit.common.bases.IntVector3(coord_x, 0, coord_z));\n" +
                    "    }\n" +
                    "    return result;\n" +
                    "}");
            findCacheRegionFileCoordinates.init(findCacheRegionFileCoordinatesMethod);
        }

        // Initialize method to obtain a RegionFile instance at a given region x/z from the cache
        {
            MethodDeclaration findRegionFileAtMethod = new MethodDeclaration(resolver,
                    "public static RegionFile findRegionFileAt(it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap cache, int rx, int rz) {\n" +
                    "    long coord = ChunkCoordIntPair.pair(rx, rz);\n" +
                    "    return (RegionFile) cache.get(coord);\n" +
                    "}");
            findRegionFileAt.init(findRegionFileAtMethod);
        }
    }

    @Override
    public void closeStreams(World world) {
        Object regionFileCache = findRegionFileCache.invoke(null, HandleConversion.toWorldHandle(world));
        for (Object regionFile : findCacheRegionFileInstances.invoke(null, regionFileCache)) {
            RegionFileHandle.createHandle(regionFile).closeStream();
        }
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        HashSet<IntVector3> regionIndices = new HashSet<IntVector3>();

        // Add all RegionFile instances in the cache
        Object regionFileCache = findRegionFileCache.invoke(null, HandleConversion.toWorldHandle(world));
        regionIndices.addAll(findCacheRegionFileCoordinates.invoke(null, regionFileCache));

        // Obtain the region coordinates from all files in regions folder
        File regionFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        if (regionFolder != null) {
            String[] regionFileNames = regionFolder.list();
            for (String regionFileName : regionFileNames) {
                File file = new File(regionFolder, regionFileName);
                if (file.isFile() && file.exists() && file.length() >= 4096) {
                    IntVector3 coords = getRegionFileCoordinates(file);
                    if (coords != null && !regionIndices.contains(coords)) {
                        regionIndices.add(coords);
                    }
                }
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

        Object regionFileCache = findRegionFileCache.invoke(null, HandleConversion.toWorldHandle(world));
        RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(findRegionFileAt.invoke(null,
                regionFileCache, rx, rz));

        if (regionFileHandle == null) {
            File regionFile = getRegionFile(world, rx, rz);
            if (regionFile.exists()) {
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

        return chunks;
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        int rx = cx >> 5;
        int rz = cz >> 5;
        cx &= 0x1F;
        cz &= 0x1F;

        // Try checking if a RegionFile instance is available. If so, use that.
        Object regionFileCache = findRegionFileCache.invoke(null, HandleConversion.toWorldHandle(world));
        RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(findRegionFileAt.invoke(null,
                regionFileCache, rx, rz));
        if (regionFileHandle != null) {
            return regionFileHandle.chunkExists(cx, cz);
        }

        //TODO: Optimize!
        return getRegionChunks3(world, rx, 0, rz).get((cz << 5) | cx);
    }

    @Override
    public void forceInitialization() {
        this.findRegionFileCache.forceInitialization();
        this.findCacheRegionFileCoordinates.forceInitialization();
        this.findCacheRegionFileInstances.forceInitialization();
        this.findRegionFileAt.forceInitialization();
    }
}
