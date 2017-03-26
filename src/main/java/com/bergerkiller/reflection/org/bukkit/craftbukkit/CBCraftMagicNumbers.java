package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.MethodAccessor;

public class CBCraftMagicNumbers {
	public static ClassTemplate<?> T = ClassTemplate.createCB("util.CraftMagicNumbers");

	public static MethodAccessor<Integer> getId = T.selectMethod("public static int getId(net.minecraft.server.Block block)");
}
