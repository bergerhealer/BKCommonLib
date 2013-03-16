package com.bergerkiller.bukkit.common.bases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.block.Block;

import net.minecraft.server.v1_5_R1.ChunkPosition;

/**
 * Represents a class containing three immutable integer coordinates: x, y and z
 */
public class IntVector3 extends ChunkPosition {

	public IntVector3(Block block) {
		this(block.getX(), block.getY(), block.getZ());
	}

	public IntVector3(final int x, final int y, final int z) {
		super(x, y, z);
	}

	@Override
	public String toString() {
		return "{" + x + ", " + y + ", " + z + "}";
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

	public static IntVector3 read(DataInputStream stream) throws IOException {
		return new IntVector3(stream.readInt(), stream.readInt(), stream.readInt());
	}

	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(this.x);
		stream.writeInt(this.y);
		stream.writeInt(this.z);
	}
}
