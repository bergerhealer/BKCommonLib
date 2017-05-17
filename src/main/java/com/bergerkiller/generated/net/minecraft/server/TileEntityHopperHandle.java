package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class TileEntityHopperHandle extends TileEntityHandle {
    public static final TileEntityHopperClass T = new TileEntityHopperClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(TileEntityHopperHandle.class, "net.minecraft.server.TileEntityHopper");


    /* ============================================================================== */

    public static TileEntityHopperHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        TileEntityHopperHandle handle = new TileEntityHopperHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static boolean suckItems(Object ihopper) {
        return T.suckItems.invoke(ihopper);
    }

    public static final class TileEntityHopperClass extends Template.Class<TileEntityHopperHandle> {
        public final Template.StaticMethod.Converted<Boolean> suckItems = new Template.StaticMethod.Converted<Boolean>();

    }
}
