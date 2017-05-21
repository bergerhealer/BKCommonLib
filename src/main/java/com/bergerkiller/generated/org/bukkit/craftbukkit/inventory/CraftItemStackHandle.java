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

    public static Object asNMSCopy(ItemStack original) {
        return T.asNMSCopy.invokeVA(original);
    }

    public static ItemStack asCraftCopy(ItemStack original) {
        return T.asCraftCopy.invokeVA(original);
    }

    public static ItemStack asCraftMirror(Object nmsItemStack) {
        return T.asCraftMirror.invokeVA(nmsItemStack);
    }

    public Object getHandle() {
        return T.handle.get(instance);
    }

    public void setHandle(Object value) {
        T.handle.set(instance, value);
    }

    public static final class CraftItemStackClass extends Template.Class<CraftItemStackHandle> {
        public final Template.Field.Converted<Object> handle = new Template.Field.Converted<Object>();

        public final Template.StaticMethod.Converted<Object> asNMSCopy = new Template.StaticMethod.Converted<Object>();
        public final Template.StaticMethod.Converted<ItemStack> asCraftCopy = new Template.StaticMethod.Converted<ItemStack>();
        public final Template.StaticMethod.Converted<ItemStack> asCraftMirror = new Template.StaticMethod.Converted<ItemStack>();

    }
}
