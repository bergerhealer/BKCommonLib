package com.bergerkiller.bukkit.common.bases;

import java.util.Collections;
import java.util.List;

import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.IInventory;
import net.minecraft.server.v1_8_R1.ItemStack;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

/**
 * A base Inventory that excludes the getting and setting of items<br>
 * Avoid using this class as much as possible, use the Bukkit versions instead!
 */
public class IInventoryBase implements IInventory {

    private int maxstacksize = 64;

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

    @Override
    public ItemStack getItem(int arg0) {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void setItem(int arg0, ItemStack arg1) {
    }

    @Override
    public boolean b(int arg0, ItemStack arg1) {
        return false;
    }
    
    //TODO: Add somethings that work
    
    @Override
    public void startOpen(EntityHuman eh) {
    }

    @Override
    public void closeContainer(EntityHuman eh) {
        eh.closeInventory();
    }

    @Override
    public int getProperty(int i) {
        return -1;
    }

    @Override
    public void b(int i, int i1) {
    }

    @Override
    public int g() {
        return -1;
    }

    @Override
    public void l() {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return null;
    }
}
