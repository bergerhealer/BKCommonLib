package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.ClassHook;

/**
 * Redirects all IInventory function calls to the appropriate method in a 
 * org.bukkit.inventory.Inventory object.
 */
@ClassHook.HookPackage("net.minecraft.server")
@ClassHook.HookImport("org.bukkit.craftbukkit.entity.CraftHumanEntity")
@ClassHook.HookImport("net.minecraft.world.item.ItemStack")
@ClassHook.HookImport("net.minecraft.world.entity.player.EntityHuman")
@ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class IInventoryProxyHook extends ClassHook<IInventoryProxyHook> {
    private final Inventory inventory;

    public IInventoryProxyHook(Inventory inventory ){
        this.inventory = inventory;
    }

    @HookMethod("public abstract int getSize:???()")
    public int getSize() {
        return this.inventory.getSize();
    }

    @HookMethod("public abstract int getMaxStackSize()")
    public int getMaxStackSize() {
        return this.inventory.getMaxStackSize();
    }

    @HookMethod("public abstract void setMaxStackSize(int paramInt)")
    public void setMaxStackSize(int size) {
        inventory.setMaxStackSize(size);
    }

    @HookMethod("public abstract void setItem(int paramInt, ItemStack paramItemStack)")
    public void setItem(int index, Object nmsItemStack) {
        this.inventory.setItem(index, WrapperConversion.toItemStack(nmsItemStack));
    }

    @HookMethod("public abstract ItemStack getItem(int paramInt)")
    public Object getItem(int index) {
        return HandleConversion.toItemStackHandle(this.inventory.getItem(index));
    }

    @HookMethod("public abstract void clear:???()")
    public void clear() {
        this.inventory.clear();
    }

    @HookMethodCondition("version >= 1.11")
    @HookMethod(value="public abstract List<ItemStack> getContents()")
    public List<?> getContents() {
        return new ConvertingList<Object>(Arrays.asList(this.inventory.getContents()), DuplexConversion.itemStack.reverse());
    }

    @HookMethodCondition("version <= 1.10.2")
    @HookMethod(value="public abstract ItemStack[] getContents()")
    public Object[] getContents_old() {
        return LogicUtil.toArray(this.getContents(), ItemStackHandle.T.getType());
    }

    @HookMethod("public abstract List<org.bukkit.entity.HumanEntity> getViewers()")
    public List<?> getViewers() {
        return new ConvertingList<Object>(this.inventory.getViewers(), DuplexConversion.entity.reverse());
    }

    @HookMethod("public abstract org.bukkit.inventory.InventoryHolder getOwner()")
    public InventoryHolder getOwner() {
        return this.inventory.getHolder();
    }

    // Since 1.10.2
    @HookMethodCondition("version >= 1.10.2")
    @HookMethod(value="public abstract org.bukkit.Location getLocation()")
    public Location getLocation() {
        if (InventoryHandle.T.getLocation.isAvailable()) {
            return InventoryHandle.T.getLocation.invoke(this.inventory);
        } else {
            return null;
        }
    }

    /* Questionable implementations taken over from EntityMinecartContainer */

    @HookMethod("public ItemStack splitStack:???(int i, int j)")
    public Object splitStack(int i, int j) {
        if ((i < 0) || (i >= inventory.getSize()) || (j <= 0)) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        org.bukkit.inventory.ItemStack item = this.inventory.getItem(i);
        if (ItemUtil.isEmpty(item)) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        ItemStackHandle nmsItem = ItemStackHandle.fromBukkit(item);
        if (nmsItem == null) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        return ItemStackHandle.getRaw(nmsItem.cloneAndSubtract(j));
    }

    @HookMethod("public ItemStack splitWithoutUpdate:???(int i)")
    public Object splitWithoutUpdate(int i) {
        if ((i < 0) || (i >= inventory.getSize())) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        org.bukkit.inventory.ItemStack item = this.inventory.getItem(i);
        if (ItemUtil.isEmpty(item)) {
            return ItemStackHandle.EMPTY_ITEM.getRaw();
        }
        this.setItem(i, ItemStackHandle.EMPTY_ITEM.getRaw());
        return HandleConversion.toItemStackHandle(item);
    }

    /* The below are NOP because they don't make sense for an Inventory 'Base' not representing anything */

    @HookMethod("public abstract void update:???()")
    public void update() {
    }

    @HookMethod("public abstract void onOpen(CraftHumanEntity paramCraftHumanEntity)")
    public void onOpen(Object entity) {
    }

    @HookMethod("public abstract void onClose(CraftHumanEntity paramCraftHumanEntity)")
    public void onClose(Object entity) {
    }

    @HookMethod("public abstract void startOpen:???(EntityHuman paramEntityHuman)")
    public void startOpen(Object entityHuman) {
    }

    @HookMethodCondition("version < 1.18")
    @HookMethod("public abstract void stopOpen:closeContainer(EntityHuman paramEntityHuman)")
    public void closeContainerV1(Object entityHuman) {
    }

    @HookMethodCondition("version >= 1.18 && version < 1.21.9")
    @HookMethod("public abstract void stopOpen(EntityHuman paramEntityHuman)")
    public void closeContainerV2(Object entityHuman) {
    }

    @HookMethodCondition("version >= 1.21.9")
    @HookMethod("public abstract void stopOpen(net.minecraft.world.entity.ContainerUser containerUser)")
    public void closeContainerV3(Object containerUser) {
    }

    @HookMethodCondition("version <= 1.13.2")
    @HookMethod(value="public abstract int getProperty:???(int key)")
    public int getProperty(int key) {
        return 0;
    }

    @HookMethodCondition("version <= 1.13.2")
    @HookMethod(value="public abstract void setProperty:???(int key, int value)")
    public void setProperty(int key, int value) {
    }

    @HookMethodCondition("version <= 1.13.2")
    @HookMethod(value="public abstract int someFunction:???()")
    public int someFunction() {
        return 0;
    }

    @HookMethod("public abstract boolean canOpen:???(EntityHuman paramEntityHuman)")
    public boolean canOpen(Object human) {
        return true;
    }

    @HookMethod("public abstract boolean canStoreItem:???(int paramInt, ItemStack paramItemStack)")
    public boolean canStoreItem(int paramInt, Object paramItemStack) {
        return true;
    }

    @HookMethodCondition("version <= 1.14.4")
    @HookMethod(value="public abstract boolean isNotEmptyOpt:???()")
    public boolean isNotEmpty() {
        return true;
    }
}
