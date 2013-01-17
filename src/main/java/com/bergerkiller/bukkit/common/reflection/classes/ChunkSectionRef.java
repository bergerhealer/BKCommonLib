package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_R1.ChunkSection;

public class ChunkSectionRef {
	public static int getTypeId(ChunkSection section, int x, int y, int z) {
		return section.a(x & 0xf, y & 0xf, z & 0xf);
	}

	public static void setTypeId(ChunkSection section, int x, int y, int z, int typeId) {
		section.a(x & 0xf, y & 0xf, z & 0xf, typeId);
	}

	public static int getData(ChunkSection section, int x, int y, int z) {
		return section.b(x & 0xf, y & 0xf, z & 0xf);
	}
	
	public static void setData(ChunkSection section, int x, int y, int z, int data) {
		section.b(x & 0xf, y & 0xf, z & 0xf, data);
	}

	public static int getSkyLight(ChunkSection section, int x, int y, int z) {
		return section.c(x & 0xf, y & 0xf, z & 0xf);
	}

	public static void setSkyLight(ChunkSection section, int x, int y, int z, int level) {
		section.c(x & 0xf, y & 0xf, z & 0xf, level);
	}

	public static int getBlockLight(ChunkSection section, int x, int y, int z) {
		return section.d(x & 0xf, y & 0xf, z & 0xf);
	}

	public static void setBlockLight(ChunkSection section, int x, int y, int z, int level) {
		section.d(x & 0xf, y & 0xf, z & 0xf, level);
	}
}
