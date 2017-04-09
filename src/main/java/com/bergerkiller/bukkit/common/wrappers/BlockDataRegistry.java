package com.bergerkiller.bukkit.common.wrappers;

import net.minecraft.server.v1_11_R1.Block;
import net.minecraft.server.v1_11_R1.IBlockData;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class BlockDataRegistry {

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
        return BlockDataImpl.BY_BLOCK_DATA.get(((Block) block).getBlockData());
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

        IBlockData b = (IBlockData) iBlockData;
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
}
