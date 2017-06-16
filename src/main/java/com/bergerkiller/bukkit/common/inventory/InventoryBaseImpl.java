package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

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
        this(contents.toArray(new ItemStack[contents.size()]), false);
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
        this.items[index] = item;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items[index];
    }

    @Override
    public ItemStack[] getContents() {
        return this.items;
    }

    //TODO FIX
    @Override
    public ItemStack[] getStorageContents() {
        return new ItemStack[0];
    }

    //TODO FIX
    @Override
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {

    }

    Location loc = null;
    
	@Override
	public Location getLocation() {
		return loc;
	}
	public void setLocation(Location loc){
		this.loc = loc;
	}
}
