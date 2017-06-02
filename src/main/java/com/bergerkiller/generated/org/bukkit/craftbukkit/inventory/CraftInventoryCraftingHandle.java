package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.CraftingInventory;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftInventoryCraftingHandle extends Template.Handle {
    public static final CraftInventoryCraftingClass T = new CraftInventoryCraftingClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryCraftingHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryCrafting");

    /* ============================================================================== */

    public static CraftInventoryCraftingHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryCraftingHandle handle = new CraftInventoryCraftingHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final CraftingInventory createNew(Object nmsInventoryCrafting, Object nmsResultIInventory) {
        return T.constr_nmsInventoryCrafting_nmsResultIInventory.newInstance(nmsInventoryCrafting, nmsResultIInventory);
    }

    /* ============================================================================== */

    public static final class CraftInventoryCraftingClass extends Template.Class<CraftInventoryCraftingHandle> {
        public final Template.Constructor.Converted<CraftingInventory> constr_nmsInventoryCrafting_nmsResultIInventory = new Template.Constructor.Converted<CraftingInventory>();

    }

}

