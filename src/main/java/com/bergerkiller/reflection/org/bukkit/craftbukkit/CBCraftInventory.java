package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

public class CBCraftInventory {
	public static final ClassTemplate<?> T = ClassTemplate.createCB("inventory.CraftInventory")
			.addImport("net.minecraft.server.IInventory");
	
	public static final FieldAccessor<Object> handle = T.selectField("protected final IInventory inventory");
}
