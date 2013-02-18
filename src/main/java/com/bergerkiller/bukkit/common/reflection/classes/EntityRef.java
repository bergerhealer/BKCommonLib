package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class EntityRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("Entity");
	public static final FieldAccessor<org.bukkit.entity.Entity> bukkitEntity = TEMPLATE.getField("bukkitEntity");
	public static final FieldAccessor<Integer> chunkX = TEMPLATE.getField("ai");
	public static final FieldAccessor<Integer> chunkY = TEMPLATE.getField("aj");
	public static final FieldAccessor<Integer> chunkZ = TEMPLATE.getField("ak");
	public static final FieldAccessor<Boolean> positionChanged = TEMPLATE.getField("am");
}
