package com.bergerkiller.bukkit.common;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ChunkUtil;

/**
 * Represents a property for a given material<br>
 * If a material container is null, air (0) is used
 */
public abstract class MaterialProperty<T> {

	/**
	 * Gets this property for the item specified
	 * 
	 * @param item to get this property of
	 * @return The property of the material
	 */
	public T get(ItemStack item) {
		return item == null ? get(0) : get(item.getTypeId());
	}

	/**
	 * Gets this property for the material specified
	 * 
	 * @param material to get this property of
	 * @return The property of the material
	 */
	public T get(Material material) {
		return material == null ? get(0) : get(material.getId());
	}

	/**
	 * Gets this property for the block specified
	 * 
	 * @param block to get this property of
	 * @return The property of the material
	 */
	public T get(Block block) {
		return block == null ? get(0) : get(block.getTypeId());
	}

	/**
	 * Gets this property for the block specified
	 * 
	 * @param world the block is in
	 * @param x-coordinate of the block
	 * @param y-coordinate of the block
	 * @param z-coordinate of the block
	 * @return The property of the material
	 */
	public T get(Chunk chunk, int x, int y, int z) {
		return get(ChunkUtil.getBlockTypeId(chunk, x, y, z));
	}

	/**
	 * Gets this property for the block specified
	 * 
	 * @param world the block is in
	 * @param x-coordinate of the block
	 * @param y-coordinate of the block
	 * @param z-coordinate of the block
	 * @return The property of the material
	 */
	public T get(org.bukkit.World world, int x, int y, int z) {
		return get(world.getBlockTypeIdAt(x, y, z));
	}

	/**
	 * Gets this property for the material type Id specified
	 * 
	 * @param typeId of the material to get this property of
	 * @return The property of the material
	 */
	public abstract T get(int typeId);
}
