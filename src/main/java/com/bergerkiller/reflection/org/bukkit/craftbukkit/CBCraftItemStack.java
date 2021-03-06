package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;

@Deprecated
public class CBCraftItemStack {
	public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.inventory.CraftItemStack");

    public static final FieldAccessor<Object> handle = T.selectField("net.minecraft.world.item.ItemStack handle");

    private static final SafeConstructor<?> constructor1 = T.getConstructor(ItemStackHandle.T.getType());
    public static ItemStack newInstanceFromHandle(Object nmsItemHandle) {
        return (ItemStack) constructor1.newInstance(nmsItemHandle);
    }
}
