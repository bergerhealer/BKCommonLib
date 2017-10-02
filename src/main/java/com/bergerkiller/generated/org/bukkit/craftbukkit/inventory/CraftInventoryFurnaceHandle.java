package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.FurnaceInventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventoryFurnace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftInventoryFurnaceHandle extends Template.Handle {
    /** @See {@link CraftInventoryFurnaceClass} */
    public static final CraftInventoryFurnaceClass T = new CraftInventoryFurnaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryFurnaceHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryFurnace");

    /* ============================================================================== */

    public static CraftInventoryFurnaceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final FurnaceInventory createNew(Object nmsTileEntityFurnace) {
        return T.constr_nmsTileEntityFurnace.newInstance(nmsTileEntityFurnace);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventoryFurnace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryFurnaceClass extends Template.Class<CraftInventoryFurnaceHandle> {
        public final Template.Constructor.Converted<FurnaceInventory> constr_nmsTileEntityFurnace = new Template.Constructor.Converted<FurnaceInventory>();

    }

}

