package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Emulates access to a char[] array as if it is by the DataPaletteBlock API.
 * Only used on MC 1.8.8
 */
public class DataPaletteBlock {
    private final char[] blockIds; // length of 4096 (16x16x16 blocks)

    public DataPaletteBlock(char[] blockIds) {
        this.blockIds = blockIds;
    }

    public BlockData getBlockData(int x, int y, int z) {
        return BlockData.fromTypeId(this.blockIds[y << 8 | z << 4 | x]);
    }

    @SuppressWarnings("deprecation")
    public void setBlockData(int x, int y, int z, BlockData data) {
        this.blockIds[y << 8 | z << 4 | x] = (char) data.getTypeId();
    }

}
