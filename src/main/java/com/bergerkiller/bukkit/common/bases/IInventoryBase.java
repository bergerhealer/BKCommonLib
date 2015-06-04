package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.conversion.ChatComponentConvertor;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IInventory;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collections;
import java.util.List;

/**
 * A base Inventory that excludes the getting and setting of items<br>
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

    public void startOpen() {
        startOpen(null);
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

    public void closeContainer() {
        closeContainer(null);
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

    public String getInventoryName() {
        return getClass().getSimpleName();
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

    public boolean k_() {
        return false;
    }

    @Override
    public String getName() {
        return getInventoryName();
    }

    @Override
    public boolean hasCustomName() {
        return k_();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return ChatComponentConvertor.convertTextToIChatBaseComponent(getInventoryName());
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
    }

    @Override
    public void closeContainer(EntityHuman entityhuman) {
    }

    @Override
    public int getProperty(int i) {
        return 0;
    }

    @Override
    public void b(int i, int j) {
    }

    @Override
    public int g() {
        return 0;
    }

    @Override
    public void l() {
    }
}
