package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BlockFenceGateHandle extends Template.Handle {
    public static final BlockFenceGateClass T = new BlockFenceGateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockFenceGateHandle.class, "net.minecraft.server.BlockFenceGate");


    /* ============================================================================== */

    public static BlockFenceGateHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockFenceGateHandle handle = new BlockFenceGateHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class BlockFenceGateClass extends Template.Class<BlockFenceGateHandle> {
    }
}
