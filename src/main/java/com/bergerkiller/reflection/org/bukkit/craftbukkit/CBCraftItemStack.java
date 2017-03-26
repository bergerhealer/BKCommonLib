package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class CBCraftItemStack {
	public static final ClassTemplate<?> T = ClassTemplate.createCB("inventory.CraftItemStack");

    public static final FieldAccessor<Object> handle = T.selectField("net.minecraft.server.ItemStack handle");
}
