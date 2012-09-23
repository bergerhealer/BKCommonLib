package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.EntityMinecart;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class EntityMinecartRef {
	public static final ClassTemplate<EntityMinecart> TEMPLATE = ClassTemplate.create(EntityMinecart.class);
	public static final SafeField<Integer> fuel = TEMPLATE.getField("e");
}
