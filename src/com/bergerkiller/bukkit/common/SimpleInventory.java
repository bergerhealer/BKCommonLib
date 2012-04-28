package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.bergerkiller.bukkit.common.utils.ItemUtil;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public class SimpleInventory implements IInventory {

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

	private int maxstacksize = 64;
	private ItemStack[] items;

	@Override
	public boolean a(EntityHuman arg0) {
		return false;
	}

	@Override
	public void f() {
	}

	@Override
	public void g() {
	}

	@Override
	public ItemStack[] getContents() {
		return this.items;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items[index];
	}

	@Override
	public String getName() {
		return "SimpleInventory";
	}

	@Override
	public InventoryHolder getOwner() {
		return null;
	}

	@Override
	public int getSize() {
		return this.items.length;
	}

	@Override
	public List<HumanEntity> getViewers() {
		return new ArrayList<HumanEntity>(0);
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
	public void setItem(int index, ItemStack stack) {
		this.items[index] = stack;
        if (stack != null && stack.count > this.getMaxStackSize()) {
        	stack.count = this.getMaxStackSize();
        }
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
        if (this.items[index] != null) {
            ItemStack itemstack;
            if (this.items[index].count <= size) {
                itemstack = this.items[index];
                this.items[index] = null;
                return itemstack;
            } else {
                itemstack = this.items[index].a(size);
                if (this.items[index].count == 0) {
                    this.items[index] = null;
                }
                return itemstack;
            }
        } else {
            return null;
        }
    }

	@Override
    public ItemStack splitWithoutUpdate(int index) {
        if (this.items[index] != null) {
            ItemStack itemstack = this.items[index];
            this.items[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }
	
	public Inventory getInventory() {
		return new CraftInventory(this);
	}
}
