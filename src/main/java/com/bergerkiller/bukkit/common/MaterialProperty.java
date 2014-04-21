package com.bergerkiller.bukkit.common;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.reflection.SafeMethod;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

/**
 * Represents a property for a given material<br>
 * If a material container is null, air (0) is used<br><br>
 * 
 * It is <b>required</b> to either implement {@link #get(int)} or {@link #get(Material)}.
 * Not doing so will result in a Runtime exception being thrown upon construction.
 */
public abstract class MaterialProperty<T> {
	private static final SafeMethod<Object> getMethodA = new SafeMethod<Object>(MaterialProperty.class, "get", int.class);
	private static final SafeMethod<Object> getMethodB = new SafeMethod<Object>(MaterialProperty.class, "get", Material.class);

	public MaterialProperty() {
		// Check whether the get(int) or get(Material) is overrided
		// If not, throw a runtime exception
		if (!getMethodA.isOverridedIn(this.getClass()) && !getMethodB.isOverridedIn(this.getClass())) {
			throw new RuntimeException("Either get(int typeId) or get(Material type) needs to be implemented!");
		}
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
		return get(WorldUtil.getBlockType(world, x, y, z));
	}

	/**
	 * Gets this property for the material specified
	 * 
	 * @param material to get this property of
	 * @return The property of the material
	 */
	public T get(Material material) {
		return material == null ? get(0) : get(MaterialUtil.getTypeId(material));
	}

	/**
	 * Gets this property for the material type Id specified
	 * 
	 * @param typeId of the material to get this property of
	 * @return The property of the material
	 */
	@Deprecated
	public T get(int typeId) {
		return get(MaterialUtil.getType(typeId));
	}
}
