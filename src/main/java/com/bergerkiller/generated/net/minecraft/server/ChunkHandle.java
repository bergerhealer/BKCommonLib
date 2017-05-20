package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class ChunkHandle extends Template.Handle {
    public static final ChunkClass T = new ChunkClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkHandle.class, "net.minecraft.server.Chunk");


    /* ============================================================================== */

    public static ChunkHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChunkHandle handle = new ChunkHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public BlockData getBlockData(IntVector3 blockposition) {
        return T.getBlockData.invokeVA(instance, blockposition);
    }

    public Object getEntitySlices() {
        return T.entitySlices.get(instance);
    }

    public void setEntitySlices(Object value) {
        T.entitySlices.set(instance, value);
    }

    public static final class ChunkClass extends Template.Class<ChunkHandle> {
        public final Template.Field.Converted<Object> entitySlices = new Template.Field.Converted<Object>();

        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();

    }
}
