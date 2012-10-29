package com.bergerkiller.bukkit.common;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

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
	 * Gets this property for the material type Id specified
	 * 
	 * @param typeId of the material to get this property of
	 * @return The property of the material
	 */
	public abstract T get(int typeId);
}
