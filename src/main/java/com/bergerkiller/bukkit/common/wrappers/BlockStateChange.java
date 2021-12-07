package com.bergerkiller.bukkit.common.wrappers;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.TileEntityTypesSerializedIds_1_8_to_1_17_1;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;

/**
 * Represents the change of a Block State's properties
 */
public abstract class BlockStateChange {

    /**
     * Gets the type of BlockState that changed
     *
     * @return Block State Type
     */
    public abstract BlockStateType getType();

    /**
     * Gets the Block coordinates of the block that changed
     *
     * @return Block coordinates
     */
    public abstract IntVector3 getPosition();

    /**
     * Gets the (new) metadata of the Block after this change
     *
     * @return New block state metadata
     */
    public abstract CommonTagCompound getMetadata();

    /**
     * Checks whether metadata is contained at all in this change.
     * Some Block states have no metadata, or it is optional, in which
     * case {@link #getMetadata()} returns a (new) empty tag. This method
     * can be used as an optimization to avoid calling it.
     *
     * @return True if this change includes metadata
     */
    public abstract boolean hasMetadata();

    /**
     * Serializes the metadata, position and type information into
     * a single NBT Tag compound. On older versions of Minecraft this
     * simply returns {@link #getMetadata()}, while on newer versions
     * it will include the required fields.
     */
    public abstract CommonTagCompound serialize();

    @Override
    public String toString() {
        return "BlockStateChange{type=" + getType() + ", pos=" + getPosition() +
                ", meta=" + (hasMetadata() ? getMetadata().toString() : "none") + "}";
    }

    @Override
    public int hashCode() {
        return this.getPosition().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof BlockStateChange) {
            BlockStateChange other = (BlockStateChange) o;
            if (this.getType() != other.getType() || !this.getPosition().equals(other.getPosition())) {
                return false;
            }
            if (this.hasMetadata()) {
                return other.hasMetadata() && this.getMetadata().equals(other.getMetadata());
            } else {
                return !other.hasMetadata() || other.getMetadata().isEmpty();
            }
        } else {
            return false;
        }
    }

    /**
     * Creates a BlockStateChange from block state (tile entity) metadata
     * packed into one. This was used from Minecraft 1.9.4 to 1.17.1 for storing
     * the block state information inside packets.
     *
     * @param metadata Block state NBT metadata
     * @return BlockStateChange
     */
    public static BlockStateChange fromMetadataPacked(CommonTagCompound metadata) {
        return new BlockStateChangeMetadataPacked(metadata);
    }

    /**
     * Creates a BlockStateChange with the change information as specified, deferring
     * to a supplier for supplying the writable metadata when requested.
     * When first called, it will initialize writable metadata if required,
     * but if never called will leave it uninitialized. The metadata field
     * does not store the position or block state type information, unlike
     * {@link #fromMetadataPacked(CommonTagCompound)}.
     *
     * @param position Block coordinates of the block whose state changed
     * @param type Type of BlockState that changed
     * @param metadataSupplier Supplies the writable metadata information of the block.
     *                         May instantiate new metadata tags when first called.
     * @param hasMetadataSupplier Supplies whether metadata is currently available
     * @return BlockStateChange
     */
    public static BlockStateChange deferred(IntVector3 position, BlockStateType type,
            Supplier<CommonTagCompound> metadataSupplier, BooleanSupplier hasMetadataSupplier
    ) {
        return new BlockStateChangeMetadataDeferred(position, type, metadataSupplier,
                hasMetadataSupplier);
    }

    private static final class BlockStateChangeMetadataPacked extends BlockStateChange {
        private final CommonTagCompound metadata;

        public BlockStateChangeMetadataPacked(CommonTagCompound metadata) {
            this.metadata = metadata;
        }

        @Override
        public BlockStateType getType() {
            String id = metadata.getValue("id", String.class);
            if (CommonCapabilities.TILE_ENTITY_LEGACY_NAMES) {
                MinecraftKeyHandle key = TileEntityTypesSerializedIds_1_8_to_1_17_1.toMinecraftKeyFromLegacyName(id);
                return BlockStateType.byKey(key);
            } else {
                return (id == null) ? null : BlockStateType.byName(id);
            }
        }

        @Override
        public IntVector3 getPosition() {
            Integer x = metadata.getValue("x", Integer.class);
            Integer y = metadata.getValue("y", Integer.class);
            Integer z = metadata.getValue("z", Integer.class);
            if (x != null && y != null && z != null) {
                return new IntVector3(x, y, z);
            } else {
                return null;
            }
        }

        @Override
        public CommonTagCompound getMetadata() {
            return metadata;
        }

        @Override
        public CommonTagCompound serialize() {
            return metadata;
        }

        @Override
        public boolean hasMetadata() {
            return true;
        }
    }

    private static final class BlockStateChangeMetadataDeferred extends BlockStateChange {
        private final IntVector3 position;
        private final BlockStateType type;
        private final Supplier<CommonTagCompound> metadataSupplier;
        private final BooleanSupplier hasMetadataSupplier;

        public BlockStateChangeMetadataDeferred(IntVector3 position, BlockStateType type,
                Supplier<CommonTagCompound> metadataSupplier, BooleanSupplier hasMetadataSupplier
        ) {
            this.position = position;
            this.type = type;
            this.metadataSupplier = metadataSupplier;
            this.hasMetadataSupplier = hasMetadataSupplier;
        }

        @Override
        public BlockStateType getType() {
            return type;
        }

        @Override
        public IntVector3 getPosition() {
            return position;
        }

        @Override
        public boolean hasMetadata() {
            return hasMetadataSupplier.getAsBoolean();
        }

        @Override
        public CommonTagCompound getMetadata() {
            return metadataSupplier.get();
        }

        @Override
        public CommonTagCompound serialize() {
            CommonTagCompound serialized = hasMetadataSupplier.getAsBoolean()
                    ? metadataSupplier.get().clone() : new CommonTagCompound();

            if (CommonCapabilities.TILE_ENTITY_LEGACY_NAMES) {
                serialized.putValue("id", TileEntityTypesSerializedIds_1_8_to_1_17_1.getLegacyName(
                        type.getKey()));
            } else {
                serialized.putValue("id", type.getKey().toString());
            }
            serialized.putValue("x", position.x);
            serialized.putValue("y", position.y);
            serialized.putValue("z", position.z);
            return serialized;
        }
    }
}
