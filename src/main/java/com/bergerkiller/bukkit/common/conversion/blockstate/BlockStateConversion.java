package com.bergerkiller.bukkit.common.conversion.blockstate;

import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;

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
        // Statically initializes WorldServer, so we must fully bootstrap the server first
        CommonBootstrap.initServer();

        BlockStateConversion inst = null;
        try {
            if (Common.evaluateMCVersion(">=", "1.13")) {
                inst = new BlockStateConversion_1_13();
            } else {
                inst = new BlockStateConversion_1_12_2();
            }
        } catch (Throwable t) {
            Logging.LOGGER_CONVERSION.log(Level.SEVERE, "Unhandled error initializing block state conversion logic", t);
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
