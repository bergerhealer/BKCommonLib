package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a property for a given material<br>
 * If a material container is null, air (0) is used<br><br>
 * <p/>
 * It is <b>required</b> to either implement {@link #get(int)} or
 * {@link #get(Material)}. Not doing so will result in a Runtime exception being
 * thrown upon construction.
 */
public abstract class MaterialProperty<T> {

    /**
     * Gets this property for the Block Data specified
     * 
     * @param blockData to get this property of
     * @return The property of the Block Data
     */
    public T get(BlockData blockData) {
        return get(blockData.getType());
    }

    /**
     * Gets this property for the item specified
     *
     * @param item to get this property of
     * @return The property of the material
     */
    public T get(ItemStack item) {
        return item == null ? get(Material.AIR) : get(item.getType());
    }

    /**
     * Gets this property for the block specified
     *
     * @param block to get this property of
     * @return The property of the material
     */
    public T get(Block block) {
        return block == null ? get(Material.AIR) : get(block.getType());
    }

    /**
     * Gets this property for the block specified
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return The property of the material
     */
    public T get(Chunk chunk, int x, int y, int z) {
        return get(ChunkUtil.getBlockType(chunk, x, y, z));
    }

    /**
     * Gets this property for the block specified
     *
     * @param world the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return The property of the material
     */
    public T get(org.bukkit.World world, int x, int y, int z) {
        return get(WorldUtil.getBlockData(world, x, y, z).getType());
    }

    /**
     * Gets this property for the material specified
     *
     * @param material to get this property of
     * @return The property of the material
     */
	public abstract T get(Material material);

}
