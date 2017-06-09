package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.TileEntityHopper</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class TileEntityHopperHandle extends TileEntityHandle {
    /** @See {@link TileEntityHopperClass} */
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
        return T.suckItems.invokeVA(ihopper);
    }

    /**
     * Stores class members for <b>net.minecraft.server.TileEntityHopper</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TileEntityHopperClass extends Template.Class<TileEntityHopperHandle> {
        public final Template.StaticMethod.Converted<Boolean> suckItems = new Template.StaticMethod.Converted<Boolean>();

    }

}

