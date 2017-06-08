package com.bergerkiller.generated.net.minecraft.server;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.List;

public class IInventoryHandle extends Template.Handle {
    public static final IInventoryClass T = new IInventoryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IInventoryHandle.class, "net.minecraft.server.IInventory");

    /* ============================================================================== */

    public static IInventoryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IInventoryHandle handle = new IInventoryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getSize() {
        return T.getSize.invoke(instance);
    }

    public ItemStackHandle getItem(int index) {
        return T.getItem.invoke(instance, index);
    }

    public void setItem(int paramInt, ItemStackHandle paramItemStack) {
        T.setItem.invoke(instance, paramInt, paramItemStack);
    }

    public List<ItemStackHandle> getContents() {
        return T.getContents.invoke(instance);
    }

    public void update() {
        T.update.invoke(instance);
    }

    public boolean canOpen(HumanEntity paramEntityHuman) {
        return T.canOpen.invoke(instance, paramEntityHuman);
    }

    public boolean canStoreItem(int index, ItemStack itemstack) {
        return T.canStoreItem.invoke(instance, index, itemstack);
    }

    public int someFunction() {
        return T.someFunction.invoke(instance);
    }

    public boolean someFunction2() {
        return T.someFunction2.invoke(instance);
    }

    public static final class IInventoryClass extends Template.Class<IInventoryHandle> {
        public final Template.Method<Integer> getSize = new Template.Method<Integer>();
        public final Template.Method.Converted<ItemStackHandle> getItem = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<List<ItemStackHandle>> getContents = new Template.Method.Converted<List<ItemStackHandle>>();
        public final Template.Method<Void> update = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> canOpen = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> canStoreItem = new Template.Method.Converted<Boolean>();
        public final Template.Method<Integer> someFunction = new Template.Method<Integer>();
        public final Template.Method<Boolean> someFunction2 = new Template.Method<Boolean>();

    }

}

