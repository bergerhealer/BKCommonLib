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
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public int getSize() {
        return T.getSize.invoke(getRaw());
    }

    public ItemStackHandle getItem(int index) {
        return T.getItem.invoke(getRaw(), index);
    }

    public void setItem(int paramInt, ItemStackHandle paramItemStack) {
        T.setItem.invoke(getRaw(), paramInt, paramItemStack);
    }

    public void update() {
        T.update.invoke(getRaw());
    }

    public boolean canOpen(HumanEntity paramEntityHuman) {
        return T.canOpen.invoke(getRaw(), paramEntityHuman);
    }

    public boolean canStoreItem(int index, ItemStack itemstack) {
        return T.canStoreItem.invoke(getRaw(), index, itemstack);
    }

    public int getProperty(int key) {
        return T.getProperty.invoke(getRaw(), key);
    }

    public void setProperty(int key, int value) {
        T.setProperty.invoke(getRaw(), key, value);
    }

    public List<ItemStackHandle> getContents() {
        return T.getContents.invoke(getRaw());
    }

    public int someFunction() {
        return T.someFunction.invoke(getRaw());
    }

    public void clear() {
        T.clear.invoke(getRaw());
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

