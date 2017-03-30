package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.SafeConstructor;
import com.bergerkiller.reflection.net.minecraft.server.NMSItemStack;

public class CBCraftItemStack {
	public static final ClassTemplate<?> T = ClassTemplate.createCB("inventory.CraftItemStack");

    public static final FieldAccessor<Object> handle = T.selectField("net.minecraft.server.ItemStack handle");

    private static final SafeConstructor<?> constructor1 = T.getConstructor(NMSItemStack.T.getType());
    public static ItemStack newInstanceFromHandle(Object nmsItemHandle) {
        return (ItemStack) constructor1.newInstance(nmsItemHandle);
    }
}
