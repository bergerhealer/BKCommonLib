package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChunkRegionLoader</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ChunkRegionLoaderHandle extends Template.Handle {
    /** @See {@link ChunkRegionLoaderClass} */
    public static final ChunkRegionLoaderClass T = new ChunkRegionLoaderClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkRegionLoaderHandle.class, "net.minecraft.server.ChunkRegionLoader");

    /* ============================================================================== */

    public static ChunkRegionLoaderHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public boolean chunkExists(org.bukkit.World world, int cx, int cz) {
        if (T.opt_chunkExists_old.isAvailable()) {
            return T.opt_chunkExists_old.invoke(getRaw(), world, cx, cz);
        } else {
            return T.opt_chunkExists.invoke(getRaw(), cx, cz);
        }
    }
    /**
     * Stores class members for <b>net.minecraft.server.ChunkRegionLoader</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkRegionLoaderClass extends Template.Class<ChunkRegionLoaderHandle> {
        @Template.Optional
        public final Template.Method<Boolean> opt_chunkExists = new Template.Method<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> opt_chunkExists_old = new Template.Method.Converted<Boolean>();

    }

}

