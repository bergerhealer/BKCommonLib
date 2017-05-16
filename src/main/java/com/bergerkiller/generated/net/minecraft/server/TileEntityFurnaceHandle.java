package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class TileEntityFurnaceHandle extends Template.Handle {
    public static final TileEntityFurnaceClass T = new TileEntityFurnaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(TileEntityFurnaceHandle.class, "net.minecraft.server.TileEntityFurnace");


    /* ============================================================================== */

    public static TileEntityFurnaceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        TileEntityFurnaceHandle handle = new TileEntityFurnaceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static int fuelTime(ItemStackHandle itemstack) {
        return T.fuelTime.invoke(itemstack);
    }

    public static final class TileEntityFurnaceClass extends Template.Class {
        public final Template.StaticMethod.Converted<Integer> fuelTime = new Template.StaticMethod.Converted<Integer>();

    }
}
