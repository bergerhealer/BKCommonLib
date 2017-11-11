package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Item</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ItemHandle extends Template.Handle {
    /** @See {@link ItemClass} */
    public static final ItemClass T = new ItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ItemHandle.class, "net.minecraft.server.Item");

    @SuppressWarnings("rawtypes")
    public static final Iterable REGISTRY = T.REGISTRY.getSafe();
    /* ============================================================================== */

    public static ItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getMaxStackSize();
    public abstract int getMaxDurability();
    public abstract boolean usesDurability();
    public abstract String getInternalName(ItemStack itemstack);
    public abstract List<ItemStack> getItemVariants(CreativeModeTabHandle creativemodetab);
    /**
     * Stores class members for <b>net.minecraft.server.Item</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ItemClass extends Template.Class<ItemHandle> {
        @SuppressWarnings("rawtypes")
        public final Template.StaticField.Converted<Iterable> REGISTRY = new Template.StaticField.Converted<Iterable>();

        public final Template.Method<Integer> getMaxStackSize = new Template.Method<Integer>();
        public final Template.Method<Integer> getMaxDurability = new Template.Method<Integer>();
        public final Template.Method<Boolean> usesDurability = new Template.Method<Boolean>();
        public final Template.Method.Converted<String> getInternalName = new Template.Method.Converted<String>();
        public final Template.Method.Converted<List<ItemStack>> getItemVariants = new Template.Method.Converted<List<ItemStack>>();

    }

}

