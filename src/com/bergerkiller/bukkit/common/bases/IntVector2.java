package com.bergerkiller.bukkit.common.bases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.World;

import net.minecraft.server.ChunkCoordIntPair;

/**
 * Represents a class containing two immutable integer coordinates: x and z
 */
public class IntVector2 {
	public final int x;
	public final int z;

	public IntVector2(Chunk chunk) {
		this(chunk.getX(), chunk.getZ());
	}

	public IntVector2(final int x, final int z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public int hashCode() {
		long key = (long) this.x & 4294967295L | ((long) this.z & 4294967295L) << 32;
		return ((int) key) ^ ((int) (key >> 32));
	}

	/**
	 * Gets a new native Chunk Coord Int Pair class with the values of this IntVector2
	 * 
	 * @return pair
	 */
	public ChunkCoordIntPair toPair() {
		return new ChunkCoordIntPair(this.x, this.z);
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

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof IntVector2) {
			IntVector2 vec = (IntVector2) object;
			return vec.x == this.x && vec.z == this.z;
		}
		return false;
	}

	public static IntVector2 read(DataInputStream stream) throws IOException {
		return new IntVector2(stream.readInt(), stream.readInt());
	}

	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(this.x);
		stream.writeInt(this.z);
	}
}
