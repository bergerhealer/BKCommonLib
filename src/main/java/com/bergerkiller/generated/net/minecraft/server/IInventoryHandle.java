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
public abstract class IInventoryHandle extends Template.Handle {
    /** @See {@link IInventoryClass} */
    public static final IInventoryClass T = new IInventoryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IInventoryHandle.class, "net.minecraft.server.IInventory", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static IInventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getSize();
    public abstract ItemStackHandle getItem(int index);
    public abstract void setItem(int paramInt, ItemStackHandle paramItemStack);
    public abstract void update();
    public abstract boolean canOpen(HumanEntity paramEntityHuman);
    public abstract boolean canStoreItem(int index, ItemStack itemstack);
    public abstract List<ItemStackHandle> getContents();
    public abstract void clear();
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
        @Template.Optional
        public final Template.Method<Integer> getProperty = new Template.Method<Integer>();
        @Template.Optional
        public final Template.Method<Void> setProperty = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Integer> someFunction = new Template.Method<Integer>();
        public final Template.Method.Converted<List<ItemStackHandle>> getContents = new Template.Method.Converted<List<ItemStackHandle>>();
        public final Template.Method<Void> clear = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Boolean> someFunction2 = new Template.Method<Boolean>();

    }

}

