package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.BeaconInventory;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftInventoryBeaconHandle extends Template.Handle {
    public static final CraftInventoryBeaconClass T = new CraftInventoryBeaconClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryBeaconHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryBeacon");

    /* ============================================================================== */

    public static CraftInventoryBeaconHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryBeaconHandle handle = new CraftInventoryBeaconHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final BeaconInventory createNew(Object nmsTileEntityBeacon) {
        return T.constr_nmsTileEntityBeacon.newInstance(nmsTileEntityBeacon);
    }

    /* ============================================================================== */

    public static final class CraftInventoryBeaconClass extends Template.Class<CraftInventoryBeaconHandle> {
        public final Template.Constructor.Converted<BeaconInventory> constr_nmsTileEntityBeacon = new Template.Constructor.Converted<BeaconInventory>();

    }

}

