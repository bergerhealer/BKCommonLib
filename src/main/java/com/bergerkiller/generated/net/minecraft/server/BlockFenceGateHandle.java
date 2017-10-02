package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BlockFenceGate</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BlockFenceGateHandle extends Template.Handle {
    /** @See {@link BlockFenceGateClass} */
    public static final BlockFenceGateClass T = new BlockFenceGateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockFenceGateHandle.class, "net.minecraft.server.BlockFenceGate");

    /* ============================================================================== */

    public static BlockFenceGateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.BlockFenceGate</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockFenceGateClass extends Template.Class<BlockFenceGateHandle> {
    }

}

