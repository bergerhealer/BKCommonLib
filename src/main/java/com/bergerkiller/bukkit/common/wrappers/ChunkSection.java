package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.server.DataPaletteBlockHandle;
import com.bergerkiller.generated.net.minecraft.server.NibbleArrayHandle;
import com.bergerkiller.reflection.net.minecraft.server.NMSNibbleArray;

public class ChunkSection extends BasicWrapper<ChunkSectionHandle> {
    private final DataPaletteBlockHandle blockIds;

    public ChunkSection(ChunkSectionHandle nmsChunkSectionHandle) {
        setHandle(nmsChunkSectionHandle);
        this.blockIds = handle.getBlockPalette();
    }

    /**
     * Gets whether this Chunk Section stored sky light information.
     * If no sky exists (e.g. The Nether), this function returns false.
     * 
     * @return True if skylight exists, False if not
     */
    public boolean hasSkyLight() {
        return handle.getSkyLightArray() != null;
    }

    /**
     * Obtains a copy of all sky lighting information stored in this Chunk Section.
     * If no sky exists (e.g. The Nether), this function returns null.
     * 
     * @return Sky lighting data
     */
    public byte[] getSkyLightData() {
        NibbleArrayHandle skyLightNibble = handle.getSkyLightArray();
        if (skyLightNibble == null) {
            return null;
        } else {
            return NMSNibbleArray.getArrayCopy(skyLightNibble.getRaw());
        }
    }

    /**
     * Obtains a copy of all block lighting information stored in this Chunk Section.
     * 
     * @return Block lighting data
     */
    public byte[] getBlockLightData() {
        NibbleArrayHandle blockLightNibble = handle.getBlockLightArray();
        return NMSNibbleArray.getArrayCopy(blockLightNibble.getRaw());
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
