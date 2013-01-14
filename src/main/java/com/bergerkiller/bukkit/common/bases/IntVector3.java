package com.bergerkiller.bukkit.common.bases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.block.Block;

import net.minecraft.server.v1_4_6.ChunkPosition;

/**
 * Represents a class containing three immutable integer coordinates: x, y and z
 */
public class IntVector3 {
	public final int x;
	public final int y;
	public final int z;

	public IntVector3(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	public IntVector3(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int hashCode() {
		return this.x + this.z << 8 + this.y << 16;
	}

	/**
	 * Gets a new native Chunk Position class with the values of this IntVector3
	 * 
	 * @return position
	 */
	public ChunkPosition toPosition() {
		return new ChunkPosition(this.x, this.y, this.z);
	}

	/**
	 * Gets the block at the coordinates of this IntVector3 on the world specified
	 * 
	 * @param world to get a block of
	 * @return block at the world
	 */
	public Block toBlock(World world) {
		return world.getBlockAt(x, y, z);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof IntVector3) {
			IntVector3 vec = (IntVector3) object;
			return vec.x == this.x && vec.y == this.y && vec.z == this.z;
		}
		return false;
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
