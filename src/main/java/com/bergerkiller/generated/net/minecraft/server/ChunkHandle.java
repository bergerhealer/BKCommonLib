package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class ChunkHandle extends Template.Handle {
    public static final ChunkClass T = new ChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkHandle.class, "net.minecraft.server.Chunk");


    /* ============================================================================== */

    public static final ChunkHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChunkHandle handle = new ChunkHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object getEntitySlices() {
        return T.entitySlices.get(instance);
    }

    public void setEntitySlices(Object value) {
        T.entitySlices.set(instance, value);
    }

    public static final class ChunkClass extends Template.Class {
        public final Template.Field.Converted<Object> entitySlices = new Template.Field.Converted<Object>();

    }
}
