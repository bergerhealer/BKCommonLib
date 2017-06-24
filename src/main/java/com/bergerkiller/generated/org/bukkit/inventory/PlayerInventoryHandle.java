package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>org.bukkit.inventory.PlayerInventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PlayerInventoryHandle extends InventoryHandle {
    /** @See {@link PlayerInventoryClass} */
    public static final PlayerInventoryClass T = new PlayerInventoryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerInventoryHandle.class, "org.bukkit.inventory.PlayerInventory");

    /* ============================================================================== */

    public static PlayerInventoryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PlayerInventoryHandle handle = new PlayerInventoryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.inventory.PlayerInventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerInventoryClass extends Template.Class<PlayerInventoryHandle> {
        @Template.Optional
        public final Template.Method<ItemStack> getItemInMainHand = new Template.Method<ItemStack>();
        @Template.Optional
        public final Template.Method<ItemStack> getItemInOffHand = new Template.Method<ItemStack>();
        @Template.Optional
        public final Template.Method<Void> setItemInMainHand = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Void> setItemInOffHand = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<ItemStack> getItemInHand = new Template.Method<ItemStack>();
        @Template.Optional
        public final Template.Method<Void> setItemInHand = new Template.Method<Void>();

    }

}

