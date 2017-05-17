package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IInventoryHandle extends Template.Handle {
    public static final IInventoryClass T = new IInventoryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IInventoryHandle.class, "net.minecraft.server.IInventory");


    /* ============================================================================== */

    public static IInventoryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IInventoryHandle handle = new IInventoryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void update() {
        T.update.invoke(instance);
    }

    public static final class IInventoryClass extends Template.Class<IInventoryHandle> {
        public final Template.Method<Void> update = new Template.Method<Void>();

    }
}
