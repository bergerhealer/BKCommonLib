package com.bergerkiller.bukkit.common.bases;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * Represents a class containing two immutable integer coordinates: x and z
 */
public class IntVector2 {
	public static final IntVector2 ZERO = new IntVector2(0, 0);
	public final int x, z;

	public IntVector2(Chunk chunk) {
		this(chunk.getX(), chunk.getZ());
	}

	public IntVector2(final double x, final double z) {
		this(MathUtil.floor(x), MathUtil.floor(z));
	}

	public IntVector2(final int x, final int z) {
		this.x = x;
		this.z = z;
	}

	public IntVector2 add(int x, int z) {
		return new IntVector2(this.x + x, this.z + z);
	}

	public IntVector2 add(IntVector2 other) {
		return add(other.x, other.z);
	}

	public IntVector2 subtract(int x, int z) {
		return new IntVector2(this.x - x, this.z - z);
	}

	public IntVector2 subtract(IntVector2 other) {
		return subtract(other.x, other.z);
	}

	public IntVector2 multiply(int x, int z) {
		return new IntVector2(this.x * x, this.z * z);
	}

	public IntVector2 multiply(double x, double z) {
		return new IntVector2((double) this.x * x, (double) this.z * z);
	}

	public IntVector2 multiply(int factor) {
		return multiply(factor, factor);
	}

	public IntVector2 multiply(double factor) {
		return multiply(factor, factor);
	}

	public IntVector2 multiply(IntVector2 other) {
		return multiply(other.x, other.z);
	}

	public IntVector2 abs() {
		return new IntVector2(Math.abs(x), Math.abs(z));
	}

	/**
	 * Checks whether the x or z coordinate is greater than the value specified
	 * 
	 * @param value to check
	 * @return True if x/z > value, False otherwise
	 */
	public boolean greaterThan(int value) {
		return x > value || z > value;
	}

	/**
	 * Checks whether the x or z coordinate is greater/equal than the value specified
	 * 
	 * @param value to check
	 * @return True if x/z >= value, False otherwise
	 */
	public boolean greaterEqualThan(int value) {
		return x >= value || z >= value;
	}
	
	/**
	 * Gets or loads the chunk at the coordinates of this IntVector2 on the
	 * world specified
	 * 
	 * @param world to get a chunk of
	 * @return chunk at the world
	 */
	public Chunk toChunk(World world) {
		return world.getChunkAt(x, z);
	}

	/**
	 * Gets the X-coordinate of the middle of 'the' block
	 * 
	 * @return block middle X
	 */
	public double midX() {
		return (double) x + 0.5;
	}

	/**
	 * Gets the Z-coordinate of the middle of 'the' block
	 * 
	 * @return block middle Z
	 */
	public double midZ() {
		return (double) z + 0.5;
	}

	@Override
	public int hashCode() {
		long i = (long) this.x & 4294967295L | ((long) this.z & 4294967295L) << 32;
		int j = (int) i;
		int k = (int) (i >> 32);
		return j ^ k;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		} else if (object instanceof IntVector2) {
			IntVector2 iv3 = (IntVector2) object;
			return iv3.x == this.x && iv3.z == this.z;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "{" + x + ", " + z + "}";
	}

	/**
	 * Constructs a new IntVector3 with the x and z coordinates of this
	 * IntVector2, and the y-value specified
	 * 
	 * @param y
	 *            - coordinate
	 * @return new IntVector3
	 */
	public IntVector3 toIntVector3(int y) {
		return new IntVector3(x, y, z);
	}

	public static IntVector2 read(DataInputStream stream) throws IOException {
		return new IntVector2(stream.readInt(), stream.readInt());
	}

	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(this.x);
		stream.writeInt(this.z);
	}
}
