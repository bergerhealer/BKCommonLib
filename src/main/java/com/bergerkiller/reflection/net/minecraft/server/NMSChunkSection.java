package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;

/**
 * Deprecated: use ChunkSectionHandle instead
 */
@Deprecated
public class NMSChunkSection {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ChunkSection");
    public static final FieldAccessor<Object> skyLight   = ChunkSectionHandle.T.skyLight.raw.toFieldAccessor();
    public static final FieldAccessor<Object> blockLight = ChunkSectionHandle.T.blockLight.raw.toFieldAccessor();

    public static final MethodAccessor<Boolean> isEmpty  = ChunkSectionHandle.T.isEmpty.toMethodAccessor();

    public static final MethodAccessor<Object> getBlockLightNibble = ChunkSectionHandle.T.getBlockLightArray.raw.toMethodAccessor();
    public static final MethodAccessor<Object> getSkyLightNibble   = ChunkSectionHandle.T.getSkyLightArray.raw.toMethodAccessor();
    public static final MethodAccessor<Object> getBlockPalette     = ChunkSectionHandle.T.getBlockPalette.raw.toMethodAccessor();

    public static BlockData getBlockData(Object section, int x, int y, int z) {
        return ChunkSectionHandle.T.getBlockData.invoke(section, x & 0xf, y & 0xf, z & 0xf);
    }

    public static void setBlockData(Object section, int x, int y, int z, BlockData data) {
        ChunkSectionHandle.T.setBlockData.invoke(section, x & 0xf, y & 0xf, z & 0xf, data);
    }

    public static int getSkyLight(Object section, int x, int y, int z) {
        return ChunkSectionHandle.T.getSkyLight.invoke(section, x & 0xf, y & 0xf, z & 0xf);
    }

    public static void setSkyLight(Object section, int x, int y, int z, int level) {
        ChunkSectionHandle.T.setSkyLight.invoke(section, x & 0xf, y & 0xf, z & 0xf, level);
    }

    public static int getBlockLight(Object section, int x, int y, int z) {
        return ChunkSectionHandle.T.getBlockLight.invoke(section, x & 0xf, y & 0xf, z & 0xf);
    }

    public static void setBlockLight(Object section, int x, int y, int z, int level) {
        ChunkSectionHandle.T.setBlockLight.invoke(section, x & 0xf, y & 0xf, z & 0xf, level);
    }
}
