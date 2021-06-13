package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkSectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use ChunkSectionHandle instead
 */
@Deprecated
public class NMSChunkSection {
    public static final ClassTemplate<?> T = ClassTemplate.create(ChunkSectionHandle.T.getType());

    public static final MethodAccessor<Boolean> isEmpty  = ChunkSectionHandle.T.isEmpty.toMethodAccessor();

    public static final MethodAccessor<Object> getBlockPalette     = ChunkSectionHandle.T.getBlockPalette.raw.toMethodAccessor();

    public static BlockData getBlockData(Object section, int x, int y, int z) {
        return ChunkSectionHandle.T.getBlockData.invoke(section, x & 0xf, y & 0xf, z & 0xf);
    }

    public static void setBlockData(Object section, int x, int y, int z, BlockData data) {
        ChunkSectionHandle.T.setBlockData.invoke(section, x & 0xf, y & 0xf, z & 0xf, data);
    }
}
