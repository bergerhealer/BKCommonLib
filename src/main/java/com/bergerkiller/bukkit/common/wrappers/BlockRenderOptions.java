package com.bergerkiller.bukkit.common.wrappers;

import java.util.Map;

import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;

public final class BlockRenderOptions extends RenderOptions {
    private final BlockData blockData;

    public BlockRenderOptions(BlockData blockData, String optionsToken) {
        super(optionsToken);
        this.blockData = blockData;
    }

    public BlockRenderOptions(BlockData blockData, Map<String, String> optionsMap) {
        super(optionsMap);
        this.blockData = blockData;
    }

    @Override
    public int hashCode() {
        return this.blockData.hashCode() + (super.hashCode() >> 10);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BlockRenderOptions) {
            return super.equals(o) && ((BlockRenderOptions) o).blockData.equals(this.blockData);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.blockData.getBlockName() + super.toString();
    }

    @Override
    public BlockRenderOptions clone() {
        if (this.optionsMap != null) {
            return new BlockRenderOptions(this.blockData, this.optionsMap);
        } else {
            return new BlockRenderOptions(this.blockData, this.optionsToken);
        }
    }

    /**
     * Gets the Block Data of the Block these rendering options are for
     * 
     * @return block data
     */
    public final BlockData getBlockData() {
        return this.blockData;
    }

    @Override
    public final String lookupModelName() {
        return ModelInfoLookup.lookupBlock(this);
    }
}
