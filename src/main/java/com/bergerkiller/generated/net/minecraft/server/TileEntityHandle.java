package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class TileEntityHandle extends Template.Handle {
    public static final TileEntityClass T = new TileEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(TileEntityHandle.class, "net.minecraft.server.TileEntity");


    /* ============================================================================== */

    public static TileEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        TileEntityHandle handle = new TileEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class TileEntityClass extends Template.Class<TileEntityHandle> {
    }
}
