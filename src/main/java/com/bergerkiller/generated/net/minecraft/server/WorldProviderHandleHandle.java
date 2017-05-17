package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class WorldProviderHandleHandle extends Template.Handle {
    public static final WorldProviderHandleClass T = new WorldProviderHandleClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldProviderHandleHandle.class, "com.bergerkiller.generated.net.minecraft.server.WorldProviderHandle");


    /* ============================================================================== */

    public static WorldProviderHandleHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldProviderHandleHandle handle = new WorldProviderHandleHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean isDarkWorld() {
        return T.isDarkWorld.invoke(instance);
    }

    public static final class WorldProviderHandleClass extends Template.Class<WorldProviderHandleHandle> {
        public final Template.Method<Boolean> isDarkWorld = new Template.Method<Boolean>();

    }
}
