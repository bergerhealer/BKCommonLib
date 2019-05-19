package com.bergerkiller.bukkit.common.conversion.blockstate;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;

public class ChunkBlockStateConverter extends DuplexConverter<Object, BlockState> {
    private final Chunk chunk;

    public ChunkBlockStateConverter(Chunk chunk) {
        super(TileEntityHandle.T.getType(), BlockState.class);
        this.chunk = chunk;
    }

    @Override
    public BlockState convertInput(Object value) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(this.chunk, value);
    }

    @Override
    public Object convertOutput(BlockState value) {
        return HandleConversion.toTileEntityHandle(value);
    }

}
