package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.IBlockStateHandle;

import java.util.Collection;

/**
 * Deprecated class! This will be removed in the near future! Use
 * {@link BlockDataStateKey} instead.
 *
 * @param <T> Value type
 */
@Deprecated
public abstract class BlockState<T extends Comparable<?>> extends BasicWrapper<IBlockStateHandle> {
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
        return CommonUtil.unsafeCast(handle.getValues());
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
