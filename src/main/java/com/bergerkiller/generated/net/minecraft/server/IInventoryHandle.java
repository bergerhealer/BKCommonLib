package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IInventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class IInventoryHandle extends Template.Handle {
    /** @See {@link IInventoryClass} */
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

    public void update() {
        T.update.invoke(instance);
    }

    public boolean canOpen(HumanEntity paramEntityHuman) {
        return T.canOpen.invoke(instance, paramEntityHuman);
    }

    public boolean canStoreItem(int index, ItemStack itemstack) {
        return T.canStoreItem.invoke(instance, index, itemstack);
    }

    public int getProperty(int key) {
        return T.getProperty.invoke(instance, key);
    }

    public void setProperty(int key, int value) {
        T.setProperty.invoke(instance, key, value);
    }

    public List<ItemStackHandle> getContents() {
        return T.getContents.invoke(instance);
    }

    public int someFunction() {
        return T.someFunction.invoke(instance);
    }

    public void clear() {
        T.clear.invoke(instance);
    }

    /**
     * Stores class members for <b>net.minecraft.server.IInventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IInventoryClass extends Template.Class<IInventoryHandle> {
        public final Template.Method<Integer> getSize = new Template.Method<Integer>();
        public final Template.Method.Converted<ItemStackHandle> getItem = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted<Void>();
        public final Template.Method<Void> update = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> canOpen = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> canStoreItem = new Template.Method.Converted<Boolean>();
        public final Template.Method<Integer> getProperty = new Template.Method<Integer>();
        public final Template.Method<Void> setProperty = new Template.Method<Void>();
        public final Template.Method.Converted<List<ItemStackHandle>> getContents = new Template.Method.Converted<List<ItemStackHandle>>();
        public final Template.Method<Integer> someFunction = new Template.Method<Integer>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Boolean> someFunction2 = new Template.Method<Boolean>();

    }

}

