package com.bergerkiller.bukkit.common.proxies;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class InventoryProxy extends ProxyBase<Inventory> implements Inventory {

    static {
        validate(InventoryProxy.class);
    }

    public InventoryProxy(Inventory base) {
        super(base);
    }

    @Override
    public int getSize() {
        return base.getSize();
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public ItemStack getItem(int index) {
        return base.getItem(index);
    }

    @Override
    public ItemStack[] getContents() {
        return base.getContents();
    }

    @Override
    public void setContents(ItemStack[] items) {
        base.setContents(items);
    }

    // @Override // Since >= v1.10.2
    public ItemStack[] getStorageContents() {
        if (InventoryHandle.T.getStorageContents.isAvailable()) {
            return InventoryHandle.T.getStorageContents.invoke(base);
        } else {
            return new ItemStack[0];
        }
    }

    // @Override // Since >= v1.10.2
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        if (InventoryHandle.T.setStorageContents.isAvailable()) {
            InventoryHandle.T.setStorageContents.invoke(base, itemStacks);
        } else {
            // Do nothing.
        }
    }

    // @Override // Since >= v1.10.2
    public Location getLocation() {
        if (InventoryHandle.T.getLocation.isAvailable()) {
            return InventoryHandle.T.getLocation.invoke(base);
        } else {
            return null;
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        base.setItem(index, item);
    }

    @Deprecated
    public boolean contains(int materialId) {
        return base.contains(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public boolean contains(Material material) {
        return base.contains(material);
    }

    @Override
    public boolean contains(ItemStack item) {
        return base.contains(item);
    }

    @Deprecated
    public boolean contains(int materialId, int amount) {
        return base.contains(CommonLegacyMaterials.getMaterialFromId(materialId), amount);
    }

    @Override
    public boolean contains(Material material, int amount) {
        return base.contains(material, amount);
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return base.contains(item, amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        return base.containsAtLeast(item, amount);
    }

    @Deprecated
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return base.all(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        return base.all(material);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return base.all(item);
    }

    @Deprecated
    public int first(int materialId) {
        return base.first(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public int first(Material material) {
        return base.first(material);
    }

    @Override
    public int first(ItemStack item) {
        return base.first(item);
    }

    @Override
    public int firstEmpty() {
        return base.firstEmpty();
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        return base.addItem(items);
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        return base.removeItem(items);
    }

    @Deprecated
    public void remove(int materialId) {
        base.remove(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public void remove(Material material) {
        base.remove(material);
    }

    @Override
    public void remove(ItemStack item) {
        base.remove(item);
    }

    @Override
    public void clear(int index) {
        base.clear(index);
    }

    @Override
    public void clear() {
        base.clear();
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return base.iterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        return base.iterator(index);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return base.getViewers();
    }

    @Override
    public String getTitle() {
        return base.getTitle();
    }

    @Override
    public InventoryType getType() {
        return base.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return base.getHolder();
    }

    @Override
    public int getMaxStackSize() {
        return base.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        base.setMaxStackSize(size);
    }

}
