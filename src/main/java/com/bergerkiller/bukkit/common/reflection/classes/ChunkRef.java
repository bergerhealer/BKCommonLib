package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkSection;
import net.minecraft.server.EnumSkyBlock;

public class ChunkRef {
	public static final int XZ_MASK = 0xf;
	public static final int Y_MASK = 0xff;

	/**
	 * Gets all chunk sections contained in a chunk
	 */
	public static ChunkSection[] getSections(Chunk chunk) {
		return chunk.i();
	}

	/**
	 * Gets the y-coordinate of the highest chunk section
	 * 
	 * @param chunk to get it from
	 * @return chunk section highest y-position
	 */
	public static int getTopSectionY(Chunk chunk) {
		return chunk.h();
	}

	public static int getHeight(Chunk chunk, int x, int z) {
		return chunk.b(x & XZ_MASK, z & XZ_MASK);
	}

	public static int getBlockLight(Chunk chunk, int x, int y, int z) {
		return getBrightness(chunk, x, y, z, EnumSkyBlock.BLOCK);
	}

	public static int getSkyLight(Chunk chunk, int x, int y, int z) {
		return getBrightness(chunk, x, y, z, EnumSkyBlock.SKY);
	}

	private static int getBrightness(Chunk chunk, int x, int y, int z, EnumSkyBlock mode) {
		if (y < 0) {
			return 0;
		} else if (y >= chunk.world.getWorld().getMaxHeight()) {
			return mode.c;
		}
		return chunk.getBrightness(mode, x & XZ_MASK, y, z & XZ_MASK);
	}

	public static boolean setBlock(Chunk chunk, int x, int y, int z, int typeId, int data) {
		return chunk.a(x & XZ_MASK, y, z & XZ_MASK, typeId, data);
	}

	public static int getData(Chunk chunk, int x, int y, int z) {
		return chunk.getData(x & XZ_MASK, y, z & XZ_MASK);
	}

	public static int getTypeId(Chunk chunk, int x, int y, int z) {
		return chunk.getTypeId(x & XZ_MASK, y, z & XZ_MASK);
	}
}
