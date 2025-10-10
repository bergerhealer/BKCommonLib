package com.bergerkiller.bukkit.common.wrappers;

import java.util.Collection;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.IBlockStateHandle;

/**
 * A single state of a block's BlockData. Not to be confused with Bukkit's BlockState,
 * which is more about block entity metadata. Acts as a key for changing block
 * data values like facing and redstone power state.
 */
public class BlockDataStateKey<T extends Comparable<?>> extends BlockState<T> {

    public BlockDataStateKey(IBlockStateHandle handle) {
        setHandle(handle);
    }

    /**
     * Name identifying this state
     * 
     * @return name
     */
    public String name() {
        return handle.getKeyToken();
    }

    /**
     * The possible values this state can have
     * 
     * @return values
     */
    public Collection<T> values() {
        return LogicUtil.unsafeCast(handle.getValues());
    }

    /**
     * Gets a String representation of a value returned by {@link #values()}
     * 
     * @param value
     * @return value name
     */
    public String valueName(Comparable<?> value) {
        return handle.getValueToken(value);
    }
}
