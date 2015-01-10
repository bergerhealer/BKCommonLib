package com.bergerkiller.bukkit.common.inventory;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * A basic inventory implementation that uses a backing array of ItemStacks
 */
public class InventoryBaseImpl extends InventoryBase {
	private final ItemStack[] items;

	public InventoryBaseImpl(int size) {
		this(new ItemStack[size], false);
	}

	public InventoryBaseImpl(Collection<ItemStack> contents) {
		this(contents, true);
	}

	public InventoryBaseImpl(Collection<ItemStack> contents, boolean clone) {
		this(contents.toArray(new ItemStack[0]), false);
		if (clone) {
			for (int i = 0; i < this.items.length; i++) {
				this.items[i] = ItemUtil.cloneItem(this.items[i]);
			}
		}
	}

	public InventoryBaseImpl(ItemStack[] contents) {
		this(contents, true);
	}

	public InventoryBaseImpl(ItemStack[] contents, boolean clone) {
		if (clone) {
			this.items = ItemUtil.cloneItems(contents);
		} else {
			this.items = contents;
		}
	}

	@Override
	public int getSize() {
		return this.items.length;
	}

	@Override
	public void setItem(int index, ItemStack item) {
		this.items[index] = ItemUtil.cloneItem(item);
	}

	@Override
	public ItemStack getItem(int index) {
		return ItemUtil.cloneItem(this.items[index]);
	}

	@Override
	public ItemStack[] getContents() {
		return ItemUtil.cloneItems(this.items);
	}
}
