package com.bergerkiller.bukkit.common.reflection.accessors;

import net.minecraft.server.v1_4_R1.ItemStack;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import com.bergerkiller.bukkit.common.utils.NativeUtil;

public class ItemStackFieldAccessor extends TranslatorFieldAccessor<org.bukkit.inventory.ItemStack, ItemStack> {

	public ItemStackFieldAccessor(FieldAccessor<?> base) {
		super(base);
	}

	@Override
	public org.bukkit.inventory.ItemStack convert(ItemStack value) {
		return NativeUtil.getItemStack(value);
	}

	@Override
	public ItemStack revert(org.bukkit.inventory.ItemStack value) {
		return NativeUtil.getNative(value);
	}
}
