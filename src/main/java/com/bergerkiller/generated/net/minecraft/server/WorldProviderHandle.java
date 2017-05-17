package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class WorldProviderHandle extends Template.Handle {
    public static final WorldProviderClass T = new WorldProviderClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldProviderHandle.class, "net.minecraft.server.WorldProvider");


    /* ============================================================================== */

    public static WorldProviderHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldProviderHandle handle = new WorldProviderHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean isDarkWorld() {
        return T.isDarkWorld.invoke(instance);
    }

    public static final class WorldProviderClass extends Template.Class<WorldProviderHandle> {
        public final Template.Method<Boolean> isDarkWorld = new Template.Method<Boolean>();

    }
}
