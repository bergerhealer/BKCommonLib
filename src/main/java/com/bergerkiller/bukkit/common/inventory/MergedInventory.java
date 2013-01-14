package com.bergerkiller.bukkit.common.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class MergedInventory extends InventoryBase {
	private final Inventory[] inventories;
	private final int size;

	public MergedInventory(Inventory... inventories) {
		this.inventories = inventories;
		int size = 0;
		for (Inventory inventory : inventories) {
			size += inventory.getSize();
		}
		this.size = size;
	}

	@Override
	public ItemStack[] getContents() {
		if (LogicUtil.nullOrEmpty(this.inventories)) {
			return new ItemStack[0];
		} else if (this.inventories.length == 1) {
			return this.inventories[0].getContents();
		} else {
			ItemStack[] rval = new ItemStack[this.getSize()];
			int i = 0;
			for (Inventory inv : this.inventories) {
				for (ItemStack stack : inv.getContents()) {
					rval[i] = stack;
					i++;
				}
			}
			return rval;
		}
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public ItemStack getItem(int index) {
		int size;
		for (Inventory inventory : this.inventories) {
			size = inventory.getSize();
			if (index < size) {
				return inventory.getItem(index);
			} else {
				index -= size;
			}
		}
		return null;
	}

	@Override
	public void setItem(int index, ItemStack arg1) {
		int size;
		for (Inventory inventory : this.inventories) {
			size = inventory.getSize();
			if (index < size) {
				inventory.setItem(index, arg1);
				return;
			} else {
				index -= size;
			}
		}
	}
}
