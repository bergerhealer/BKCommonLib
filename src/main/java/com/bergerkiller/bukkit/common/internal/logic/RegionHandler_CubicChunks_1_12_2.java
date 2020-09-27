package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;
import java.lang.reflect.Method;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
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
    public Set<IntVector3> getRegions3(World world) {
        // Obtain the region file names
        Set<File> regionFiles = new HashSet<File>();
        File regionFolder = getRegionFolder(world);
        if (regionFolder.exists()) {
            File[] regionFilesArr = regionFolder.listFiles();
            for (File regionFile : regionFilesArr) {
                if (regionFile.isFile() && regionFile.length() >= 4096) {
                    regionFiles.add(regionFile);
                }
            }
        }

        // Detect any addition Region Files in the cache that are not yet saved
        // Synchronized, since we are going to iterate the files here...unsafe not to do so!
        /*
        synchronized (regionFileCacheType) {
            for (File regionFile : getCache().keySet()) {
                if (regionFile != null && regionFile.getParentFile().equals(regionFolder)) {
                    regionFiles.add(regionFile);
                }
            }
        }
        */

        // Parse all found files into the region x and z coordinates
        HashSet<IntVector3> regionIndices = new HashSet<IntVector3>();
        for (File file : regionFiles) {
            IntVector3 coords = getRegionFileCoordinates(file);
            if (coords != null) {
                regionIndices.add(coords);
            }
        }

        // Look at all loaded chunks and their cubes of the world and add the regions they are inside of
        for (Chunk chunk : world.getLoadedChunks()) {
            List<Integer> cubes_y = handle.getLoadedCubesY(HandleConversion.toChunkHandle(chunk));
            for (Integer y : cubes_y) {
                IntVector3 coords = new IntVector3(chunk.getX() >> 5, y.intValue() >> 6, chunk.getZ() >> 5);
                regionIndices.add(coords);
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
        for (Object regionProvider : handle.getRegionProviders(chunkProviderServer)) {
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
        System.out.println("From existing region(" + regionProvider.getClass() + "   " + callback.getClass());
        return (T) handle.fromExistingRegion(regionProvider, base_cx, base_cy, base_cz, callback);
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        return false;
    }

    protected File getRegionFolder(World world) {
        return new File(Common.SERVER.getWorldFolder(world.getName()), "region3d");
    }

    protected IntVector3 getRegionFileCoordinates(File regionFile) {
        String regionFileName = regionFile.getName();

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

    // Note: uses cubic chunks coordinate space (16x16x16 cubes per region)
    protected File getRegionFile(World world, int ccrx, int ccry, int ccrz) {
        File regionsFolder = getRegionFolder(world);
        StringBuilder fileName = new StringBuilder();
        fileName.append(ccrx).append('.').append(ccry).append('.').append(ccrz).append(".3dr");
        return new File(regionsFolder, fileName.toString());
    }

    @Template.Optional
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.IColumn")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.ICube")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.server.chunkio.ICubeIO")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.world.ICubeProviderInternal")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.server.chunkio.RegionCubeIO")
    @Template.Import("cubicchunks.regionlib.api.region.IRegionProvider")
    @Template.Import("cubicchunks.regionlib.impl.save.SaveSection3D")
    @Template.Import("cubicchunks.regionlib.impl.SaveCubeColumns")
    @Template.Import("cubicchunks.regionlib.impl.EntryLocation3D")
    @Template.InstanceType("net.minecraft.server.World")
    public static abstract class CubicChunksHandle extends Template.Class<Template.Handle> {

        /*
         * <IS_SUPPORTED>
         * public static boolean isSupported(net.minecraft.server.WorldServer world) {
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
         *     RegionCubeIO regionCubeIO = (RegionCubeIO) cubeIO;
         *     #require RegionCubeIO private SaveCubeColumns getSave();
         *     SaveCubeColumns columns = regionCubeIO#getSave();
         *     SaveSection3D saveSection = columns.getSaveSection3D();
         *     #require cubicchunks.regionlib.api.storage.SaveSection private final java.util.List<IRegionProvider> regionProviders;
         *     return saveSection#regionProviders;
         * }
         */
        @Template.Generated("%GET_REGION_PROVIDERS%")
        public abstract List<Object> getRegionProviders(Object chunkProviderServer);
    }
}
