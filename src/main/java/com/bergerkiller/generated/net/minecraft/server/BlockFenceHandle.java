package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BlockFence</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class BlockFenceHandle extends Template.Handle {
    /** @See {@link BlockFenceClass} */
    public static final BlockFenceClass T = new BlockFenceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockFenceHandle.class, "net.minecraft.server.BlockFence");

    /* ============================================================================== */

    public static BlockFenceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        BlockFenceHandle handle = new BlockFenceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.BlockFence</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockFenceClass extends Template.Class<BlockFenceHandle> {
    }

}

