package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class BlockRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("Block");
	public static final Object[] byId = TEMPLATE.getStaticFieldValue("byId");
	private static final MethodAccessor<Void> dropNaturally = TEMPLATE.getMethod("dropNaturally", WorldRef.TEMPLATE.getType(), int.class, int.class, int.class, int.class, float.class, int.class);
	private static final MethodAccessor<Void> ignite = TEMPLATE.getMethod("wasExploded", WorldRef.TEMPLATE.getType(), int.class, int.class, int.class);

	public static void dropNaturally(Object blockHandle, World world, int x, int y, int z, int data, float yield) {
		dropNaturally(blockHandle, world, x, y, z, data, yield, 0);
	}

	public static void dropNaturally(Object blockHandle, World world, int x, int y, int z, int data, float yield, int chanceFactor) {
		dropNaturally.invoke(blockHandle, Conversion.toWorldHandle.convert(world), x, y, z, data, yield, chanceFactor);
	}

	public static void ignite(Object blockHandle, World world, int x, int y, int z) {
		ignite.invoke(blockHandle, Conversion.toWorldHandle.convert(world), x, y, z);
	}

	public static Object getBlock(int id) {
		return LogicUtil.isInBounds(byId, id) ? byId[id] : null;
	}
}
