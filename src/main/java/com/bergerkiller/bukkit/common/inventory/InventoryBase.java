package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.hooks.IInventoryProxyHook;
import com.bergerkiller.generated.net.minecraft.server.IInventoryHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * A basic implementation of Inventory that excludes the getting and setting of
 * item content information. Uses a backing CraftInventory to perform all
 * utility methods provided by Bukkit. This ensures that the functionality is
 * up-to-date with the latest bugfixes and additions.
 */
public abstract class InventoryBase implements Inventory {
    private final Object nmsProxy;
    private final Inventory cbProxy;
    private int maxstacksize;

    static {
        // Verify all Inventory interface methods are implemented by this InventoryBase
        for (Method m : Inventory.class.getDeclaredMethods()) {
            try {
                InventoryBase.class.getDeclaredMethod(m.getName(), m.getParameterTypes());
            } catch (Throwable t) {
                throw new RuntimeException("Method " + m.toString() + " is not implemented in InventoryBase");
            }
        }
    }

    /*
     * - nmsProxy implements a basic net.minecraft.server.IInventory, but redirects
     * all calls to the abstract functions found in here.
     * - cbProxy has the Inventory implementation and will call nmsProxy to do item
     * transactions.
     * - this InventoryBase proxies all non-abstract functions to call the cbProxy.
     * This handles things like merging, splitting, etc.
     */
    public InventoryBase() {
        this.nmsProxy = new IInventoryProxyHook(this).createInstance(IInventoryHandle.T.getType());
        this.cbProxy = CraftInventoryHandle.createNew(this.nmsProxy);
        this.maxstacksize = 64;
    }

    /**
     * Gets the raw underlying net.minecraft.server.IInventory that is used to proxy item transactions
     * 
     * @return raw IInventory handle
     */
    public final Object getRawHandle() {
        return this.nmsProxy;
    }

    @Override
    public abstract int getSize();

    @Override
    public abstract ItemStack getItem(int index);

    @Override
    public abstract void setItem(int index, ItemStack item);

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[getSize()];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = this.getItem(i);
        }
        return contents;
    }

    @Override
    public void setContents(ItemStack[] items) {
        // On MC 1.8.8 this function bugs out, because they don't use setItem
        //this.cbProxy.setContents(items);

        // Instead, rely on copying known working code
        if (getSize() < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getSize() + " or less");
        }

        for (int i = 0; i < getSize(); i++) {
            if (i >= items.length) {
                setItem(i, null);
            } else {
                setItem(i, items[i]);
            }
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
        return Collections.emptyList();
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
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) throws IllegalArgumentException {
        return cbProxy.addItem(items);
    }

    @Deprecated
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return cbProxy.all(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return cbProxy.all(material);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return cbProxy.all(item);
    }

    @Override
    public void clear() {
        cbProxy.clear();
    }

    @Override
    public void clear(int index) {
        cbProxy.clear(index);
    }

    @Deprecated
    public boolean contains(int materialId) {
        return cbProxy.contains(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        return cbProxy.contains(material);
    }

    @Override
    public boolean contains(ItemStack item) {
        return cbProxy.contains(item);
    }

    @Deprecated
    public boolean contains(int materialId, int amount) {
        return cbProxy.contains(CommonLegacyMaterials.getMaterialFromId(materialId), amount);
    }

    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException {
        return cbProxy.contains(material, amount);
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return cbProxy.contains(item, amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        return cbProxy.containsAtLeast(item, amount);
    }

    @Deprecated
    public int first(int materialId) {
        return cbProxy.first(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public int first(Material material) throws IllegalArgumentException {
        return cbProxy.first(material);
    }

    @Override
    public int first(ItemStack item) {
        return cbProxy.first(item);
    }

    @Override
    public int firstEmpty() {
        return cbProxy.firstEmpty();
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return cbProxy.iterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        return cbProxy.iterator(index);
    }

    @Deprecated
    public void remove(int materialId) {
        remove(CommonLegacyMaterials.getMaterialFromId(materialId));
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException {
        cbProxy.remove(material);
    }

    @Override
    public void remove(ItemStack item) {
        cbProxy.remove(item);
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) throws IllegalArgumentException {
        return cbProxy.removeItem(items);
    }

    @Override
    public int hashCode() {
        return cbProxy.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return cbProxy.equals(obj);
    }

    @Override
    public String toString() {
        return cbProxy.toString();
    }

    //@Override // Only on >= v1.10.2
    public Location getLocation() {
        return null;
    }

    //@Override // Only on >= v1.10.2
    public ItemStack[] getStorageContents() {
        if (InventoryHandle.T.getStorageContents.isAvailable()) {
            return InventoryHandle.T.getStorageContents.invoke(this.cbProxy);
        } else {
            return new ItemStack[0];
        }
    }

    //@Override // Only on >= v1.10.2
    public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
        if (InventoryHandle.T.setStorageContents.isAvailable()) {
            InventoryHandle.T.setStorageContents.invoke(this.cbProxy, items);
        } else {
            // Do nothing.
        }
    }

}
