package com.bergerkiller.bukkit.common.wrappers;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.BlockHandle;
import com.bergerkiller.generated.net.minecraft.server.IBlockDataHandle;

public class BlockDataRegistry {

    public static final BlockData AIR = fromMaterial(Material.AIR);

    /**
     * Creates an empty uninitialized BlockData object that can be set to a new state using the
     * apply* methods. By default the created object stored the information of AIR.
     * 
     * @return new BlockData wrapper object
     */
    public static BlockData create() {
        return new BlockDataImpl();
    }

    /**
     * Obtains immutable BlockData information for a particular Block
     * 
     * @param block input block
     * @return Immutable BlockData
     */
    public static BlockData fromBlock(Object block) {
        return BlockDataImpl.BY_BLOCK_DATA.get(BlockHandle.T.getBlockData.raw.invoke(block));
    }

    /**
     * Obtains immutable BlockData information wrapping particular IBlockData
     * 
     * @param iBlockData input data
     * @return Immutable BlockData
     */
    public static BlockData fromBlockData(Object iBlockData) {
        BlockData data = BlockDataImpl.BY_BLOCK_DATA.get(iBlockData);
        if (data != null) {
            return data;
        }

        IBlockDataHandle b = IBlockDataHandle.createHandle(iBlockData);
        BlockDataImpl.BlockDataConstant c = new BlockDataImpl.BlockDataConstant(b);
        BlockDataImpl.BY_BLOCK_DATA.put(b, c);
        return c;
    }

    /**
     * Obtains immutable BlockData information for a Block Material Type
     * 
     * @param material input
     * @return Immutable BlockData
     */
    @SuppressWarnings("deprecation")
    public static BlockData fromMaterial(Material material) {
        if (material.isBlock()) {
            return BlockDataImpl.BY_ID[material.getId()];
        } else {
            return BlockDataImpl.AIR;
        }
    }

    /**
     * Obtains immutable BlockData information for a MaterialData instance
     * 
     * @param materialData input MaterialData
     * @return Immutable BlockData
     */
    @SuppressWarnings("deprecation")
    public static BlockData fromMaterialData(MaterialData materialData) {
        return fromTypeIdAndData(materialData.getItemType().getId(), (int) materialData.getData());
    }

    /**
     * Obtains immutable BlockData information for a Material and its data
     * 
     * @param material input material
     * @param data input data
     * @return Immutable BlockData
     */
    @Deprecated
    public static BlockData fromMaterialData(Material material, int data) {
        return fromTypeIdAndData(material.getId(), data);
    }

    /**
     * Obtains immutable BlockData information by Block type Id
     * 
     * @param typeId input
     * @return Immutable BlockData
     */
    public static BlockData fromTypeId(int typeId) {
        return BlockDataImpl.BY_ID[typeId & BlockDataImpl.ID_MASK];
    }

    /**
     * This function is only used internally on MC 1.8.8 to translate from char[] block Ids to BlockData.
     * 
     * @param typeId
     * @return block data
     */
    @Deprecated
    public static BlockData fromCombinedId_1_8_8(int combinedId) {
        return BlockDataImpl.BY_ID_AND_DATA[combinedId & BlockDataImpl.REGISTRY_MASK];
    }

    /**
     * Obtains immutable BlockData information by Block type Id and its data
     * 
     * @param typeId input
     * @param data input
     * @return Immutable BlockData
     */
    @Deprecated
    public static BlockData fromTypeIdAndData(int typeId, int data) {
        return BlockDataImpl.BY_ID_AND_DATA[((typeId << BlockDataImpl.DATA_BITS) | (data & BlockDataImpl.DATA_MASK)) & BlockDataImpl.REGISTRY_MASK];
    }

    /**
     * Obtains immutable BlockData information by combined block id and data
     * 
     * @param combinedId of the block
     * @return Immutable BlockData
     */
    @Deprecated
    public static BlockData fromCombinedId(int combinedId) {
        return fromBlockData(BlockHandle.T.getByCombinedId.raw.invoke(combinedId));
    }

    /**
     * Obtains an immutable collection containing all possible BlockData values
     * 
     * @return all BlockData values
     */
    public static Collection<BlockData> values() {
        return CommonUtil.unsafeCast(BlockDataImpl.BY_BLOCK_DATA.values());
    }
}
