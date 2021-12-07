package com.bergerkiller.bukkit.common.conversion.blockstate;

import java.util.List;

import com.bergerkiller.bukkit.common.bases.DeferredSupplier;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelChunkPacketDataHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

/**
 * Converts between the internal BlockEntityData class, and the BKCL API BlockStateChange
 * class. This converter is required because position is stored relative to the chunk coordinates.
 */
public final class ChunkBlockStateChangeConverter extends DuplexConverter<Object, BlockStateChange> {
    private static final TypeDeclaration INPUT_TYPE = TypeDeclaration.fromClass(
            ClientboundLevelChunkPacketDataHandle.BlockEntityDataHandle.T.getType());
    private static final TypeDeclaration OUTPUT_TYPE = TypeDeclaration.fromClass(BlockStateChange.class);
    private final int chunkX;
    private final int chunkZ;

    public static List<BlockStateChange> convertList(List<?> list, int chunkX, int chunkZ) {
        return new ConvertingList<BlockStateChange>(list, new ChunkBlockStateChangeConverter(chunkX, chunkZ));
    }

    public ChunkBlockStateChangeConverter(int chunkX, int chunkZ) {
        super(INPUT_TYPE, OUTPUT_TYPE); // Constant fields because this constructor will run a lot...
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    public BlockStateChange convertInput(Object value) {
        final ClientboundLevelChunkPacketDataHandle.BlockEntityDataHandle handle;
        handle = ClientboundLevelChunkPacketDataHandle.BlockEntityDataHandle.createHandle(value);

        // Deferred readout of metadata for performance, as we're using reflection
        IntVector3 position = handle.getPosition(this.chunkX, this.chunkZ);
        BlockStateType type = handle.getType();
        CommonTagCompound initialMetadata = handle.getTag();
        if (initialMetadata != null) {
            // Constant value
            return BlockStateChange.deferred(position, type,
                    LogicUtil.constantSupplier(initialMetadata), () -> true);
        } else {
            // Initialize when first called
            final DeferredSupplier<CommonTagCompound> metadataSupplier = DeferredSupplier.of(() -> {
                CommonTagCompound metadata = new CommonTagCompound();
                handle.setTag(metadata);
                return metadata;
            });
            return BlockStateChange.deferred(position, type,
                    metadataSupplier, metadataSupplier::isInitialized);
        }
    }

    @Override
    public Object convertOutput(BlockStateChange value) {
        return ClientboundLevelChunkPacketDataHandle.BlockEntityDataHandle.encodeRaw(
                value.getPosition(), value.getType(),
                value.hasMetadata() ? value.getMetadata() : null);
    }
}
