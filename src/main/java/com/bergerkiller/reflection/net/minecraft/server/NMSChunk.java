package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.LightLayerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

@Deprecated
public class NMSChunk {
    public static final ClassTemplate<?> T = ClassTemplate.create(LevelChunkHandle.T.getType());

    private static final MethodAccessor<Void> addEntities = LevelChunkHandle.T.addEntities.toMethodAccessor();
    private static final MethodAccessor<Boolean> needsSaving = LevelChunkHandle.T.checkCanSave.toMethodAccessor();

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
        return LevelChunkHandle.T.getTopSliceY.invoke(chunkHandle);
    }

    public static int getBlockLight(Object chunkHandle, int x, int y, int z) {
        return getBrightness(chunkHandle, x, y, z, LightLayerHandle.BLOCK);
    }

    public static int getSkyLight(Object chunkHandle, int x, int y, int z) {
        return getBrightness(chunkHandle, x, y, z, LightLayerHandle.SKY);
    }

    private static int getBrightness(Object chunkHandle, int x, int y, int z, LightLayerHandle mode) {
        if (y < 0) {
            return 0;
        } else if (y >= LevelChunkHandle.T.getWorld.invoke(chunkHandle).getWorld().getMaxHeight()) {
            return (mode == LightLayerHandle.SKY) ? 15 : 0;
        }
        return LevelChunkHandle.T.getBrightness.invoke(chunkHandle, mode.getRaw(), new IntVector3(x & XZ_MASK, y, z & XZ_MASK));
    }

    public static boolean setBlockData(Object chunkHandle, int x, int y, int z, BlockData data) {
        return LevelChunkHandle.T.setBlockData.invoke(chunkHandle, new IntVector3(x & XZ_MASK, y, z & XZ_MASK), data) != null;
    }

    public static BlockData getBlockData(Object chunkHandle, int x, int y, int z) {
        return LevelChunkHandle.T.getBlockData.invoke(chunkHandle, new IntVector3(x & XZ_MASK, y, z & XZ_MASK));
    }

}
