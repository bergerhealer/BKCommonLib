package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.BrewerInventory;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.inventory.CraftInventoryBrewer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class CraftInventoryBrewerHandle extends Template.Handle {
    /** @See {@link CraftInventoryBrewerClass} */
    public static final CraftInventoryBrewerClass T = new CraftInventoryBrewerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryBrewerHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryBrewer");

    /* ============================================================================== */

    public static CraftInventoryBrewerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final BrewerInventory createNew(Object nmsTileEntityBrewingStand) {
        return T.constr_nmsTileEntityBrewingStand.newInstance(nmsTileEntityBrewingStand);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.craftbukkit.inventory.CraftInventoryBrewer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftInventoryBrewerClass extends Template.Class<CraftInventoryBrewerHandle> {
        public final Template.Constructor.Converted<BrewerInventory> constr_nmsTileEntityBrewingStand = new Template.Constructor.Converted<BrewerInventory>();

    }

}

