package com.bergerkiller.bukkit.common;

import java.util.Collection;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public class SimpleInventory extends IInventoryBase {
	private ItemStack[] items;

	public SimpleInventory(Inventory inventory) {
		this(inventory.getContents());
	}

	public SimpleInventory(IInventory inventory) {
		this(inventory.getContents());
	}

	public SimpleInventory(org.bukkit.inventory.ItemStack... items) {
		this.items = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			org.bukkit.inventory.ItemStack item = items[i];
			this.items[i] = item == null ? null : ItemUtil.getNative(items[i]);
		}
	}

	public SimpleInventory(Collection<?> items) {
		this.items = new ItemStack[items.size()];
		int i = 0;
		for (Object item : items) {
			if (item instanceof ItemStack) {
				this.items[i] = (ItemStack) item;
			} else if (item instanceof CraftItemStack) {
				this.items[i] = ((CraftItemStack) item).getHandle();
			} else {
				this.items[i] = null;
			}
			i++;
		}
	}

	public SimpleInventory(ItemStack... items) {
		this.items = items;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items[index];
	}

	@Override
	public String getName() {
		return "Simple Inventory";
	}

	@Override
	public int getSize() {
		return this.items.length;
	}

	@Override
	public ItemStack[] getContents() {
		return this.items;
	}

	@Override
	public void setContents(ItemStack[] items) {
		this.items = items;
	}

	@Override
	public void setItem(int index, ItemStack itemstack) {
		this.items[index] = limitStack(itemstack);
	}
}
