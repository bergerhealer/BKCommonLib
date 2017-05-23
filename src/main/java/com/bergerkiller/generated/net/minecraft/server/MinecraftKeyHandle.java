package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class MinecraftKeyHandle extends Template.Handle {
    public static final MinecraftKeyClass T = new MinecraftKeyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MinecraftKeyHandle.class, "net.minecraft.server.MinecraftKey");


    /* ============================================================================== */

    public static MinecraftKeyHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MinecraftKeyHandle handle = new MinecraftKeyHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class MinecraftKeyClass extends Template.Class<MinecraftKeyHandle> {
    }
}
