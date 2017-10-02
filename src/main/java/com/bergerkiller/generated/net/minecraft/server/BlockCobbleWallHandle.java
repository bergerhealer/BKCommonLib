package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.BlockCobbleWall</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class BlockCobbleWallHandle extends Template.Handle {
    /** @See {@link BlockCobbleWallClass} */
    public static final BlockCobbleWallClass T = new BlockCobbleWallClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(BlockCobbleWallHandle.class, "net.minecraft.server.BlockCobbleWall");

    /* ============================================================================== */

    public static BlockCobbleWallHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.BlockCobbleWall</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockCobbleWallClass extends Template.Class<BlockCobbleWallHandle> {
    }

}

