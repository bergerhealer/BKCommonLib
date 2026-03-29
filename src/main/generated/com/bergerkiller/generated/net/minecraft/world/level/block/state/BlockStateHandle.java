package com.bergerkiller.generated.net.minecraft.world.level.block.state;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.generated.net.minecraft.world.level.BlockGetterHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.SoundTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.PropertyHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import java.util.Map;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.state.BlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.state.BlockState")
public abstract class BlockStateHandle extends Template.Handle {
    /** @see BlockStateClass */
    public static final BlockStateClass T = Template.Class.create(BlockStateClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract BlockHandle getBlock();
    public abstract BlockFaceSet getCachedOpaqueFaces();
    public abstract int getCachedOpacity();
    public abstract boolean isPowerSource();
    public abstract SoundTypeHandle getSoundType();
    public abstract boolean isSolid();
    public abstract AABBHandle getInteractableBox(BlockGetterHandle iblockaccess, IntVector3 blockposition);
    public abstract AABBHandle getBoundingBox(BlockGetterHandle iblockaccess, IntVector3 blockposition);
    public abstract Object get(PropertyHandle state);
    public abstract BlockStateHandle set(PropertyHandle state, Object value);
    public abstract Map<PropertyHandle, Comparable<?>> getStates();
    public void logStates() {
        for (java.util.Map.Entry<PropertyHandle, Comparable<?>> entry : getStates().entrySet()) {
            com.bergerkiller.bukkit.common.Logging.LOGGER.info(entry.getKey() + " = " + entry.getValue());
        }
    }

    public PropertyHandle findState(String key) {
        for (PropertyHandle blockState : getStates().keySet()) {
            if (blockState.getKeyToken().equals(key)) {
                return blockState;
            }
        }
        return null;
    }

    public BlockStateHandle set(String key, Object value) {
        return set(findState(key), value);
    }

    public <T> T get(String key, Class<T> type) {
        return get(findState(key), type);
    }

    public <T> T get(PropertyHandle state, Class<T> type) {
        return com.bergerkiller.bukkit.common.conversion.Conversion.convert(get(state), type, null);
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.block.state.BlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockStateClass extends Template.Class<BlockStateHandle> {
        public final Template.Method.Converted<BlockHandle> getBlock = new Template.Method.Converted<BlockHandle>();
        public final Template.Method<BlockFaceSet> getCachedOpaqueFaces = new Template.Method<BlockFaceSet>();
        public final Template.Method<Integer> getCachedOpacity = new Template.Method<Integer>();
        public final Template.Method<Boolean> isPowerSource = new Template.Method<Boolean>();
        public final Template.Method.Converted<SoundTypeHandle> getSoundType = new Template.Method.Converted<SoundTypeHandle>();
        public final Template.Method<Boolean> isSolid = new Template.Method<Boolean>();
        public final Template.Method.Converted<AABBHandle> getInteractableBox = new Template.Method.Converted<AABBHandle>();
        public final Template.Method.Converted<AABBHandle> getBoundingBox = new Template.Method.Converted<AABBHandle>();
        public final Template.Method.Converted<Object> get = new Template.Method.Converted<Object>();
        public final Template.Method.Converted<BlockStateHandle> set = new Template.Method.Converted<BlockStateHandle>();
        public final Template.Method.Converted<Map<PropertyHandle, Comparable<?>>> getStates = new Template.Method.Converted<Map<PropertyHandle, Comparable<?>>>();

    }

}

