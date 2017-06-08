package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class ChunkRegionLoaderHandle extends Template.Handle {
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

    public static final class ChunkRegionLoaderClass extends Template.Class<ChunkRegionLoaderHandle> {
        public final Template.Method<Boolean> chunkExists = new Template.Method<Boolean>();

    }

}

