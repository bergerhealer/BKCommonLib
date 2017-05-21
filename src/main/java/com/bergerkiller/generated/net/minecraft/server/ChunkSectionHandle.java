package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class ChunkSectionHandle extends Template.Handle {
    public static final ChunkSectionClass T = new ChunkSectionClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkSectionHandle.class, "net.minecraft.server.ChunkSection");


    /* ============================================================================== */

    public static ChunkSectionHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChunkSectionHandle handle = new ChunkSectionHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final ChunkSectionHandle createNew(int y, boolean hasSkyLight) {
        return T.constr_y_hasSkyLight.newInstance(y, hasSkyLight);
    }

    /* ============================================================================== */

    public static final class ChunkSectionClass extends Template.Class<ChunkSectionHandle> {
        public final Template.Constructor.Converted<ChunkSectionHandle> constr_y_hasSkyLight = new Template.Constructor.Converted<ChunkSectionHandle>();

    }
}
