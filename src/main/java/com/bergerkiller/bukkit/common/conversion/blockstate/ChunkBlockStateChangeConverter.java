package com.bergerkiller.bukkit.common.conversion.blockstate;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundLevelChunkPacketDataHandle;

/**
 * Represents a List of internal BlockEntityData values and represents it as the BlockStateChange API type.
 * This converter is required because position is stored relative to the chunk coordinates.
 * It handles initialization of new (block) metadata when a tag is modified for a block that did not include any.
 */
public final class ChunkBlockStateChangeConverter extends AbstractList<BlockStateChange> {
    private final int chunkX;
    private final int chunkZ;
    private final List<Object> rawList;
    private final List<BlockStateChangeItem> changeList;

    // Called from protocol_packets_other.txt templates
    public static List<BlockStateChange> convertList(List<?> rawList, int chunkX, int chunkZ) {
        return new ChunkBlockStateChangeConverter(rawList, chunkX, chunkZ);
    }

    private ChunkBlockStateChangeConverter(List<?> rawList, int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.rawList = LogicUtil.unsafeCast(rawList);
        this.changeList = new ArrayList<>(rawList.size());
        for (int i = 0; i < rawList.size(); i++) {
            this.changeList.add(new BlockStateChangeItem(i, rawList.get(i)));
        }
    }

    @Override
    public BlockStateChange get(int index) {
        return changeList.get(index);
    }

    @Override
    public BlockStateChange set(int index, BlockStateChange value) {
        BlockStateChangeItem prev = changeList.get(index);
        if (prev == value) {
            return value;
        }

        // Make sure previous value becomes 'immutable'
        prev.markRemovedFromList();

        // Modify raw list
        Object rawValue = toRawValue(value);
        rawList.set(index, rawValue);
        changeList.set(index, new BlockStateChangeItem(index, rawValue));

        return prev;
    }

    @Override
    public boolean add(BlockStateChange value) {
        int index = rawList.size();
        rawList.add(index);
        changeList.add( new BlockStateChangeItem(index, index));
        return true;
    }

    @Override
    public void add(int index, BlockStateChange value) {
        Object rawValue = toRawValue(value);
        rawList.add(index, rawValue);
        changeList.add(index, new BlockStateChangeItem(index, rawValue));
        for (int idxAfter = index + 1; idxAfter < changeList.size(); idxAfter++) {
            changeList.get(idxAfter).index = idxAfter;
        }
    }

    @Override
    public BlockStateChange remove(int index) {
        BlockStateChangeItem prev = changeList.remove(index);
        prev.markRemovedFromList();
        for (int idxAfter = index; idxAfter < changeList.size(); idxAfter++) {
            changeList.get(idxAfter).index = idxAfter;
        }
        return prev;
    }

    @Override
    public void clear() {
        changeList.forEach(BlockStateChangeItem::markRemovedFromList);
        rawList.clear();
        changeList.clear();
    }

    @Override
    public int size() {
        return changeList.size();
    }

    /**
     * Extracts or converts the internal raw value to represent the block metadata.
     * If the input value is also item, is optimized to simply repurpose the immutable entry.
     *
     * @param value BlockStateChange
     * @return BlockEntityInfo
     */
    private static Object toRawValue(BlockStateChange value) {
        if (value instanceof BlockStateChangeItem) {
            return ((BlockStateChangeItem) value).handle.getRaw();
        } else {
            return ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle.encodeRaw(
                    value.getPosition(), value.getType(),
                    value.hasMetadata() ? value.getMetadata() : null);
        }
    }

    private class BlockStateChangeItem extends BlockStateChange {
        private int index;
        private ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle handle;

        public BlockStateChangeItem(int index, Object rawValue) {
            this.index = index;
            this.handle = ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle.createHandle(rawValue);
        }

        private void swapRawValue(Object newRawValue) {
            if (index != -1) {
                rawList.set(index, newRawValue);
            }
            this.handle = ClientboundLevelChunkPacketDataHandle.BlockEntityInfoHandle.createHandle(newRawValue);
        }

        public void markRemovedFromList() {
            index = -1;
        }

        @Override
        public BlockStateType getType() {
            return handle.getType();
        }

        @Override
        public IntVector3 getPosition() {
            return handle.getPosition(chunkX, chunkZ);
        }

        @Override
        public CommonTagCompound getMetadata() {
            CommonTagCompound tag = handle.getTag();
            if (tag == null) {
                // Initialize a new metadata tag and swap it into the raw list
                tag = new CommonTagCompound();
                swapRawValue(handle.withTag(tag));
            }

            return tag;
        }

        @Override
        public CommonTagCompound getMetadataIfExists() {
            return handle.getTag();
        }

        @Override
        public boolean hasMetadata() {
            return handle.getTag() != null;
        }
    }
}
