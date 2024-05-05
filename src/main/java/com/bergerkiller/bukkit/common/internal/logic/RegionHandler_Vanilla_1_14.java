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
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.storage.RegionFileHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Handles region-based operations from MC 1.14 onwards
 */
class RegionHandler_Vanilla_1_14 extends RegionHandlerVanilla {
    private final RegionHandlerImpl handler;

    public RegionHandler_Vanilla_1_14() {
        this.handler = Template.Class.create(RegionHandlerImpl.class, Common.TEMPLATE_RESOLVER);
    }

    private Object findRegionFileCache(World world) {
        Object pcm = WorldServerHandle.fromBukkit(world).getPlayerChunkMap().getRaw();
        Object rfc = this.handler.findRegionFileCache(pcm);
        return this.handler.findRegionFileCacheStorage(rfc);
    }

    private Object findPOIFileCache(World world) {
        Object pcm = WorldServerHandle.fromBukkit(world).getChunkProviderServer().getRaw();
        Object rfc = this.handler.findPOIFileCache(pcm);
        return this.handler.findRegionFileCacheStorage(rfc);
    }

    @Override
    public void closeStreams(World world) {
        // Close region files
        {
            Object regionFileCache = findRegionFileCache(world);
            for (Object regionFile : this.handler.findCacheRegionFileInstances(regionFileCache)) {
                RegionFileHandle.createHandle(regionFile).closeStream();
            }
        }

        // Close POI region files
        {
            Object regionFileCache = findPOIFileCache(world);
            for (Object regionFile : this.handler.findCacheRegionFileInstances(regionFileCache)) {
                RegionFileHandle.createHandle(regionFile).closeStream();
            }
        }
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        HashSet<IntVector3> regionIndices = new HashSet<IntVector3>();

        // Add all RegionFile instances in the cache
        Object regionFileCache = findRegionFileCache(world);
        regionIndices.addAll(handler.findCacheRegionFileCoordinates(regionFileCache));

        // Figure out the minimum/maximum region y coordinate
        // Since Minecraft 1.17 there can be more than one region (32 chunks) vertically
        WorldHandle worldHandle = WorldHandle.fromBukkit(world);
        int minRegionY = worldHandle.getMinBuildHeight() >> 9;
        int maxRegionY = (worldHandle.getMaxBuildHeight()-1) >> 9;

        // Obtain the region coordinates from all files in regions folder
        File regionFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        if (regionFolder != null) {
            String[] regionFileNames = regionFolder.list();
            for (String regionFileName : regionFileNames) {
                File file = new File(regionFolder, regionFileName);
                if (file.isFile() && file.exists() && file.length() >= 4096) {
                    IntVector2 coords = getRegionFileCoordinates(file);
                    if (coords != null) {
                        for (int ry = minRegionY; ry <= maxRegionY; ry++) {
                            regionIndices.add(coords.toIntVector3(ry));
                        }
                    }
                }
            }
        }

        // Look at all loaded chunks of the world and add the regions they are inside of
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector2 coords = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            for (int ry = minRegionY; ry <= maxRegionY; ry++) {
                regionIndices.add(coords.toIntVector3(ry));
            }
        }

        return regionIndices;
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        BitSet chunks = new BitSet(1024);

        Object regionFileCache = findRegionFileCache(world);
        RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(
                handler.findRegionFileAt(regionFileCache, rx, rz));

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
        Object regionFileCache = findRegionFileCache(world);
        RegionFileHandle regionFileHandle = RegionFileHandle.createHandle(
                handler.findRegionFileAt(regionFileCache, rx, rz));
        if (regionFileHandle != null) {
            return regionFileHandle.chunkExists(cx, cz);
        }

