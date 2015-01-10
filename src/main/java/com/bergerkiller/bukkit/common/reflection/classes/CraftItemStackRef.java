package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.craftbukkit.inventory.CraftItemStack;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftItemStackRef {
	public static final FieldAccessor<Object> handle = new SafeField<Object>(CraftItemStack.class, "handle");
}
