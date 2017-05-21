package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class CraftWorldHandle extends Template.Handle {
    public static final CraftWorldClass T = new CraftWorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftWorldHandle.class, "org.bukkit.craftbukkit.CraftWorld");


    /* ============================================================================== */

    public static CraftWorldHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftWorldHandle handle = new CraftWorldHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object getHandle() {
        return T.getHandle.invoke(instance);
    }

    public static final class CraftWorldClass extends Template.Class<CraftWorldHandle> {
        public final Template.Method.Converted<Object> getHandle = new Template.Method.Converted<Object>();

    }
}
