package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumSkyBlockHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.World;

@Deprecated
public class NMSChunk {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("Chunk");
    public static final TranslatorFieldAccessor<ChunkSection[]> sections = ChunkHandle.T.sections.raw.toFieldAccessor().translate(DuplexConversion.chunkSectionArray);

    private static final MethodAccessor<Void> addEntities = ChunkHandle.T.addEntities.toMethodAccessor();
    private static final MethodAccessor<Boolean> needsSaving = ChunkHandle.T.checkCanSave.toMethodAccessor();

    public static final TranslatorFieldAccessor<World> world = ChunkHandle.T.world.raw.toFieldAccessor().translate(DuplexConversion.world);

    public static final int XZ_MASK = 0xf;
    public static final int Y_MASK = 0xff;

    public static void addEntities(Object chunkHandle) {
        addEntities.invoke(chunkHandle);
    }

    /**
     * Whether saving is needed for a chunk
     *
     * @param chunkHandle to check
     * @return True if the chunk needs saving, False if not
     */
    public static boolean needsSaving(Object chunkHandle) {
        return needsSaving.invoke(chunkHandle, false);
    }

    /**
     * Gets the y-coordinate of the highest chunk section
     *
     * @param chunkHandle to get it from
     * @return chunk section highest y-position
     */
    public static int getTopSectionY(Object chunkHandle) {
        return ChunkHandle.T.getTopSliceY.invoke(chunkHandle);
    }

    public static int getBlockLight(Object chunkHandle, int x, int y, int z) {
        return getBrightness(chunkHandle, x, y, z, EnumSkyBlockHandle.BLOCK);
    }

    public static int getSkyLight(Object chunkHandle, int x, int y, int z) {
        return getBrightness(chunkHandle, x, y, z, EnumSkyBlockHandle.SKY);
    }

    private static int getBrightness(Object chunkHandle, int x, int y, int z, EnumSkyBlockHandle mode) {
        if (y < 0) {
            return 0;
        } else if (y >= ChunkHandle.T.world.get(chunkHandle).getWorld().getMaxHeight()) {
            return mode.getBrightness();
        }
        return ChunkHandle.T.getBrightness.invoke(chunkHandle, mode.getRaw(), new IntVector3(x & XZ_MASK, y, z & XZ_MASK));
    }

    public static boolean setBlockData(Object chunkHandle, int x, int y, int z, BlockData data) {
        return ChunkHandle.T.setBlockData.invoke(chunkHandle, new IntVector3(x & XZ_MASK, y, z & XZ_MASK), data) != null;
    }

    public static BlockData getBlockData(Object chunkHandle, int x, int y, int z) {
        return ChunkHandle.T.getBlockData.invoke(chunkHandle, new IntVector3(x & XZ_MASK, y, z & XZ_MASK));
    }

}
