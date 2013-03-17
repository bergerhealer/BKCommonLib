package com.bergerkiller.bukkit.common.bases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Represents a class containing three immutable integer coordinates: x, y and z
 */
public class IntVector3 implements Comparable<IntVector3> {
	public final int x, y, z;

	public IntVector3(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	public IntVector3(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "{" + x + ", " + y + ", " + z + "}";
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof IntVector3) {
			IntVector3 other = (IntVector3) object;
			return this.x == other.x && this.y == other.y && this.z == other.z;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.x + this.z << 8 + this.y << 16;
	}

	@Override
	public int compareTo(IntVector3 other) {
		return this.y == other.y ? (this.z == other.z ? this.x - other.x : this.z - other.z) : this.y - other.y;
	}

	/**
	 * Gets the block at the coordinates of this IntVector3 on the world
	 * specified
	 * 
	 * @param world
	 *            to get a block of
	 * @return block at the world
	 */
	public Block toBlock(World world) {
		return world.getBlockAt(x, y, z);
	}

	/**
	 * Obtains the chunk coordinates, treating this IntVector3 as block
	 * coordinates
	 * 
	 * @return chunk coordinates
	 */
	public IntVector2 toChunkCoordinates() {
		return new IntVector2(x >> 4, z >> 4);
	}

	/**
	 * Converts this IntVector3 into an IntVector2 using the x/z coordinates
	 * 
	 * @return new IntVector2
	 */
	public IntVector2 toIntVector2() {
		return new IntVector2(x, z);
	}

	public static IntVector3 read(DataInputStream stream) throws IOException {
		return new IntVector3(stream.readInt(), stream.readInt(), stream.readInt());
	}

	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(this.x);
		stream.writeInt(this.y);
		stream.writeInt(this.z);
	}
}
