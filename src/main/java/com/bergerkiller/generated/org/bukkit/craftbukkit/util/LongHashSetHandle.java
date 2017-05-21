package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class LongHashSetHandle extends Template.Handle {
    public static final LongHashSetClass T = new LongHashSetClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(LongHashSetHandle.class, "org.bukkit.craftbukkit.util.LongHashSet");


    /* ============================================================================== */

    public static LongHashSetHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        LongHashSetHandle handle = new LongHashSetHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean add(int msw, int lsw) {
        return T.add.invoke(instance, msw, lsw);
    }

    public void remove(int msw, int lsw) {
        T.remove.invoke(instance, msw, lsw);
    }

    public static final class LongHashSetClass extends Template.Class<LongHashSetHandle> {
        public final Template.Method<Boolean> add = new Template.Method<Boolean>();
        public final Template.Method<Void> remove = new Template.Method<Void>();

    }
}
