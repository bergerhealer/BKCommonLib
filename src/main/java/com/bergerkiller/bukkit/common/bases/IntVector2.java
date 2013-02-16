package com.bergerkiller.bukkit.common.bases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.World;

import net.minecraft.server.v1_4_R1.ChunkCoordIntPair;

/**
 * Represents a class containing two immutable integer coordinates: x and z
 */
public class IntVector2 extends ChunkCoordIntPair {

	public IntVector2(Chunk chunk) {
		this(chunk.getX(), chunk.getZ());
	}

	public IntVector2(final int x, final int z) {
		super(x, z);
	}

	/**
	 * Gets or loads the chunk at the coordinates of this IntVector2 on the world specified
	 * 
	 * @param world to get a chunk of
	 * @return chunk at the world
	 */
	public Chunk toChunk(World world) {
		return world.getChunkAt(x, z);
	}

	public static IntVector2 read(DataInputStream stream) throws IOException {
		return new IntVector2(stream.readInt(), stream.readInt());
	}

	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(this.x);
		stream.writeInt(this.z);
	}
}
