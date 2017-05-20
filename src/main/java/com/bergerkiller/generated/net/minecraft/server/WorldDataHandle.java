package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.WorldTypeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class WorldDataHandle extends Template.Handle {
    public static final WorldDataClass T = new WorldDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldDataHandle.class, "net.minecraft.server.WorldData");


    /* ============================================================================== */

    public static WorldDataHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldDataHandle handle = new WorldDataHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public WorldTypeHandle getType() {
        return T.getType.invokeVA(instance);
    }

    public void setClearTimer(int ticks) {
        T.setClearTimer.invoke(instance, ticks);
    }

    public static final class WorldDataClass extends Template.Class<WorldDataHandle> {
        public final Template.Method.Converted<WorldTypeHandle> getType = new Template.Method.Converted<WorldTypeHandle>();
        public final Template.Method<Void> setClearTimer = new Template.Method<Void>();

    }
}
