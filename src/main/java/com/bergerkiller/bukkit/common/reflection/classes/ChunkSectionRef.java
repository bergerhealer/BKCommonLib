package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class ChunkSectionRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("ChunkSection");
	private static final MethodAccessor<Integer> blocks = TEMPLATE.getMethod("a", int.class, int.class, int.class);
	private static final MethodAccessor<Void> blocksWithType = TEMPLATE.getMethod("a", int.class, int.class, int.class, int.class);
	private static final MethodAccessor<Integer> getData = TEMPLATE.getMethod("b", int.class, int.class, int.class);
	private static final MethodAccessor<Void> setData = TEMPLATE.getMethod("b", int.class, int.class, int.class, int.class);
	private static final MethodAccessor<Integer> getSkyLight = TEMPLATE.getMethod("c", int.class, int.class, int.class);
	private static final MethodAccessor<Void> setSkyLight = TEMPLATE.getMethod("c", int.class, int.class, int.class, int.class);
	private static final MethodAccessor<Integer> getBlockLight = TEMPLATE.getMethod("d", int.class, int.class, int.class);
	private static final MethodAccessor<Void> setBlockLight = TEMPLATE.getMethod("d", int.class, int.class, int.class, int.class);
	
	public static final MethodAccessor<Boolean> blockCount = TEMPLATE.getMethod("a");
	public static final MethodAccessor<byte[]> blockIds = TEMPLATE.getMethod("g");
	public static final MethodAccessor<Object> extBlockIds = TEMPLATE.getMethod("i");
	public static final MethodAccessor<Object> blockData = TEMPLATE.getMethod("j");
	public static final MethodAccessor<Object> blockLight = TEMPLATE.getMethod("k");
	public static final MethodAccessor<Object> skyLight = TEMPLATE.getMethod("l");
	
	public static int getTypeId(Object section, int x, int y, int z) {
		return blocks.invoke(section, x & 0xf, y  & 0xf, z & 0xf);
	}

	public static void setTypeId(Object section, int x, int y, int z, int typeId) {
		blocksWithType.invoke(section, x & 0xf, y & 0xf, z & 0xf, typeId);
	}

	public static int getData(Object section, int x, int y, int z) {
		return getData.invoke(section, z & 0xf, y & 0xf, z & 0xf);
	}
	
	public static void setData(Object section, int x, int y, int z, int data) {
		setData.invoke(section, x & 0xf, y & 0xf, z & 0xf, data);
	}

	public static int getSkyLight(Object section, int x, int y, int z) {
		return getSkyLight.invoke(section, x & 0xf, y & 0xf, z & 0xf);
	}

	public static void setSkyLight(Object section, int x, int y, int z, int level) {
		setSkyLight.invoke(section, x & 0xf, y & 0xf, z & 0xf, level);
	}

	public static int getBlockLight(Object section, int x, int y, int z) {
		return getBlockLight.invoke(section,x & 0xf, y & 0xf, z & 0xf);
	}

	public static void setBlockLight(Object section, int x, int y, int z, int level) {
		setBlockLight.invoke(section, x & 0xf, y & 0xf, z & 0xf, level);
	}
}
