package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_11_R1.DataPaletteBlock;
import net.minecraft.server.v1_11_R1.IBlockData;

import com.bergerkiller.reflection.net.minecraft.server.NMSChunkSection;
import com.bergerkiller.reflection.net.minecraft.server.NMSNibbleArray;

public class ChunkSection extends BasicWrapper {
    private final DataPaletteBlock blockIds;

    public ChunkSection(Object nmsChunkSectionHandle) {
        setHandle(nmsChunkSectionHandle);
        this.blockIds = (DataPaletteBlock) NMSChunkSection.getBlockPalette.invoke(getHandle());
    }

    /**
     * Gets whether this Chunk Section stored sky light information.
     * If no sky exists (e.g. The Nether), this function returns false.
     * 
     * @return True if skylight exists, False if not
     */
    public boolean hasSkyLight() {
        return NMSChunkSection.getSkyLightNibble.invoke(getHandle()) != null;
    }

    /**
     * Obtains a copy of all sky lighting information stored in this Chunk Section.
     * If no sky exists (e.g. The Nether), this function returns null.
     * 
     * @return Sky lighting data
     */
    public byte[] getSkyLightData() {
        Object skyLightNibble = NMSChunkSection.getSkyLightNibble.invoke(getHandle());
        if (skyLightNibble == null) {
            return null;
        } else {
            return NMSNibbleArray.getArrayCopy(skyLightNibble);
        }
    }

    /**
     * Obtains a copy of all block lighting information stored in this Chunk Section.
     * 
     * @return Block lighting data
     */
    public byte[] getBlockLightData() {
        Object blockLightNibble = NMSChunkSection.getBlockLightNibble.invoke(getHandle());
        return NMSNibbleArray.getArrayCopy(blockLightNibble);
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
        return BlockData.fromBlockData(blockIds.a(x, y, z));
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
        blockIds.setBlock(x, y, z, (IBlockData) data.getData());
    }
}
