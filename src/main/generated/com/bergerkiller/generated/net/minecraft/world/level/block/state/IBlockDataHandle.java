package com.bergerkiller.generated.net.minecraft.world.level.block.state;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.IBlockStateHandle;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.state.IBlockData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.state.IBlockData")
public abstract class IBlockDataHandle extends Template.Handle {
    /** @see IBlockDataClass */
    public static final IBlockDataClass T = Template.Class.create(IBlockDataClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IBlockDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockHandle getBlock();
    public abstract BlockFaceSet getCachedOpaqueFaces();
    public abstract int getCachedOpacity();
    public abstract boolean isPowerSource();
    public abstract boolean isSolid();
    public abstract Object get(IBlockStateHandle state);
    public abstract IBlockDataHandle set(IBlockStateHandle state, Object value);
    public abstract Map<IBlockStateHandle, Comparable<?>> getStates();

    public void logStates() {
        for (java.util.Map.Entry<IBlockStateHandle, Comparable<?>> entry : getStates().entrySet()) {
            com.bergerkiller.bukkit.common.Logging.LOGGER.info(entry.getKey() + " = " + entry.getValue());
        }
    }

    public IBlockStateHandle findState(String key) {
        for (IBlockStateHandle blockState : getStates().keySet()) {
            if (blockState.getKeyToken().equals(key)) {
                return blockState;
            }
        }
        return null;
    }

    public IBlockDataHandle set(String key, Object value) {
        return set(findState(key), value);
    }

    public <T> T get(String key, Class<T> type) {
        return get(findState(key), type);
    }

    public <T> T get(IBlockStateHandle state, Class<T> type) {
        return com.bergerkiller.bukkit.common.conversion.Conversion.convert(get(state), type, null);
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.block.state.IBlockData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IBlockDataClass extends Template.Class<IBlockDataHandle> {
        public final Template.Method.Converted<BlockHandle> getBlock = new Template.Method.Converted<BlockHandle>();
        public final Template.Method<BlockFaceSet> getCachedOpaqueFaces = new Template.Method<BlockFaceSet>();
        public final Template.Method<Integer> getCachedOpacity = new Template.Method<Integer>();
        public final Template.Method<Boolean> isPowerSource = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isSolid = new Template.Method<Boolean>();
        public final Template.Method.Converted<Object> get = new Template.Method.Converted<Object>();
        public final Template.Method.Converted<IBlockDataHandle> set = new Template.Method.Converted<IBlockDataHandle>();
        public final Template.Method.Converted<Map<IBlockStateHandle, Comparable<?>>> getStates = new Template.Method.Converted<Map<IBlockStateHandle, Comparable<?>>>();

    }

}

