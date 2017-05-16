package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.ItemStack;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftItemStackHandle extends Template.Handle {
    public static final CraftItemStackClass T = new CraftItemStackClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftItemStackHandle.class, "org.bukkit.craftbukkit.inventory.CraftItemStack");


    /* ============================================================================== */

    public static CraftItemStackHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftItemStackHandle handle = new CraftItemStackHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static ItemStack asCraftCopy(ItemStack original) {
        return T.asCraftCopy.invoke(original);
    }

    public static final class CraftItemStackClass extends Template.Class {
        public final Template.StaticMethod.Converted<ItemStack> asCraftCopy = new Template.StaticMethod.Converted<ItemStack>();

    }
}
