package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;

public class RegionHandler_CubicChunks_1_12_2 extends RegionHandler {
    private final CubicChunksHandle handle;
    private int forRegion_base_cx;
    private int forRegion_base_cy;
    private int forRegion_base_cz;
    private final Object forRegionCallbackListChunks;

    public RegionHandler_CubicChunks_1_12_2() throws Throwable {
        handle = Template.Class.create(CubicChunksHandle.class, Common.TEMPLATE_RESOLVER);

        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            protected Invoker<?> getCallback(Method method) {
                if (method.getName().equals("apply")) {
                    return new Invoker<Object>() {
                        @Override
                        public Object invokeVA(Object instance, Object... args) {
                            return handle.listRegionChunkXZ(args[0],
                                    forRegion_base_cx,
                                    forRegion_base_cy,
                                    forRegion_base_cz);
                        }
                    };
                } else {
                    return null;
                }
            }
        };
        forRegionCallbackListChunks = interceptor.createInstance(Class.forName(
                "cubicchunks.regionlib.util.CheckedFunction"));
    }

    @Override
    public boolean isSupported(World world) {
        return handle.isSupported(HandleConversion.toWorldHandle(world));
    }

    @Override
    public void forceInitialization() {
        handle.forceInitialization();
    }

    @Override
    public void closeStreams(World world) {
    }

    @Override
    public Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        // First try using the ICubicStorage API, if available
        {
            Object worldHandle = HandleConversion.toWorldHandle(world);
            Object chunkProviderServer = WorldServerHandle.T.getChunkProviderServer.raw.invoke(worldHandle);
            final Set<IntVector3> regionIndices = new HashSet<>();
            if (handle.forEachCube(chunkProviderServer, wrap(cubeCoordinate -> {
                IntVector2 regionXZ = new IntVector2(cubeCoordinate.x >> 5, cubeCoordinate.z >> 5);
                if (regionXZCoordinates.contains(regionXZ)) {
                    regionIndices.add(regionXZ.toIntVector3(cubeCoordinate.y >> 5));
                }
            }))) {
                return regionIndices;
            }
        }

        // Obtain the coordinates using the files stored on disk
        Set<IntVector3> regionIndices = getWorldRegionFileCoordinates(world, c -> {
            return regionXZCoordinates.contains(c.toIntVector2());
        });

        // Look at all loaded chunks and their cubes of the world and add the regions they are inside of
        for (Chunk chunk : world.getLoadedChunks()) {
            // Check region is filtered
            IntVector2 region = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            if (!regionXZCoordinates.contains(region)) {
                continue;
            }

            List<Integer> cubes_y = handle.getLoadedCubesY(HandleConversion.toChunkHandle(chunk));
            for (Integer y : cubes_y) {
                regionIndices.add(region.toIntVector3(y.intValue() >> 5));
            }
        }

        return regionIndices;
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        // First try using the ICubicStorage API, if available
        {
            Object worldHandle = HandleConversion.toWorldHandle(world);
            Object chunkProviderServer = WorldServerHandle.T.getChunkProviderServer.raw.invoke(worldHandle);
            final Set<IntVector3> regionIndices = new HashSet<>();
            if (handle.forEachCube(chunkProviderServer, wrap(cubeCoordinate -> {
                regionIndices.add(new IntVector3(cubeCoordinate.x >> 5, cubeCoordinate.y >> 5, cubeCoordinate.z >> 5));
            }))) {
                return regionIndices;
            }
        }

        // Fallback for older CubicChunks versions
        // Obtain the coordinates using the files stored on disk
        Set<IntVector3> regionIndices = getWorldRegionFileCoordinates(world, c -> true);

        // Look at all loaded chunks and their cubes of the world and add the regions they are inside of
        for (Chunk chunk : world.getLoadedChunks()) {
            IntVector2 region = new IntVector2(chunk.getX() >> 5, chunk.getZ() >> 5);
            List<Integer> cubes_y = handle.getLoadedCubesY(HandleConversion.toChunkHandle(chunk));
            for (Integer y : cubes_y) {
                regionIndices.add(region.toIntVector3(y.intValue() >> 5));
            }
        }

        return regionIndices;
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        // Coordinates 32x32x32 space -> cube space
        int base_cx = rx << 5;
        int base_cy = ry << 5;
        int base_cz = rz << 5;

        BitSet chunks = new BitSet();
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object chunkProviderServer = WorldServerHandle.T.getChunkProviderServer.raw.invoke(worldHandle);
        List<Object> regionProviderList = handle.getRegionProviders(chunkProviderServer);
        if (regionProviderList != null) {
            // Region format and we can efficiently query the chunks inside a region
            for (Object regionProvider : regionProviderList) {
                // Got to check all 8 16x16x16 areas inside this region
                applyChunksToBitset(chunks, regionProvider, base_cx,    base_cy,      base_cz);
                applyChunksToBitset(chunks, regionProvider, base_cx+16, base_cy,      base_cz);
                applyChunksToBitset(chunks, regionProvider, base_cx,    base_cy,      base_cz+16);
                applyChunksToBitset(chunks, regionProvider, base_cx+16, base_cy,      base_cz+16);
                applyChunksToBitset(chunks, regionProvider, base_cx,    base_cy + 16, base_cz);
                applyChunksToBitset(chunks, regionProvider, base_cx+16, base_cy + 16, base_cz);
                applyChunksToBitset(chunks, regionProvider, base_cx,    base_cy + 16, base_cz+16);
                applyChunksToBitset(chunks, regionProvider, base_cx+16, base_cy + 16, base_cz+16);
            }
        } else {
            // Fallback that depends heavily on the chunkExists API of a custom ICubicStorage provider
            for (int rel_cz = 0; rel_cz < 32; rel_cz++) {
                for (int rel_cx = 0; rel_cx < 32; rel_cx++) {
                    int cx = base_cx + rel_cx;
                    int cz = base_cz + rel_cz;
                    boolean regionHasCubes = false;
                    for (int rel_cy = 0; rel_cy < 32; rel_cy++) {
                        if (handle.cubeExists(chunkProviderServer, cx, base_cy + rel_cy, cz)) {
                            regionHasCubes = true;
                            break;
                        }
                    }
                    if (regionHasCubes) {
                        int index = (rel_cz << 5) | rel_cx;
                        chunks.set(index);
                    }
                }
            }
        }

        return chunks;
    }

    private void applyChunksToBitset(BitSet chunks, Object regionProvider, int base_cx, int base_cy, int base_cz) {
        java.util.Optional<BitSet> region_chunks_opt = forRegion(regionProvider, base_cx, base_cy, base_cz, forRegionCallbackListChunks);
        if (!region_chunks_opt.isPresent()) {
            return;
        }

        // Only interested in 0-31, so only the +16 offset is important
        base_cx &= 0x10;
        base_cz &= 0x10;

        // Go by all 16x16 columns inside the region and set the bit if set
        BitSet region_chunks = region_chunks_opt.get();
        int region_data_index = 0;
        for (int cz = 0; cz < 16; cz++) {
            for (int cx = 0; cx < 16; cx++) {
                if (region_chunks.get(region_data_index++)) {
                    int index = ((cz + base_cz) << 5) | (cx + base_cx);
                    chunks.set(index);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T forRegion(Object regionProvider, int base_cx, int base_cy, int base_cz, Object callback) {
        forRegion_base_cx = base_cx;
        forRegion_base_cy = base_cy;
        forRegion_base_cz = base_cz;
        return (T) handle.fromExistingRegion(regionProvider, base_cx, base_cy, base_cz, callback);
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        Object worldHandle = HandleConversion.toWorldHandle(world);
        Object chunkProviderServer = WorldServerHandle.T.getChunkProviderServer.raw.invoke(worldHandle);
        return handle.columnExists(chunkProviderServer, cx, cz);
    }

    @Override
    public int getMinHeight(World world) {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getMaxHeight(World world) {
        return Integer.MAX_VALUE;
    }

    // Gets all region file coordinates stored on disk
    // Note: as of newer version of CubicChunks this is not used, since storage could be different
    protected HashSet<IntVector3> getWorldRegionFileCoordinates(World world, Predicate<IntVector3> filter) {
        Path regionPath = (new File(Common.SERVER.getWorldFolder(world.getName()), "region3d")).toPath();
        try {
            try (Stream<IntVector3> stream = Files.list(regionPath)
                    .parallel()
                    .map(path -> getRegionFileCoordinates(path.getFileName().toString()))
                    .filter(coord -> coord != null)
                    .filter(filter)
                    .sequential())
            {
                return stream.collect(Collectors.toCollection(HashSet::new));
            }
        } catch (IOException e) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to list region files of world " + world.getName(), e);
            return new HashSet<IntVector3>();
        }
    }

    protected IntVector3 getRegionFileCoordinates(String regionFileName) {
        // Parse 0.0.0.3dr
        // Step one: verify starts with r. and ends with .mca
        if (!regionFileName.endsWith(".3dr")) {
            return null;
        }

        // Find dot between coordinates
        String[] parts = regionFileName.substring(0, regionFileName.length()-4).split("\\.");
        if (parts.length != 3) {
            return null;
        }

        // Parse the two numbers as integers - should succeed
        // Note: coordinates are in 16x16x16-space, we expect 32x32x32 space
        // For this reason, divide the coordinates by 2. (shift >>1)
        try {
            int rx = Integer.parseInt(parts[0]);
            int ry = Integer.parseInt(parts[1]);
            int rz = Integer.parseInt(parts[2]);
            return new IntVector3(rx >> 1, ry >> 1, rz >> 1);
        } catch (Exception ex) {
        }
        return null;
    }

    private CubePosConsumerAdapter wrap(Consumer<IntVector3> consumer) {
        return new CubePosConsumerAdapter(consumer);
    }

    public final class CubePosConsumerAdapter implements Consumer<Object> {
        private final Consumer<IntVector3> consumer;

        private CubePosConsumerAdapter(Consumer<IntVector3> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(Object t) {
            this.consumer.accept(handle.cubePosToIntVector3(t));
        }
    }

    @Template.Optional
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.util.CubePos")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.IColumn")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.ICube")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.server.chunkio.ICubeIO")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeIO")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeStorage")
    @Template.Import("cubicchunks.regionlib.api.region.IRegionProvider")
    @Template.Import("cubicchunks.regionlib.impl.save.SaveSection3D")
    @Template.Import("cubicchunks.regionlib.impl.SaveCubeColumns")
    @Template.Import("cubicchunks.regionlib.impl.EntryLocation3D")
    @Template.Import("com.bergerkiller.bukkit.common.bases.IntVector3")
    @Template.InstanceType("net.minecraft.world.level.World")
    public static abstract class CubicChunksHandle extends Template.Class<Template.Handle> {

        /*
         * <IS_SUPPORTED>
         * public static boolean isSupported(net.minecraft.server.level.WorldServer world) {
         *     return world instanceof ICubicWorld && ((ICubicWorld) world).isCubicWorld();
         * }
         */
        @Template.Generated("%IS_SUPPORTED%")
        public abstract boolean isSupported(Object worldHandle);

        /*
         * <GET_LOADED_CUBES_Y>
         * public static java.util.List<Integer> getLoadedCubesY(IColumn chunk) {
         *     java.util.List result = new java.util.ArrayList();
         *     java.util.Iterator iter = chunk.getLoadedCubes().iterator();
         *     while (iter.hasNext()) {
         *         ICube cube = (ICube) iter.next();
         *         result.add(Integer.valueOf(cube.getY()));
         *     }
         *     return result;
         * }
         */
        @Template.Generated("%GET_LOADED_CUBES_Y%")
        public abstract List<Integer> getLoadedCubesY(Object chunkHandle);

        /*
         * <LIST_REGION_CHUNKS_XZ>
         * public static Object listRegionChunkXZ(cubicchunks.regionlib.api.region.IRegion region, int base_cx, int base_cy, int base_cz) {
         *     java.util.BitSet chunks_xz = new java.util.BitSet(256);
         *     int dataIndex = 0;
         *     for (int cz = 0; cz < 16; cz++) {
         *         for (int cx = 0; cx < 16; cx++) {
         *             for (int cy = 0; cy < 16; cy++) {
         *                 EntryLocation3D key = new EntryLocation3D(base_cx+cx, base_cy+cy, base_cz+cz);
         *                 if (region.hasValue(key)) {
         *                     chunks_xz.set(dataIndex);
         *                     break;
         *                 }
         *             }
         *             dataIndex++;
         *         }
         *     }
         *     return chunks_xz;
         * }
         */
        @Template.Generated("%LIST_REGION_CHUNKS_XZ%")
        public abstract Object listRegionChunkXZ(Object region, int base_cx, int base_cy, int base_cz);

        /*
         * <FROM_EXISTING_REGION>
         * public static java.util.Optional<Object> fromExistingRegion(cubicchunks.regionlib.api.region.IRegionProvider regionProvider, int cx, int cy, int cz, cubicchunks.regionlib.util.CheckedFunction checkedFunction) {
         *     EntryLocation3D key = new EntryLocation3D(cx, cy, cz);
         *     return regionProvider.fromExistingRegion(key, checkedFunction);
         * }
         */
        @SuppressWarnings("rawtypes")
        @Template.Generated("%FROM_EXISTING_REGION%")
        public abstract java.util.Optional fromExistingRegion(Object regionProvider, int cx, int cy, int cz, Object checkedFunction);

        /*
         * <GET_REGION_PROVIDERS>
         * public static List<Object> forRegion(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider) {
         *     ICubeIO cubeIO = provider.getCubeIO();
         * 
         * #if exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage
         *     // ICubicStorage API
         *     if (!(cubeIO instanceof AsyncBatchingCubeIO)) {
         *         return null; // signal not supported
         *     }
         *     ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();
         *     if (!(storage instanceof RegionCubeStorage)) {
         *         return null; // signal not supported
         *     }
         *     RegionCubeStorage regionCubeStorage = (RegionCubeStorage) storage;
         *     #require io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeStorage private cubicchunks.regionlib.impl.SaveCubeColumns save;
         *     SaveCubeColumns columns = regionCubeStorage#save;
         * #else
         *     // Legacy
         *     RegionCubeIO regionCubeIO = (RegionCubeIO) cubeIO;
         *     #require RegionCubeIO private SaveCubeColumns getSave();
         *     SaveCubeColumns columns = regionCubeIO#getSave();
         * #endif
         * 
         *     SaveSection3D saveSection = columns.getSaveSection3D();
         *     #require cubicchunks.regionlib.api.storage.SaveSection private final java.util.List<IRegionProvider> regionProviders;
         *     return saveSection#regionProviders;
         * }
         */
        @Template.Generated("%GET_REGION_PROVIDERS%")
        public abstract List<Object> getRegionProviders(Object chunkProviderServer);

        /*
         * <CUBEPOS_TO_INTVECTOR3>
         * public static IntVector3 cubePosToIntVector3(CubePos pos) {
         *     return new IntVector3(pos.getX(), pos.getY(), pos.getZ());
         * }
         */
        @Template.Generated("%CUBEPOS_TO_INTVECTOR3%")
        public abstract IntVector3 cubePosToIntVector3(Object pos);

        /*
         * <FOR_EACH_CUBE>
         * public static boolean forEachCube(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider, java.util.function.Consumer callback) {
         * #if !exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage
         *     return false;
         * #elseif !exists io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO
         *     return false;
         * #else
         *     ICubeIO cubeIO = provider.getCubeIO();
         *     if (!(cubeIO instanceof AsyncBatchingCubeIO)) {
         *         return false;
         *     }
         * 
         *     ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();
         *     try {
         *         storage.forEachCube(callback);
         *         return true;
         *     } catch (java.io.IOException ex) {
         *         ex.printStackTrace();
         *         return false;
         *     }
         * #endif
         * }
         */
        @Template.Generated("%FOR_EACH_CUBE%")
        public abstract boolean forEachCube(Object chunkProviderServer, CubePosConsumerAdapter callback);

        /*
         * <COLUMN_EXISTS>
         * public static boolean columnExists(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider, int cx, int cz) {
         * #if !exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage
         *     return false;
         * #elseif !exists io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO
         *     return false;
         * #else
         *     ICubeIO cubeIO = provider.getCubeIO();
         *     if (!(cubeIO instanceof AsyncBatchingCubeIO)) {
         *         return false;
         *     }
         * 
         *     ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();
         *     return storage.columnExists(new net.minecraft.world.level.ChunkCoordIntPair(cx, cz));
         * #endif
         * }
         */
        @Template.Generated("%COLUMN_EXISTS%")
        public abstract boolean columnExists(Object chunkProviderServer, int cx, int cz);

        /*
         * <CUBE_EXISTS>
         * public static boolean columnExists(io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal.Server provider, int cx, int cy, int cz) {
         * #if !exists io.github.opencubicchunks.cubicchunks.api.world.storage.ICubicStorage
         *     return false;
         * #elseif !exists io.github.opencubicchunks.cubicchunks.core.server.chunkio.AsyncBatchingCubeIO
         *     return false;
         * #else
         *     ICubeIO cubeIO = provider.getCubeIO();
         *     if (!(cubeIO instanceof AsyncBatchingCubeIO)) {
         *         return false;
         *     }
         * 
         *     ICubicStorage storage = ((AsyncBatchingCubeIO) cubeIO).getStorage();
         *     return storage.cubeExists(new CubePos(cx, cy, cz));
         * #endif
         * }
         */
        @Template.Generated("%CUBE_EXISTS%")
        public abstract boolean cubeExists(Object chunkProviderServer, int cx, int cy, int cz);
    }
}
