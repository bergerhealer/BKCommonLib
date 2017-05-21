package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.inventory.MerchantInventory;

public class CraftInventoryMerchantHandle extends Template.Handle {
    public static final CraftInventoryMerchantClass T = new CraftInventoryMerchantClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryMerchantHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventoryMerchant");


    /* ============================================================================== */

    public static CraftInventoryMerchantHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryMerchantHandle handle = new CraftInventoryMerchantHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final MerchantInventory createNew(Object nmsInventoryMerchant) {
        return T.constr_nmsInventoryMerchant.newInstance(nmsInventoryMerchant);
    }

    /* ============================================================================== */

    public static final class CraftInventoryMerchantClass extends Template.Class<CraftInventoryMerchantHandle> {
        public final Template.Constructor.Converted<MerchantInventory> constr_nmsInventoryMerchant = new Template.Constructor.Converted<MerchantInventory>();

    }
}
