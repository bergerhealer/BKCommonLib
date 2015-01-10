package com.bergerkiller.bukkit.common.proxies;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.server.v1_8_R1.IInventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * A Craft Inventory proxy class. To call methods in the base class, call the
 * regular methods. To call methods from the current implementation, call the
 * super_ methods.
 */
@SuppressWarnings("deprecation")
public class CraftInventoryProxy extends CraftInventory implements Proxy<Inventory> {

    private Inventory base;

    public CraftInventoryProxy(Object iInventory, Inventory base) {
        super((IInventory) iInventory);
        setProxyBase(base);
    }

    public IInventory super_getInventory() {
        return super.getInventory();
    }

    @Override
    public void setProxyBase(Inventory base) {
        this.base = base;
    }

    @Override
    public Inventory getProxyBase() {
        return this.base;
    }

    @Override
    public int getSize() {
        return base.getSize();
    }

    public int super_getSize() {
        return super.getSize();
    }

    @Override
    public String getName() {
        return base.getName();
    }

    public String super_getName() {
        return super.getName();
    }

    @Override
    public ItemStack getItem(int index) {
        return base.getItem(index);
    }

    public ItemStack super_getItem(int index) {
        return super.getItem(index);
    }

    @Override
    public ItemStack[] getContents() {
        return base.getContents();
    }

    public ItemStack[] super_getContents() {
        return super.getContents();
    }

    @Override
    public void setContents(ItemStack[] items) {
        base.setContents(items);
    }

    public void super_setContents(ItemStack[] items) {
        super.setContents(items);
    }

    @Override
    public void setItem(int index, ItemStack item) {
        base.setItem(index, item);
    }

    public void super_setItem(int index, ItemStack item) {
        super.setItem(index, item);
    }

    @Override
    public boolean contains(int materialId) {
        return base.contains(materialId);
    }

    public boolean super_contains(int materialId) {
        return super.contains(materialId);
    }

    @Override
    public boolean contains(Material material) {
        return base.contains(material);
    }

    public boolean super_contains(Material material) {
        return super.contains(material);
    }

    @Override
    public boolean contains(ItemStack item) {
        return base.contains(item);
    }

    public boolean super_contains(ItemStack item) {
        return super.contains(item);
    }

    @Override
    public boolean contains(int materialId, int amount) {
        return base.contains(materialId, amount);
    }

    public boolean super_contains(int materialId, int amount) {
        return super.contains(materialId, amount);
    }

    @Override
    public boolean contains(Material material, int amount) {
        return base.contains(material, amount);
    }

    public boolean super_contains(Material material, int amount) {
        return super.contains(material, amount);
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return base.contains(item, amount);
    }

    public boolean super_contains(ItemStack item, int amount) {
        return super.contains(item, amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        return base.containsAtLeast(item, amount);
    }

    public boolean super_containsAtLeast(ItemStack item, int amount) {
        return super.containsAtLeast(item, amount);
    }

    @Override
    @SuppressWarnings("unchecked")
    public HashMap<Integer, ItemStack> all(int materialId) {
        return (HashMap<Integer, ItemStack>) base.all(materialId);
    }

    public HashMap<Integer, ItemStack> super_all(int materialId) {
        return super.all(materialId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public HashMap<Integer, ItemStack> all(Material material) {
        return (HashMap<Integer, ItemStack>) base.all(material);
    }

    public HashMap<Integer, ItemStack> super_all(Material material) {
        return super.all(material);
    }

    @Override
    @SuppressWarnings("unchecked")
    public HashMap<Integer, ItemStack> all(ItemStack item) {
        return (HashMap<Integer, ItemStack>) base.all(item);
    }

    public HashMap<Integer, ItemStack> super_all(ItemStack item) {
        return super.all(item);
    }

    @Override
    public int first(int materialId) {
        return base.first(materialId);
    }

    public int super_first(int materialId) {
        return super.first(materialId);
    }

    @Override
    public int first(Material material) {
        return base.first(material);
    }

    public int super_first(Material material) {
        return super.first(material);
    }

    @Override
    public int first(ItemStack item) {
        return base.first(item);
    }

    public int super_first(ItemStack item) {
        return super.first(item);
    }

    @Override
    public int firstEmpty() {
        return base.firstEmpty();
    }

    public int super_firstEmpty() {
        return super.firstEmpty();
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        return base.addItem(items);
    }

    public HashMap<Integer, ItemStack> super_addItem(ItemStack... items) {
        return super.addItem(items);
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        return base.removeItem(items);
    }

    public HashMap<Integer, ItemStack> super_removeItem(ItemStack... items) {
        return super.removeItem(items);
    }

    @Override
    public void remove(int materialId) {
        base.remove(materialId);
    }

    public void super_remove(int materialId) {
        super.remove(materialId);
    }

    @Override
    public void remove(Material material) {
        base.remove(material);
    }

    public void super_remove(Material material) {
        super.remove(material);
    }

    @Override
    public void remove(ItemStack item) {
        base.remove(item);
    }

    public void super_remove(ItemStack item) {
        super.remove(item);
    }

    @Override
    public void clear(int index) {
        base.clear(index);
    }

    public void super_clear(int index) {
        super.clear(index);
    }

    @Override
    public void clear() {
        base.clear();
    }

    public void super_clear() {
        super.clear();
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return base.iterator();
    }

    public ListIterator<ItemStack> super_iterator() {
        return super.iterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        return base.iterator(index);
    }

    public ListIterator<ItemStack> super_iterator(int index) {
        return super.iterator(index);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return base.getViewers();
    }

    public List<HumanEntity> super_getViewers() {
        return super.getViewers();
    }

    @Override
    public String getTitle() {
        return base.getTitle();
    }

    public String super_getTitle() {
        return super.getTitle();
    }

    @Override
    public InventoryType getType() {
        return base.getType();
    }

    public InventoryType super_getType() {
        return super.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return base.getHolder();
    }

    public InventoryHolder super_getHolder() {
        return super.getHolder();
    }

    @Override
    public int getMaxStackSize() {
        return base.getMaxStackSize();
    }

    public int super_getMaxStackSize() {
        return super.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        base.setMaxStackSize(size);
    }

    public void super_setMaxStackSize(int size) {
        super.setMaxStackSize(size);
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }

    public int super_hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return base.equals(obj);
    }

    public boolean super_equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return base.toString();
    }

    public String super_toString() {
        return super.toString();
    }
}
