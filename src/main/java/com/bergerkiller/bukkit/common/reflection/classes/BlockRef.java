package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_5_R3.Explosion;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;

import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class BlockRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("Block");
	public static final Object[] byId = TEMPLATE.getStaticFieldValue("byId");
	public static final FieldAccessor<Integer> id = TEMPLATE.getField("id");
	public static final MethodAccessor<Void> dropNaturally = TEMPLATE.getMethod("dropNaturally", WorldRef.TEMPLATE.getType(), int.class, int.class, int.class, int.class, float.class, int.class);
	public static final MethodAccessor<Void> ignite = TEMPLATE.getMethod("wasExploded", WorldRef.TEMPLATE.getType(), int.class, int.class, int.class, Explosion.class);
	public static final MethodAccessor<Boolean> isSolid = TEMPLATE.getMethod("c");
	public static final MethodAccessor<Boolean> isPowerSource = TEMPLATE.getMethod("isPowerSource");

	public static Object getBlock(int id) {
		return LogicUtil.getArray(byId, id, null);
	}
}
