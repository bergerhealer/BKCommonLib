package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BlockPosition</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class BlockPositionHandle extends BaseBlockPositionHandle {
    /** @See {@link BlockPositionClass} */
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

    /**
     * Stores class members for <b>net.minecraft.server.BlockPosition</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockPositionClass extends Template.Class<BlockPositionHandle> {
        public final Template.Constructor.Converted<BlockPositionHandle> constr_x_y_z = new Template.Constructor.Converted<BlockPositionHandle>();

    }

}

