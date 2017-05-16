package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BlocksHandle extends Template.Handle {
    public static final BlocksClass T = new BlocksClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlocksHandle.class, "net.minecraft.server.Blocks");

    public static final Object LADDER = T.LADDER.getSafe();

    /* ============================================================================== */

    public static BlocksHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlocksHandle handle = new BlocksHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class BlocksClass extends Template.Class {
        public final Template.StaticField.Converted<Object> LADDER = new Template.StaticField.Converted<Object>();

    }
}
