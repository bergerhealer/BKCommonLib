package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.CraftBlockStateHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

/**
 * Deprecated: use {@link BlockStateConversion} and {@link CraftBlockStateHandle} instead to perform BlockState
 * conversion and initialization, or to access private members.
 */
@Deprecated
public class CBCraftBlockState {
    public static final ClassTemplate<?> T = ClassTemplate.create(CommonUtil.getCBClass("block.CraftBlockState"));

    public static Object toTileEntity(BlockState state) {
        return BlockStateConversion.INSTANCE.blockStateToTileEntity(state);
    }

    public static BlockState toBlockState(Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    public static BlockState toBlockState(Object tileEntity) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(tileEntity);
    }
}
