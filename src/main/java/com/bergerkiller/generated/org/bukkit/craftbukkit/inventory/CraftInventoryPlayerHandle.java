package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.PlayerInventory;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftInventoryPlayerHandle extends Template.Handle {
    public static final CraftInventoryPlayerClass T = new CraftInventoryPlayerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryPlayerHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryPlayer");

    /* ============================================================================== */

    public static CraftInventoryPlayerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryPlayerHandle handle = new CraftInventoryPlayerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PlayerInventory createNew(Object nmsPlayerInventory) {
        return T.constr_nmsPlayerInventory.newInstance(nmsPlayerInventory);
    }

    /* ============================================================================== */

    public static final class CraftInventoryPlayerClass extends Template.Class<CraftInventoryPlayerHandle> {
        public final Template.Constructor.Converted<PlayerInventory> constr_nmsPlayerInventory = new Template.Constructor.Converted<PlayerInventory>();

    }

}

