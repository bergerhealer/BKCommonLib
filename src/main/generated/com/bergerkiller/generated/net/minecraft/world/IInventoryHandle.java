package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.IInventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.IInventory")
public abstract class IInventoryHandle extends Template.Handle {
    /** @see IInventoryClass */
    public static final IInventoryClass T = Template.Class.create(IInventoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static IInventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ItemStackHandle getItem(int index);
    public abstract void setItem(int paramInt, ItemStackHandle paramItemStack);
    public abstract void stopOpen(HumanEntity paramEntityHuman);
    public abstract ItemStack splitStack(int i, int j);
    public abstract ItemStack splitWithoutUpdate(int i);
    public abstract int getSize();
    public abstract void update();
    public abstract boolean canOpen(HumanEntity paramEntityHuman);
    public abstract boolean canStoreItem(int index, ItemStack itemstack);
    public abstract List<ItemStackHandle> getContents();
    public abstract void clear();
    /**
     * Stores class members for <b>net.minecraft.world.IInventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IInventoryClass extends Template.Class<IInventoryHandle> {
        public final Template.Method.Converted<ItemStackHandle> getItem = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> stopOpen = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ItemStack> splitStack = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<ItemStack> splitWithoutUpdate = new Template.Method.Converted<ItemStack>();
        public final Template.Method<Integer> getSize = new Template.Method<Integer>();
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
        public final Template.Method<Boolean> isNotEmptyOpt = new Template.Method<Boolean>();

    }

}

