package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.BaseBlockPositionHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class BlockPositionHandle extends BaseBlockPositionHandle {
    public static final BlockPositionClass T = new BlockPositionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockPositionHandle.class, "net.minecraft.server.BlockPosition");


    /* ============================================================================== */

    public static BlockPositionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockPositionHandle handle = new BlockPositionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final BlockPositionHandle createNew(int x, int y, int z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public static final class BlockPositionClass extends Template.Class<BlockPositionHandle> {
        public final Template.Constructor.Converted<BlockPositionHandle> constr_x_y_z = new Template.Constructor.Converted<BlockPositionHandle>();

    }
}
