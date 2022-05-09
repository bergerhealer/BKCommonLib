package com.bergerkiller.bukkit.common.internal.logic;

import java.util.logging.Level;

import org.bukkit.event.block.BlockPhysicsEvent;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Reads the BlockData of a block that changed during a BlockPhysicsEvent.
 * Since Paper 1.17.1 there is a special optimized function to get this
 * without querying the world block data.
 */
public abstract class BlockPhysicsEventDataAccessor {
    public static BlockPhysicsEventDataAccessor INSTANCE;
    static {
        try {
            INSTANCE = new BlockPhysicsEventDataAccessorEventField();
        } catch (Throwable t) {
            //t.printStackTrace();
            INSTANCE = new BlockPhysicsEventDataAccessorDefault();
        }
    }

    /**
     * Gets the BlockData of the Block of a BlockPhysicsEvent
     *
     * @param event
     * @return BlockData
     */
    public abstract BlockData get(BlockPhysicsEvent event);

    private static final class BlockPhysicsEventDataAccessorDefault extends BlockPhysicsEventDataAccessor {
        @Override
        public BlockData get(BlockPhysicsEvent event) {
            return WorldUtil.getBlockData(event.getBlock());
        }
    }

    // Since a late 1.17.1 version of paper there is a custom method to quickly retrieve the block data
    private static final class BlockPhysicsEventDataAccessorEventField extends BlockPhysicsEventDataAccessor {
        private final FastMethod<Object> blockDataGetter;
        private final FastMethod<Object> blockDataGetState;

        public BlockPhysicsEventDataAccessorEventField() throws Throwable {
            Class<?> cbd = CommonUtil.getClass("org.bukkit.craftbukkit.block.data.CraftBlockData");
            this.blockDataGetter = new FastMethod<Object>(BlockPhysicsEvent.class.getDeclaredMethod("getChangedBlockData"));
            this.blockDataGetState = new FastMethod<Object>(cbd.getDeclaredMethod("getState"));
            this.blockDataGetter.forceInitialization();
            this.blockDataGetState.forceInitialization();
        }

        @Override
        public BlockData get(BlockPhysicsEvent event) {
            try {
                Object bukkit_blockdata = blockDataGetter.invoke(event);
                Object iblockdata = blockDataGetState.invoke(bukkit_blockdata);
                return BlockData.fromBlockData(iblockdata);
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "BlockPhysicsEvent getChangedBlockData failed", t);
                BlockPhysicsEventDataAccessor.INSTANCE = new BlockPhysicsEventDataAccessorDefault();
                return WorldUtil.getBlockData(event.getBlock());
            }
        }
    }
}
