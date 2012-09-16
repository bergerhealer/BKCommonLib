package com.bergerkiller.bukkit.common;

import java.util.Collections;
import java.util.List;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * An Inventory that excludes the getting and setting of items<br>
 * All other logic has been implemented
 */
public abstract class IInventoryBase implements IInventory {
	private int maxstacksize = 64;

	public void setContents(ItemStack[] items) {
		for (int i = 0; i < items.length; i++) {
			this.setItem(i, items[i]);
		}
	}

	@Override
	public ItemStack[] getContents() {
		ItemStack[] contents = new ItemStack[this.getSize()];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = this.getItem(i);
		}
		return contents;
	}

	@Override
	public InventoryHolder getOwner() {
		return null;
	}

	@Override
	public void startOpen() {
	}

	@Override
	public List<HumanEntity> getViewers() {
		return Collections.emptyList();
	}

	@Override
	public void onClose(CraftHumanEntity arg0) {
	}

	@Override
	public void onOpen(CraftHumanEntity arg0) {
	}

	@Override
	public void update() {
	}

	@Override
	public boolean a(EntityHuman arg0) {
		return false;
	}

	@Override
	public void f() {
	}

	@Override
	public int getMaxStackSize() {
		return this.maxstacksize;
	}

	@Override
	public void setMaxStackSize(int size) {
		this.maxstacksize = size;
	}

	public ItemStack limitStack(ItemStack itemstack) {
		if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
			itemstack.count = this.getMaxStackSize();
		}
		return itemstack;
	}

	@Override
	public ItemStack splitStack(int index, int size) {
		ItemStack item = this.getItem(index);
		if (item != null) {
			ItemStack itemstack;
			if (item.count <= size) {
				itemstack = item;
				this.setItem(index, null);
				return itemstack;
			} else {
				itemstack = item.a(size);
				if (item.count == 0) {
					this.setItem(index, null);
				}
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack splitWithoutUpdate(int index) {
		ItemStack item = this.getItem(index);
		if (item != null) {
			this.setItem(index, null);
			return item;
		} else {
			return null;
		}
	}

	public Inventory getInventory() {
		return new CraftInventory(this);
	}
}
