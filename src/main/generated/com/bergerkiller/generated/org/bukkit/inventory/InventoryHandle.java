package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

/**
 * Instance wrapper handle for type <b>org.bukkit.inventory.Inventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.inventory.Inventory")
public abstract class InventoryHandle extends Template.Handle {
    /** @See {@link InventoryClass} */
    public static final InventoryClass T = Template.Class.create(InventoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static InventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.inventory.Inventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InventoryClass extends Template.Class<InventoryHandle> {
        @Template.Optional
        public final Template.Method<Location> getLocation = new Template.Method<Location>();
        @Template.Optional
        public final Template.Method<ItemStack[]> getStorageContents = new Template.Method<ItemStack[]>();
        @Template.Optional
        public final Template.Method<Void> setStorageContents = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<String> getName = new Template.Method<String>();
        @Template.Optional
        public final Template.Method<String> getTitle = new Template.Method<String>();
        @Template.Optional
        public final Template.Method<HashMap<Integer, ItemStack>> removeItemAnySlot = new Template.Method<HashMap<Integer, ItemStack>>();

    }

}

