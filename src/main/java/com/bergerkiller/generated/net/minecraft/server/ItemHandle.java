package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class ItemHandle extends Template.Handle {
    public static final ItemClass T = new ItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ItemHandle.class, "net.minecraft.server.Item");

    /* ============================================================================== */

    public static ItemHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ItemHandle handle = new ItemHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getMaxStackSize() {
        return T.getMaxStackSize.invoke(instance);
    }

    public boolean usesDurability() {
        return T.usesDurability.invoke(instance);
    }

    public static final class ItemClass extends Template.Class<ItemHandle> {
        public final Template.Method<Integer> getMaxStackSize = new Template.Method<Integer>();
        public final Template.Method<Boolean> usesDurability = new Template.Method<Boolean>();

    }

}

