package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.Set;

public class ChunkProviderServerHandle extends Template.Handle {
    public static final ChunkProviderServerClass T = new ChunkProviderServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkProviderServerHandle.class, "net.minecraft.server.ChunkProviderServer");


    /* ============================================================================== */

    public static ChunkProviderServerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChunkProviderServerHandle handle = new ChunkProviderServerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public ChunkHandle getChunkAt(int cx, int cz) {
        return T.getChunkAt.invoke(instance, cx, cz);
    }

    public void saveChunk(ChunkHandle chunk) {
        T.saveChunk.invoke(instance, chunk);
    }

    public Set<Long> getUnloadQueue() {
        return T.unloadQueue.get(instance);
    }

    public void setUnloadQueue(Set<Long> value) {
        T.unloadQueue.set(instance, value);
    }

    public Object getChunks() {
        return T.chunks.get(instance);
    }

    public void setChunks(Object value) {
        T.chunks.set(instance, value);
    }

    public static final class ChunkProviderServerClass extends Template.Class<ChunkProviderServerHandle> {
        public final Template.Field<Set<Long>> unloadQueue = new Template.Field<Set<Long>>();
        public final Template.Field.Converted<Object> chunks = new Template.Field.Converted<Object>();

        public final Template.Method.Converted<ChunkHandle> getChunkAt = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method.Converted<Void> saveChunk = new Template.Method.Converted<Void>();

    }
}
