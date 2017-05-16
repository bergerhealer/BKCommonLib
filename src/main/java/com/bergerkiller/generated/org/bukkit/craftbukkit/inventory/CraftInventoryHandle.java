package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.generated.net.minecraft.server.IInventoryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftInventoryHandle extends Template.Handle {
    public static final CraftInventoryClass T = new CraftInventoryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftInventoryHandle.class, "org.bukkit.craftbukkit.inventory.CraftInventory");


    /* ============================================================================== */

    public static CraftInventoryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftInventoryHandle handle = new CraftInventoryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public IInventoryHandle getHandle() {
        return T.getHandle.invoke(instance);
    }

    public IInventoryHandle getHandleField() {
        return T.handleField.get(instance);
    }

    public void setHandleField(IInventoryHandle value) {
        T.handleField.set(instance, value);
    }

    public static final class CraftInventoryClass extends Template.Class {
        public final Template.Field.Converted<IInventoryHandle> handleField = new Template.Field.Converted<IInventoryHandle>();

        public final Template.Method.Converted<IInventoryHandle> getHandle = new Template.Method.Converted<IInventoryHandle>();

    }
}
