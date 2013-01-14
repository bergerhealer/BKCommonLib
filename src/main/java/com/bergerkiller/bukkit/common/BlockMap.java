package com.bergerkiller.bukkit.common;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.bases.IntVector3;

public class BlockMap<T> extends HashMap<BlockLocation, T> {
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

	public T get(World world, IntVector3 coord) {
		return this.get(world.getName(), coord);
	}

	public T get(final String world, IntVector3 coord) {
		return this.get(world, coord.x, coord.y, coord.z);
	}

	public T get(World world, final int x, final int y, final int z) {
		return this.get(world.getName(), x, y, z);
	}

	public T get(final String world, final int x, final int y, final int z) {
		return super.get(new BlockLocation(world, x, y, z));
	}

	public T get(Block block) {
		return super.get(new BlockLocation(block));
	}

	public T put(World world, IntVector3 coord, T value) {
		return this.put(world.getName(), coord, value);
	}

	public T put(final String world, IntVector3 coord, T value) {
		return this.put(world, coord.x, coord.y, coord.z, value);
	}

	public T put(Block block, T value) {
		return super.put(new BlockLocation(block), value);
	}

	public T put(World world, final int x, final int y, final int z, T value) {
		return this.put(world.getName(), x, y, z, value);
	}

	public T put(final String world, final int x, final int y, final int z, T value) {
		return super.put(new BlockLocation(world, x, y, z), value);
	}

	public T remove(World world, IntVector3 coord) {
		return this.remove(world.getName(), coord);
	}

	public T remove(final String world, IntVector3 coord) {
		return this.remove(world, coord.x, coord.y, coord.z);
	}

	public T remove(World world, final int x, final int y, final int z) {
		return this.remove(world.getName(), x, y, z);
	}

	public T remove(final String world, final int x, final int y, final int z) {
		return super.remove(new BlockLocation(world, x, y, z));
	}

	public T remove(Block block) {
		return super.remove(new BlockLocation(block));
	}

}