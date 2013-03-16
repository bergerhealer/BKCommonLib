package com.bergerkiller.bukkit.common.inventory;

import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_5_R1.IInventory;

import org.bukkit.craftbukkit.v1_5_R1.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.bases.IInventoryBase;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * A basic implementation of Inventory that excludes the getting and setting of item content information
 */
public abstract class InventoryBase extends CraftInventory implements Inventory {
	private int maxStackSize = 64;

	public InventoryBase() {
		this(new BaseInventoryNative());
	}

	private InventoryBase(BaseInventoryNative internal) {
		super(internal);
		internal.owner = this;
	}

	/*
	 * This class is needed because CraftInventory uses a private getMaxItemStack method
	 * To still have proper variable max stack sizes, this class is needed
	 * Damnit Bukkit, why is something so simplistic made so difficult?
	 */
	private static class BaseInventoryNative extends IInventoryBase {
		private InventoryBase owner;

		@Override
		public int getMaxStackSize() {
			return owner.getMaxStackSize();
		}
	}

	@Override
	@Deprecated
	public IInventory getInventory() {
		return null;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public abstract int getSize();

	@Override
	public abstract ItemStack getItem(int index);

	@Override
	public abstract void setItem(int index, ItemStack item);

	@Override
	public List<HumanEntity> getViewers() {
		return Collections.emptyList();
	}

	@Override
	public void clear() {
		final int size = getSize();
		for (int i = 0; i < size; i++) {
			setItem(i, null);
		}
	}

	@Override
	public ItemStack[] getContents() {
		final int size = getSize();
		ItemStack[] items = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			items[i] = ItemUtil.cloneItem(getItem(i));
		}
		return items;
	}

	@Override
	public void setContents(ItemStack[] items) {
		final int size = getSize();
		if (size < items.length) {
			throw new IllegalArgumentException("Invalid inventory size; expected " + size + " or less");
		}
		for (int i = 0; i < size; i++) {
			setItem(i, i >= items.length ? null : items[i]);
		}
	}

	@Override
	public InventoryType getType() {
		return null;
	}

	@Override
	public InventoryHolder getHolder() {
		return null;
	}

	@Override
	public int getMaxStackSize() {
		return this.maxStackSize;
	}

	@Override
	public void setMaxStackSize(int size) {
		this.maxStackSize = size;
	}
}
