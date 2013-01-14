package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.craftbukkit.v1_4_6.inventory.CraftItemStack;
import net.minecraft.server.v1_4_6.ItemStack;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftItemStackRef {
	public static final FieldAccessor<ItemStack> handle = new SafeField<ItemStack>(CraftItemStack.class, "handle");
}
