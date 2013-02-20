package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.ChunkSection;
import net.minecraft.server.v1_4_R1.EnumSkyBlock;

public class ChunkRef {
	private static final Class<?> icp = CommonUtil.getNMSClass("IChunkProvider");
	public static final int XZ_MASK = 0xf;
	public static final int Y_MASK = 0xff;
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("Chunk");
	public static final FieldAccessor<Integer> x = TEMPLATE.getField("x");
	public static final FieldAccessor<Integer> z = TEMPLATE.getField("z");
	public static final MethodAccessor<byte[]> biomeData = TEMPLATE.getMethod("m");
	public static final MethodAccessor<Object[]> sections = TEMPLATE.getMethod("i");
	public static final FieldAccessor<Boolean> seenByPlayer = TEMPLATE.getField("seenByPlayer");
	private static final MethodAccessor<Void> addEntities = TEMPLATE.getMethod("addEntities");
	private static final MethodAccessor<Void> loadNeighbours = TEMPLATE.getMethod("a", icp, icp, int.class, int.class);
	public static final FieldAccessor<Object> world = TEMPLATE.getField("world");
	public static final FieldAccessor<Map<?, ?>> tileEntities = TEMPLATE.getField("tileEntities");
	public static final FieldAccessor<List<Object>[]> entitySlices = TEMPLATE.getField("entitySlices");
	public static final FieldAccessor<Object> worldProvider = new SafeField<Object>(CommonUtil.getNMSClass("World"), "worldProvider");
	public static final FieldAccessor<Boolean> hasSkyLight = new SafeField<Boolean>(CommonUtil.getNMSClass("WorldProvider"), "f");

	public static void loadNeighbours(Object chunkHandle, Object chunkProvider1, Object chunkProvider2, int x, int z) {
		loadNeighbours.invoke(chunkHandle, chunkProvider1, chunkProvider2, x, z);
	}

	public static void addEntities(Object chunkHandle) {
		addEntities.invoke(chunkHandle);
	}

	/**
	 * Gets all chunk sections contained in a chunk
	 */
	public static ChunkSection[] getSections(Object chunkHandle) {
		return (ChunkSection[]) sections.invoke(chunkHandle);
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
