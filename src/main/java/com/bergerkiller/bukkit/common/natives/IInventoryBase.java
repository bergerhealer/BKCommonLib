package com.bergerkiller.bukkit.common.natives;

import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_4_5.EntityHuman;
import net.minecraft.server.v1_4_5.IInventory;
import net.minecraft.server.v1_4_5.ItemStack;

import org.bukkit.craftbukkit.v1_4_5.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_4_5.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * An base Inventory that excludes the getting and setting of items<br>
 * Avoid using this class as much as possible, use the Bukkit versions instead!
 */
public class IInventoryBase implements IInventory {
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
	public boolean a_(EntityHuman arg0) {
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

	@Override
	public ItemStack getItem(int arg0) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public void setItem(int arg0, ItemStack arg1) {
	}
}
