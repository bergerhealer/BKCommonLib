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
        if (handleInstance == null) return null;
        ChunkRegionLoaderHandle handle = new ChunkRegionLoaderHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean chunkExists(int cx, int cz) {
        return T.chunkExists.invoke(instance, cx, cz);
    }

    /**
     * Stores class members for <b>net.minecraft.server.ChunkRegionLoader</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkRegionLoaderClass extends Template.Class<ChunkRegionLoaderHandle> {
        public final Template.Method<Boolean> chunkExists = new Template.Method<Boolean>();

    }

}

