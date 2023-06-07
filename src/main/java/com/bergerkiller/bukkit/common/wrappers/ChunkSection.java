package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.DataPaletteBlockHandle;

public class ChunkSection extends BasicWrapper<ChunkSectionHandle> {
    private final DataPaletteBlockHandle blockIds;
    private final int yPos;

    public ChunkSection(ChunkSectionHandle nmsChunkSectionHandle, int yPos) {
        setHandle(nmsChunkSectionHandle);
        this.blockIds = handle.getBlockPalette();
        this.yPos = yPos;
    }

    /**
     * Gets the y-coordinate of the chunk section. Each section stores 16 vertical blocks.
     * 
     * @return section y coordinate
     */
    public int getY() {
        return yPos >> 4;
    }

    /**
     * Gets the base y-position of this Chunk Section. This is the lowest world coordinate
     * a block is at in the World stored in this section.
     * 
     * @return bottom y position
     */
    public int getYPosition() {
        return yPos;
    }

    /**
     * Obtains the Block Data stored at particular coordinates in this Chunk Section
     * 
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @return BlockData
     */
    public BlockData getBlockData(int x, int y, int z) {
        return blockIds.getBlockData(x, y, z);
    }

    /**
     * Sets the Block Data stored at particular coordinates in this Chunk Section
     * 
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate
     * @param data to set to
     */
    public void setBlockData(int x, int y, int z, BlockData data) {
        blockIds.setBlockData(x, y, z, data);
    }

}
