package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import org.bukkit.inventory.Inventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventory</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftInventory")
public abstract class CraftInventoryHandle extends InventoryHandle {
    /** @see CraftInventoryClass */
    public static final CraftInventoryClass T = Template.Class.create(CraftInventoryClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftInventoryHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final Inventory createNew(Object nmsContainer) {
        return T.constr_nmsContainer.newInstance(nmsContainer);
    }

    /* ============================================================================== */

    public abstract ContainerHandle getHandle();
    public abstract ContainerHandle getHandleField();
    public abstract void setHandleField(ContainerHandle value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventory</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryClass extends Template.Class<CraftInventoryHandle> {
        public final Template.Constructor.Converted<Inventory> constr_nmsContainer = new Template.Constructor.Converted<Inventory>();

        public final Template.Field.Converted<ContainerHandle> handleField = new Template.Field.Converted<ContainerHandle>();

        public final Template.Method.Converted<ContainerHandle> getHandle = new Template.Method.Converted<ContainerHandle>();

    }

}

