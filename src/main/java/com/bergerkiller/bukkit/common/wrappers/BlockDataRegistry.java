package com.bergerkiller.bukkit.common.wrappers;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
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
     * Obtains immutable BlockData information for the Material and Data of an ItemStack
     * 
     * @param itemStack input
     * @return Immutable BlockData
     */
    public static BlockData fromItemStack(ItemStack itemStack) {
        if (itemStack != null) {
            Material type = itemStack.getType();
            if (type.isBlock()) {
                return fromMaterialData(type, itemStack.getDurability());
            }
        }
        return AIR;
    }

    /**
     * Obtains immutable BlockData information for a Block Material Type
     * 
     * @param material input
     * @return Immutable BlockData
     */
    public static BlockData fromMaterial(Material material) {
        return BlockDataImpl.BY_MATERIAL.get(material);
    }

    /**
     * Obtains immutable BlockData information for a MaterialData instance
     * 
     * @param materialData input MaterialData
     * @return Immutable BlockData
     */
    @SuppressWarnings("deprecation")
    public static BlockData fromMaterialData(MaterialData materialData) {
        return fromMaterialData(materialData.getItemType(), materialData.getData());
    }

    /**
     * Obtains immutable BlockData information for a Material type and MaterialData instance
     * 
     * @param type Material
     * @param materialData input MaterialData
     * @return Immutable BlockData
     */
    @SuppressWarnings("deprecation")
    public static BlockData fromMaterialData(Material type, MaterialData materialData) {
        return fromMaterialData(type, materialData.getData());
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
        int index = CommonLegacyMaterials.getOrdinal(material);
        index |= (data << BlockDataImpl.BY_LEGACY_MAT_DATA_SHIFT);
        return BlockDataImpl.BY_LEGACY_MAT_DATA[index];
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
