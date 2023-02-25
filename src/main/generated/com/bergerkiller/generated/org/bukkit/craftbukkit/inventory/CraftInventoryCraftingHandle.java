package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.CraftingInventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventoryCrafting</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("org.bukkit.craftbukkit.inventory.CraftInventoryCrafting")
public abstract class CraftInventoryCraftingHandle extends Template.Handle {
    /** @See {@link CraftInventoryCraftingClass} */
    public static final CraftInventoryCraftingClass T = Template.Class.create(CraftInventoryCraftingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static CraftInventoryCraftingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final CraftingInventory createNew(Object nmsInventoryCrafting, Object nmsResultIInventory) {
        return T.constr_nmsInventoryCrafting_nmsResultIInventory.newInstance(nmsInventoryCrafting, nmsResultIInventory);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventoryCrafting</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryCraftingClass extends Template.Class<CraftInventoryCraftingHandle> {
        public final Template.Constructor.Converted<CraftingInventory> constr_nmsInventoryCrafting_nmsResultIInventory = new Template.Constructor.Converted<CraftingInventory>();

    }

}

