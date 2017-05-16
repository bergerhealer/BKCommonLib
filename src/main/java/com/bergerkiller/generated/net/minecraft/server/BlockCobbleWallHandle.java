package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BlockCobbleWallHandle extends Template.Handle {
    public static final BlockCobbleWallClass T = new BlockCobbleWallClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockCobbleWallHandle.class, "net.minecraft.server.BlockCobbleWall");


    /* ============================================================================== */

    public static BlockCobbleWallHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockCobbleWallHandle handle = new BlockCobbleWallHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class BlockCobbleWallClass extends Template.Class {
    }
}
