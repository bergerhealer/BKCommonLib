package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.FurnaceInventory;

public class CraftInventoryFurnaceHandle extends Template.Handle {
    public static final CraftInventoryFurnaceClass T = new CraftInventoryFurnaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryFurnaceHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryFurnace");


    /* ============================================================================== */

    public static CraftInventoryFurnaceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryFurnaceHandle handle = new CraftInventoryFurnaceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final FurnaceInventory createNew(Object nmsTileEntityFurnace) {
        return T.constr_nmsTileEntityFurnace.newInstance(nmsTileEntityFurnace);
    }

    /* ============================================================================== */

    public static final class CraftInventoryFurnaceClass extends Template.Class<CraftInventoryFurnaceHandle> {
        public final Template.Constructor.Converted<FurnaceInventory> constr_nmsTileEntityFurnace = new Template.Constructor.Converted<FurnaceInventory>();

    }
}
