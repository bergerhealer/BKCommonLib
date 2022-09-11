package com.bergerkiller.bukkit.common.wrappers;

import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.org.bukkit.block.BlockStateHandle;

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
        return fromBlockData(BlockHandle.T.getBlockData.raw.invoke(block));
    }

    /**
     * Obtains immutable BlockData information wrapping particular IBlockData
     * 
     * @param iBlockData input data
     * @return Immutable BlockData
     */
    public static BlockData fromBlockData(Object iBlockData) {
        final BlockDataWrapperHook hook = BlockDataWrapperHook.INSTANCE;

        // Try to access an already hooked IBlockData first
        Object accessor;
        try {
            accessor = hook.getAccessor(iBlockData);
            if (accessor instanceof BlockDataWrapperHook.Accessor) {
                return ((BlockDataWrapperHook.Accessor) accessor).bkcGetBlockData();
            }
        } catch (Throwable t) {
            BlockDataWrapperHook.disableHook(); // Gross. Stop using it.
            Logging.LOGGER.log(Level.SEVERE, "Failed to read IBlockData accessor field", t);
            return BlockDataImpl.BY_BLOCK_DATA.getOrCreate(iBlockData);
        }

        // Got to hook it
        BlockData blockData = BlockDataImpl.BY_BLOCK_DATA.getOrCreate(iBlockData);
        try {
            hook.hook(iBlockData, accessor, blockData);
        } catch (Throwable t) {
            BlockDataWrapperHook.disableHook(); // Gross. Stop using it.
            Logging.LOGGER.log(Level.SEVERE, "Failed to hook IBlockData accessor", t);
        }
        return blockData;
    }

    /**
     * Obtains immutable BlockData information about the Block represented by a BlockState
     * 
     * @param state
     * @return BlockData of the state
     */
    public static BlockData fromBlockState(org.bukkit.block.BlockState state) {
        if (BlockStateHandle.T.getBlockData.isAvailable()) {
            return BlockStateHandle.T.getBlockData.invoke(state);
        } else {
            return fromMaterialData(state.getType(), state.getRawData());
        }
    }

    /**
     * Obtains immutable BlockData information for the Material and Data of an ItemStack
     * 
     * @param itemStack input
     * @return Immutable BlockData
     */
    public static BlockData fromItemStack(ItemStack itemStack) {
        if (CommonLegacyMaterials.isLegacy(itemStack.getType())) {
            return fromMaterialData(itemStack.getType(), itemStack.getDurability());
        } else {
            return fromMaterial(itemStack.getType());
        }
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
     * Deserializes a String previously serialized using {@link BlockData#serializeToString()}
     * into BlockData. Returns null if deserialization fails.
     * This deserialization is cross-version compatible.<br>
     * <br>
     * Example input:
     * <pre>minecraft:furnace[facing=east,lit=true]</pre>
     * 
     * @param serializedString Input serialized text</i>
     * @return deserialized BlockData, or null if deserialization failed
     */
    public static BlockData fromString(String serializedString) {
        return BlockDataSerializer.INSTANCE.deserialize(serializedString);
    }

    /**
     * Obtains an immutable collection containing all possible BlockData values
     * 
     * @return all BlockData values
     */
    public static Collection<BlockData> values() {
        return BlockDataImpl.BY_BLOCK_DATA.getAll();
    }
}
