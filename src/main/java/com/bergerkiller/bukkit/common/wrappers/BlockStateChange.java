package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.proxy.TileEntityTypesSerializedIds_1_8_to_1_17_1;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;

import java.util.function.Supplier;

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
     * Gets the (new) metadata of the Block after this change. Null if no metadata is specified.
     *
     * @return New block state metadata, or null if not {@link #hasMetadata()}}
     */
    public abstract CommonTagCompound getMetadataIfExists();

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
    public CommonTagCompound serialize() {
        CommonTagCompound serialized = getMetadataIfExists();
        if (serialized != null) {
            serialized = serialized.clone();
        } else {
            serialized = new CommonTagCompound();
        }

        if (CommonCapabilities.TILE_ENTITY_LEGACY_NAMES) {
            serialized.putValue("id", TileEntityTypesSerializedIds_1_8_to_1_17_1.getLegacyName(
                    getType().getKey()));
        } else {
            serialized.putValue("id", getType().getKey().toString());
        }

        IntVector3 position = getPosition();
        serialized.putValue("x", position.x);
        serialized.putValue("y", position.y);
        serialized.putValue("z", position.z);

        return serialized;
    }

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
     * Creates a BlockStateChange with the change information as specified. Note that writing
     * to this instance will not modify existing packet items when added to them. A conversion
     * will occur that breaks linkage with this detached state change (copy).
     *
     * @param position Block coordinates of the block whose state changed
     * @param type Type of BlockState that changed
     * @param metadata Initial metadata tag to assign to this block. Null for none.
     * @return BlockStateChange
     */
    public static BlockStateChange detached(IntVector3 position, BlockStateType type, CommonTagCompound metadata) {
        return new BlockStateChangeDetached(position, type, metadata);
    }

    /**
     * Creates a BlockStateChange with the change information as specified. Note that writing
     * to this instance will not modify existing packet items when added to them. A conversion
     * will occur that breaks linkage with this detached state change (copy).
     *
     * @param position Block coordinates of the block whose state changed
     * @param type Type of BlockState that changed
     * @param metadataSupplier Initial metadata tag to assign to this block once metadata is requested once.
     * @return BlockStateChange
     */
    public static BlockStateChange detachedDeferred(IntVector3 position, BlockStateType type, Supplier<CommonTagCompound> metadataSupplier) {
        return new BlockStateChangeDetached(position, type, metadataSupplier);
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
                IdentifierHandle key = TileEntityTypesSerializedIds_1_8_to_1_17_1.toMinecraftKeyFromLegacyName(id);
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
        public CommonTagCompound getMetadataIfExists() {
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

    /**
     * Detached block state change. This is what people can create using API to create brand new block
     * changes. It will be converted internally.
     */
    private static final class BlockStateChangeDetached extends BlockStateChange {
        private final IntVector3 position;
        private final BlockStateType type;
        private Supplier<CommonTagCompound> deferredMetadataSupplier; // If non-null, call this on first use
        private CommonTagCompound metadata;

        public BlockStateChangeDetached(IntVector3 position, BlockStateType type, CommonTagCompound metadata) {
            this.position = position;
            this.type = type;
            this.deferredMetadataSupplier = null;
            this.metadata = metadata;
        }

        public BlockStateChangeDetached(IntVector3 position, BlockStateType type, Supplier<CommonTagCompound> metadataSupplier) {
            this.position = position;
            this.type = type;
            this.deferredMetadataSupplier = metadataSupplier;
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
            return getMetadataIfExists() != null;
        }

        @Override
        public CommonTagCompound getMetadataIfExists() {
            Supplier<CommonTagCompound> supplier = deferredMetadataSupplier;
            if (supplier != null) {
                metadata = supplier.get();
                deferredMetadataSupplier = null;
            }
            return metadata;
        }

        @Override
        public CommonTagCompound getMetadata() {
            CommonTagCompound metadata = this.getMetadataIfExists();
            if (metadata == null) {
                this.metadata = metadata = new CommonTagCompound();
            }
            return metadata;
        }
    }
}
