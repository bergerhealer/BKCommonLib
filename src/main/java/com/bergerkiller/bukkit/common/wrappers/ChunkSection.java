package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_11_R1.DataPaletteBlock;

import com.bergerkiller.reflection.net.minecraft.server.NMSChunkSection;
import com.bergerkiller.reflection.net.minecraft.server.NMSNibbleArray;

public class ChunkSection extends BasicWrapper {
    private final DataPaletteBlock blockIds;

    public ChunkSection(Object nmsChunkSectionHandle) {
        setHandle(nmsChunkSectionHandle);
        this.blockIds = (DataPaletteBlock) NMSChunkSection.getBlockData.invoke(getHandle());
    }

    public byte[] getSkyLightData() {
        Object skyLightNibble = NMSChunkSection.getSkyLightNibble.invoke(getHandle());
        if (skyLightNibble == null) {
            return null;
        } else {
            return NMSNibbleArray.getArrayCopy(skyLightNibble);
        }
    }

    public byte[] getBlockLightData() {
        Object blockLightNibble = NMSChunkSection.getBlockLightNibble.invoke(getHandle());
        return NMSNibbleArray.getArrayCopy(blockLightNibble);
    }

    public BlockData getBlockData(int x, int y, int z) {
        return BlockData.fromBlockData(blockIds.a(x, y, z));
    }
}
