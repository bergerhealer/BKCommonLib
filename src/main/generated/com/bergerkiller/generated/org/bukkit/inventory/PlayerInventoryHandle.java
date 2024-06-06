package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>org.bukkit.inventory.PlayerInventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.inventory.PlayerInventory")
public abstract class PlayerInventoryHandle extends InventoryHandle {
    /** @see PlayerInventoryClass */
    public static final PlayerInventoryClass T = Template.Class.create(PlayerInventoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerInventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ItemStack getItemInMainHand();
    public abstract ItemStack getItemInOffHand();
    public abstract boolean isEquipmentSlotSupported(EquipmentSlot slot);
    public abstract void setItem(EquipmentSlot slot, ItemStack item);
    public abstract ItemStack getItem(EquipmentSlot slot);
    /**
     * Stores class members for <b>org.bukkit.inventory.PlayerInventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerInventoryClass extends Template.Class<PlayerInventoryHandle> {
        @Template.Optional
        public final Template.Method<Void> setItemInMainHand = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Void> setItemInOffHand = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<ItemStack> getItemInHand = new Template.Method<ItemStack>();
        @Template.Optional
        public final Template.Method<Void> setItemInHand = new Template.Method<Void>();
        public final Template.Method<ItemStack> getItemInMainHand = new Template.Method<ItemStack>();
        public final Template.Method<ItemStack> getItemInOffHand = new Template.Method<ItemStack>();
        public final Template.Method<Boolean> isEquipmentSlotSupported = new Template.Method<Boolean>();
        public final Template.Method<Void> setItem = new Template.Method<Void>();
        public final Template.Method<ItemStack> getItem = new Template.Method<ItemStack>();

    }

}

