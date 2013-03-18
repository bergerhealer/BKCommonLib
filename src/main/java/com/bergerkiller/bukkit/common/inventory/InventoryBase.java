package com.bergerkiller.bukkit.common.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.bases.IInventoryBase;
import com.bergerkiller.bukkit.common.proxies.CraftInventoryProxy;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * A basic implementation of Inventory that excludes the getting and setting of item content information.
 * Uses a backing CraftInventory to perform all utility methods provided by Bukkit.
 * This ensures that the functionality is up-to-date with the latest bugfixes and additions.
 */
public abstract class InventoryBase implements Inventory {
	private final CraftInventoryProxy proxy = new CraftInventoryProxy(new IInventoryBase(), this);

	@Override
	public abstract int getSize();

	@Override
	public abstract ItemStack getItem(int index);

	@Override
	public abstract void setItem(int index, ItemStack item);

	@Override
	public ItemStack[] getContents() {
		// Overridden because the NMS IInventory is used in CraftInventory
		final int size = getSize();
		ItemStack[] items = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			items[i] = ItemUtil.cloneItem(getItem(i));
		}
		return items;
	}

	@Override
	public void setContents(ItemStack[] items) {
		// Overridden because the NMS IInventory is used in CraftInventory
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
    	return InventoryType.CHEST;
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
	public InventoryHolder getHolder() {
		return null;
	}

	@Override
	public List<HumanEntity> getViewers() {
		return proxy.super_getViewers();
	}

	@Override
	public int getMaxStackSize() {
		return proxy.super_getMaxStackSize();
	}

	@Override
	public void setMaxStackSize(int size) {
		proxy.super_setMaxStackSize(size);
	}

	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items) throws IllegalArgumentException {
		return proxy.super_addItem(items);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(int materialId) {
		return proxy.super_all(materialId);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
		return proxy.super_all(material);
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
		return proxy.super_all(item);
	}

	@Override
	public void clear() {
		proxy.super_clear();
	}

	@Override
	public void clear(int index) {
		proxy.super_clear(index);
	}

	@Override
	public boolean contains(int materialId) {
		return proxy.super_contains(materialId);
	}

	@Override
	public boolean contains(Material material) throws IllegalArgumentException {
		return proxy.super_contains(material);
	}

	@Override
	public boolean contains(ItemStack item) {
		return proxy.super_contains(item);
	}

	@Override
	public boolean contains(int materialId, int amount) {
		return proxy.super_contains(materialId, amount);
	}

	@Override
	public boolean contains(Material material, int amount) throws IllegalArgumentException {
		return proxy.super_contains(material, amount);
	}

	@Override
	public boolean contains(ItemStack item, int amount) {
		return proxy.super_contains(item, amount);
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
		return proxy.super_containsAtLeast(item, amount);
	}

	@Override
	public int first(int materialId) {
		return proxy.super_first(materialId);
	}

	@Override
	public int first(Material material) throws IllegalArgumentException {
		return proxy.super_first(material);
	}

	@Override
	public int first(ItemStack item) {
		return proxy.super_first(item);
	}

	@Override
	public int firstEmpty() {
		return proxy.super_firstEmpty();
	}

	@Override
	public ListIterator<ItemStack> iterator() {
		return proxy.super_iterator();
	}

	@Override
	public ListIterator<ItemStack> iterator(int index) {
		return proxy.super_iterator(index);
	}

	@Override
	public void remove(int materialId) {
		proxy.super_remove(materialId);
	}

	@Override
	public void remove(Material material) throws IllegalArgumentException {
		proxy.super_remove(material);
	}

	@Override
	public void remove(ItemStack item) {
		proxy.super_remove(item);
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException {
		return proxy.super_removeItem(items);
	}

	@Override
	public int hashCode() {
		return proxy.super_hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return proxy.super_equals(obj);
	}

	@Override
	public String toString() {
		return proxy.super_toString();
	}
}
