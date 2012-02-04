package com.bergerkiller.bukkit.common;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.utils.WorldUtil;


public class BlockLocation {
	public BlockLocation(ConfigurationNode node) {
		this.world = node.get("world", "world");
		this.x = node.get("x", 0);
		this.y = node.get("y", 0);
		this.z = node.get("z", 0);
	}
	public BlockLocation(Block block) {
		this(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}
	public BlockLocation(World world, final int x, final int y, final int z) {
		this.world = world == null ? null : world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public BlockLocation(final String world, final int x, final int y, final int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public final int x, y, z;
	public final String world;
	
	public static BlockLocation parseLocation(String value) {
		if (value.length() < 10) {
			return null;
		}
		try {
			StringBuilder world = new StringBuilder(value.length() - 9);
			String[] bits = value.split("_");
			int z = Integer.parseInt(bits[bits.length - 1]);
			int y = Integer.parseInt(bits[bits.length - 2]);
			int x = Integer.parseInt(bits[bits.length - 3]);
			//get world
			int worldbitcount = bits.length - 3;
			for (int i = 0; i < worldbitcount; i++) {
				world.append(bits[i]);
				if (i != worldbitcount - 1) world.append('_');
			}
			return new BlockLocation(world.toString(), x, y, z);
		} catch (Throwable t) {
			return null;
		}
	}
	
	public Chunk getChunk() {
		World world = this.getWorld();
		if (world == null) return null;
		return world.getChunkAt(this.x >> 4, this.z >> 4);
	}
	public Block getBlock() {
		World world = this.getWorld();
		return world == null ? null : world.getBlockAt(this.x, this.y, this.z);
	}
	public World getWorld() {
		return Bukkit.getServer().getWorld(this.world);
	}
	
	public boolean isLoaded() {
		return WorldUtil.isLoaded(this.getWorld(), this.x, this.y, this.z);
	}
	public boolean isIn(World world) {
		if (this.world == null && world == null) {
			return true;
		} else if (this.world == null || world == null) {
			return false;
		} else {
			return this.world.equals(world.getName());
		}
	}
	public boolean isIn(Chunk chunk) {
		if (chunk != null && this.isIn(chunk.getWorld())) {
			return (this.x >> 4) == chunk.getX() && (this.z >> 4) == chunk.getZ();
		}
		return false;
	}
	public boolean isIn(BlockLocation point1, BlockLocation point2) {
		if (point1 != null && point2 != null) {
			if (point1.world.equals(point2.world)&& point1.world.equals(this.world)) {
				int value;
				value = Math.min(point1.x, point2.x);
				if (this.x < value || this.x > (value + Math.abs(point1.x - point2.x))) return false;
				
				value = Math.min(point1.y, point2.y);
				if (this.y < value || this.y > (value + Math.abs(point1.y - point2.y))) return false;
				
				value = Math.min(point1.z, point2.z);
				if (this.z < value || this.z > (value + Math.abs(point1.z - point2.z))) return false;
				
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return this.world + "_" + this.x + "_" + this.y + "_" + this.z;
	}
	public boolean equals(Object object) {
		if (object == this) return true;
		if (object instanceof BlockLocation) {
			BlockLocation bloc = (BlockLocation) object;
			return bloc.x == this.x && bloc.y == this.y && bloc.z == this.z && bloc.world.equals(this.world);
		} else {
			return false;
		}
	}
	public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.world.hashCode();
        hash = 53 * hash + (this.x ^ (this.x >> 16));
        hash = 53 * hash + (this.y ^ (this.y >> 16));
        hash = 53 * hash + (this.z ^ (this.z >> 16));
        return hash;
	}
	public void save(ConfigurationNode node) {
		node.set("world", this.world);
		node.set("x", this.x);
		node.set("y", this.y);
		node.set("z", this.z);
	}
}
