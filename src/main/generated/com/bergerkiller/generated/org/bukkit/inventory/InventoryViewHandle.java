package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.inventory.InventoryView</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.inventory.InventoryView")
public abstract class InventoryViewHandle extends Template.Handle {
    /** @see InventoryViewClass */
    public static final InventoryViewClass T = Template.Class.create(InventoryViewClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static InventoryViewHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void close();
    public abstract HumanEntity getPlayer();
    public abstract Inventory getTopInventory();
    public abstract Inventory getBottomInventory();
    /**
     * Stores class members for <b>org.bukkit.inventory.InventoryView</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class InventoryViewClass extends Template.Class<InventoryViewHandle> {
        public final Template.Method<Void> close = new Template.Method<Void>();
        public final Template.Method<HumanEntity> getPlayer = new Template.Method<HumanEntity>();
        public final Template.Method<Inventory> getTopInventory = new Template.Method<Inventory>();
        public final Template.Method<Inventory> getBottomInventory = new Template.Method<Inventory>();

    }

}

