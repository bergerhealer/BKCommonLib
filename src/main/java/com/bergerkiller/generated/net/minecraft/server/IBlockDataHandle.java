package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IBlockDataHandle extends Template.Handle {
    public static final IBlockDataClass T = new IBlockDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IBlockDataHandle.class, "net.minecraft.server.IBlockData");


    /* ============================================================================== */

    public static IBlockDataHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IBlockDataHandle handle = new IBlockDataHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public BlockHandle getBlock() {
        return T.getBlock.invoke(instance);
    }

    public static final class IBlockDataClass extends Template.Class<IBlockDataHandle> {
        public final Template.Method.Converted<BlockHandle> getBlock = new Template.Method.Converted<BlockHandle>();

    }
}