        //TODO: Optimize!
        return getRegionChunks3(world, rx, 0, rz).get((cz << 5) | cx);
    }

    @Override
    public void forceInitialization() {
        this.handler.forceInitialization();
    }

    @Template.Optional
    @Template.Import("net.minecraft.world.level.ChunkCoordIntPair")
    @Template.Import("net.minecraft.server.level.PlayerChunkMap")
    @Template.Import("net.minecraft.server.level.ChunkProviderServer")
    @Template.Import("net.minecraft.server.level.WorldServer")
    @Template.Import("net.minecraft.world.level.chunk.storage.IChunkLoader")
    @Template.Import("net.minecraft.world.level.chunk.storage.RegionFile")
    @Template.Import("net.minecraft.world.level.chunk.storage.RegionFileSection")
    @Template.Import("net.minecraft.world.level.chunk.storage.SimpleRegionStorage")
    @Template.Import("net.minecraft.world.entity.ai.village.poi.VillagePlace")
    @Template.Import("net.minecraft.world.entity.ai.village.poi.VillagePlaceSection")
    @Template.Import("it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap")
    @Template.Import("it.unimi.dsi.fastutil.longs.Long2ObjectMap")
    @Template.Import("com.bergerkiller.bukkit.common.bases.IntVector3")
    @Template.Import("it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap")
    @Template.InstanceType("net.minecraft.world.level.chunk.storage.RegionFileCache")
    public static abstract class RegionHandlerImpl extends Template.Class<Template.Handle> {

        /*
         * <FIND_REGION_FILE_CACHE>
         * public static RegionFileCache findRegionFileCache(PlayerChunkMap pcm) {
         * #if assignable RegionFileCache PlayerChunkMap
         *     // On 1.14 and PaperMC this can more trivially be accessed
         *     return (RegionFileCache) pcm;
         * #elseif exists net.minecraft.world.level.chunk.storage.IChunkLoader protected final RegionFileCache regionFileCache;
         *     // Paperspigot compatible code
         *     #require net.minecraft.world.level.chunk.storage.IChunkLoader protected final RegionFileCache regionFileCache;
         *     IChunkLoader icl = (IChunkLoader) pcm;
         *     return icl#regionFileCache;
         * #else
         *     // Access RegionFileCache inside IOWorker
         *     IChunkLoader icl = (IChunkLoader) pcm;
         *   #if version >= 1.17
         *     #require net.minecraft.world.level.chunk.storage.IChunkLoader private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;
         *   #else
         *     #require net.minecraft.world.level.chunk.storage.IChunkLoader private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:a;
         *   #endif
         *     IOWorker ioworker = icl#ioworker;
         * 
         *   #if version >= 1.17
         *     #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:storage;
         *   #elseif version >= 1.16
         *     #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:d;
         *   #else
         *     #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:e;
         *   #endif
         *     return ioworker#cache;
         * #endif
         * }
         */
        @Template.Generated("%FIND_REGION_FILE_CACHE%")
        public abstract Object findRegionFileCache(Object playerChunkMap);

        /*
         * <FIND_POI_FILE_CACHE>
         * public static RegionFileCache findPOIFileCache(ChunkProviderServer cps) {
         * #if version >= 1.18
         *     VillagePlace poi = cps.getPoiManager();
         * #elseif version >= 1.14.4
         *     VillagePlace poi = cps.j();
         * #else
         *     VillagePlace poi = cps.i();
         * #endif
         *     RegionFileSection rfs = (RegionFileSection) poi;
         * 
         * #if assignable RegionFileCache RegionFileSection
         *     // On 1.14 and PaperMC this can more trivially be accessed
         *     return (RegionFileCache) rfs;
         * #elseif version >= 1.20.5
         *     #require RegionFileSection private final SimpleRegionStorage simpleRegionStorage;
         *     SimpleRegionStorage srs = rfs#simpleRegionStorage;
         *     #require SimpleRegionStorage private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;
         *     IOWorker ioworker = srs#ioworker;
         * #else
         *     // Access RegionFileCache inside IOWorker
         *     #if version >= 1.17
         *         #require RegionFileSection private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:worker;
         *     #else
         *         #require RegionFileSection private final net.minecraft.world.level.chunk.storage.IOWorker ioworker:b;
         *     #endif
         *     IOWorker ioworker = rfs#ioworker;
         * #endif
         *
         * #if version >= 1.17
         *     #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:storage;
         * #elseif version >= 1.16
         *     #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:d;
         * #else
         *     #require net.minecraft.world.level.chunk.storage.IOWorker private final RegionFileCache cache:e;
         * #endif
         *     return ioworker#cache;
         * }
         */
        @Template.Generated("%FIND_POI_FILE_CACHE%")
        public abstract Object findPOIFileCache(Object chunkProviderServer);

        /*
         * <FIND_REGION_FILE_CACHE_STORAGE>
         * public static Long2ObjectLinkedOpenHashMap findRegionFileCache(RegionFileCache rfc) {
         * #if version >= 1.17
         *     #require net.minecraft.world.level.chunk.storage.RegionFileCache private Long2ObjectLinkedOpenHashMap<RegionFile> regionCache;
         * #else
         *     #require net.minecraft.world.level.chunk.storage.RegionFileCache private Long2ObjectLinkedOpenHashMap<RegionFile> regionCache:cache;
         * #endif
         *     return rfc#regionCache;
         * }
         */
        @Template.Generated("%FIND_REGION_FILE_CACHE_STORAGE%")
        public abstract Object findRegionFileCacheStorage(Object regionFileCache);

        /*
         * <FIND_CACHE_REGION_FILES>
         * public static Collection<RegionFile> findWorldRegionFileInstances(Long2ObjectLinkedOpenHashMap cache) {
         *     return cache.values();
         * }
         */
        @Template.Generated("%FIND_CACHE_REGION_FILES%")
        public abstract Collection<Object> findCacheRegionFileInstances(Object cache);

        /*
         * <FIND_CACHE_REGION_FILE_COORDINATES>
         * public static Collection<IntVector3> findCacheRegionFileCoordinates(Long2ObjectLinkedOpenHashMap cache) {
         *     it.unimi.dsi.fastutil.longs.LongSet coordSet;
         *     it.unimi.dsi.fastutil.longs.LongIterator iter;
         * 
         *     coordSet = cache.keySet();
         *     java.util.ArrayList result = new java.util.ArrayList(coordSet.size());
         *     iter = coordSet.iterator();
         *     while (iter.hasNext()) {
         *         long coord = iter.nextLong();
         *         int coord_x = ChunkCoordIntPair.getX(coord);
         *         int coord_z = ChunkCoordIntPair.getZ(coord);
         *         result.add(new com.bergerkiller.bukkit.common.bases.IntVector3(coord_x, 0, coord_z));
         *     }
         *     return result;
         * }
         */
        @Template.Generated("%FIND_CACHE_REGION_FILE_COORDINATES%")
        public abstract Collection<IntVector3> findCacheRegionFileCoordinates(Object cache);

        /*
         * <FIND_REGION_FILE_AT>
         * public static RegionFile findRegionFileAt(Long2ObjectLinkedOpenHashMap cache, int rx, int rz) {
         * #if version >= 1.18
         *     long coord = ChunkCoordIntPair.asLong(rx, rz);
         * #else
         *     long coord = ChunkCoordIntPair.pair(rx, rz);
         * #endif
         *     return (RegionFile) cache.get(coord);
         * }
         */
        @Template.Generated("%FIND_REGION_FILE_AT%")
        public abstract Object findRegionFileAt(Object cache, int rx, int rz);
    }
}
