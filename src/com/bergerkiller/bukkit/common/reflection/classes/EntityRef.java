package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.SafeField;

import net.minecraft.server.Entity;

public class EntityRef {
	public static final SafeField<org.bukkit.entity.Entity> bukkitEntity = new SafeField<org.bukkit.entity.Entity>(Entity.class, "bukkitEntity");
}
