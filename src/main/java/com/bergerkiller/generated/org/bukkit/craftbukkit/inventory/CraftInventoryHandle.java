package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.IInventoryHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import org.bukkit.inventory.Inventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftInventory")
public abstract class CraftInventoryHandle extends InventoryHandle {
    /** @See {@link CraftInventoryClass} */
    public static final CraftInventoryClass T = Template.Class.create(CraftInventoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftInventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Inventory createNew(Object nmsIInventory) {
        return T.constr_nmsIInventory.newInstance(nmsIInventory);
    }

    /* ============================================================================== */

    public abstract IInventoryHandle getHandle();
    public abstract IInventoryHandle getHandleField();
    public abstract void setHandleField(IInventoryHandle value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryClass extends Template.Class<CraftInventoryHandle> {
        public final Template.Constructor.Converted<Inventory> constr_nmsIInventory = new Template.Constructor.Converted<Inventory>();

        public final Template.Field.Converted<IInventoryHandle> handleField = new Template.Field.Converted<IInventoryHandle>();

        public final Template.Method.Converted<IInventoryHandle> getHandle = new Template.Method.Converted<IInventoryHandle>();

    }

}

