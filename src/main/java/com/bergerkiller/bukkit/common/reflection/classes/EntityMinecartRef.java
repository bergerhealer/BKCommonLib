package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_R1.EntityMinecart;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

public class EntityMinecartRef {
	public static final ClassTemplate<EntityMinecart> TEMPLATE = ClassTemplate.create(EntityMinecart.class);
	public static final FieldAccessor<Integer> fuel = TEMPLATE.getField("e");
}
