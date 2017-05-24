package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class RegistryBlocksHandle extends Template.Handle {
    public static final RegistryBlocksClass T = new RegistryBlocksClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegistryBlocksHandle.class, "net.minecraft.server.RegistryBlocks");


    /* ============================================================================== */

    public static RegistryBlocksHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RegistryBlocksHandle handle = new RegistryBlocksHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class RegistryBlocksClass extends Template.Class<RegistryBlocksHandle> {
    }
}
