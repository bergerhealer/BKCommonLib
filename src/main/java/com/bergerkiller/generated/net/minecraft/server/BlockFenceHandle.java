package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BlockFenceHandle extends Template.Handle {
    public static final BlockFenceClass T = new BlockFenceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockFenceHandle.class, "net.minecraft.server.BlockFence");


    /* ============================================================================== */

    public static BlockFenceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockFenceHandle handle = new BlockFenceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class BlockFenceClass extends Template.Class {
    }
}
