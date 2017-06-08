package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.BrewerInventory;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftInventoryBrewerHandle extends Template.Handle {
    public static final CraftInventoryBrewerClass T = new CraftInventoryBrewerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryBrewerHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryBrewer");

    /* ============================================================================== */

    public static CraftInventoryBrewerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryBrewerHandle handle = new CraftInventoryBrewerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final BrewerInventory createNew(Object nmsTileEntityBrewingStand) {
        return T.constr_nmsTileEntityBrewingStand.newInstance(nmsTileEntityBrewingStand);
    }

    /* ============================================================================== */

    public static final class CraftInventoryBrewerClass extends Template.Class<CraftInventoryBrewerHandle> {
        public final Template.Constructor.Converted<BrewerInventory> constr_nmsTileEntityBrewingStand = new Template.Constructor.Converted<BrewerInventory>();

    }

}

