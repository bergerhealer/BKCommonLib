package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.craftbukkit.v1_4_R1.inventory.CraftItemStack;
import net.minecraft.server.v1_4_R1.ItemStack;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftItemStackRef {
	public static final FieldAccessor<ItemStack> handle = new SafeField<ItemStack>(CraftItemStack.class, "handle");
}
