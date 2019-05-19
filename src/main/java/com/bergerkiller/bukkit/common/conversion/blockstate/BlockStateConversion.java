package com.bergerkiller.bukkit.common.conversion.blockstate;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.Common;

/**
 * Specialized utility class that deals with conversion from TileEntity to its respective
 * BlockState. This is done in such a way that no calls to the server state are made, guaranteeing
 * no internal modifications occur. This is required when querying TileEntities not yet added to a world.<br>
 * <br>
 * All of this is done by creating a virtual proxy environment in which to perform CraftBlockState construction.
 */
public abstract class BlockStateConversion {
    public static final BlockStateConversion INSTANCE;

    static {
        BlockStateConversion inst = null;
        try {
            if (Common.evaluateMCVersion(">=", "1.13")) {
                inst = new BlockStateConversion_1_13();
            } else {
                inst = new BlockStateConversion_1_12_2();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }       
        INSTANCE = inst;
    }

    public abstract Object blockStateToTileEntity(BlockState state);

    public abstract BlockState blockToBlockState(Block block);

    public abstract BlockState tileEntityToBlockState(org.bukkit.Chunk chunk, Object nmsTileEntity);

    /**
     * <b>Deprecated: may cause unwanted chunk load when trying to convert</b>
     */
    @Deprecated
    public BlockState tileEntityToBlockState(Object nmsTileEntity) {
        return tileEntityToBlockState(null, nmsTileEntity);
    }
}
