package com.bergerkiller.bukkit.common.collections;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * A Map implementation for mapping values to Block Locations (offline Blocks)
 * 
 * @param <V> - Value type to map to Block Location keys
 */
public class BlockMap<V> extends HashMap<BlockLocation, V> {
	private static final long serialVersionUID = 1L;

	public boolean containsKey(World world, IntVector3 coord) {
		return this.containsKey(world.getName(), coord);
	}

	public boolean containsKey(final String world, IntVector3 coord) {
		return this.containsKey(world, coord.x, coord.y, coord.z);
	}

	public boolean containsKey(final String world, final int x, final int y, final int z) {
		return super.containsKey(new BlockLocation(world, x, y, z));
	}

	public boolean containsKey(Block block) {
		return super.containsKey(new BlockLocation(block));
	}

	public V get(World world, IntVector3 coord) {
		return this.get(world.getName(), coord);
	}

	public V get(final String world, IntVector3 coord) {
		return this.get(world, coord.x, coord.y, coord.z);
	}

	public V get(World world, final int x, final int y, final int z) {
		return this.get(world.getName(), x, y, z);
	}

	public V get(final String world, final int x, final int y, final int z) {
		return super.get(new BlockLocation(world, x, y, z));
	}

	public V get(Block block) {
		return super.get(new BlockLocation(block));
	}

	public V put(World world, IntVector3 coord, V value) {
		return this.put(world.getName(), coord, value);
	}

	public V put(final String world, IntVector3 coord, V value) {
		return this.put(world, coord.x, coord.y, coord.z, value);
	}

	public V put(Block block, V value) {
		return super.put(new BlockLocation(block), value);
	}

	public V put(World world, final int x, final int y, final int z, V value) {
		return this.put(world.getName(), x, y, z, value);
	}

	public V put(final String world, final int x, final int y, final int z, V value) {
		return super.put(new BlockLocation(world, x, y, z), value);
	}

	public V remove(World world, IntVector3 coord) {
		return this.remove(world.getName(), coord);
	}

	public V remove(final String world, IntVector3 coord) {
		return this.remove(world, coord.x, coord.y, coord.z);
	}

	public V remove(World world, final int x, final int y, final int z) {
		return this.remove(world.getName(), x, y, z);
	}

	public V remove(final String world, final int x, final int y, final int z) {
		return super.remove(new BlockLocation(world, x, y, z));
	}

	public V remove(Block block) {
		return super.remove(new BlockLocation(block));
	}

}