package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle.BiomeMetaHandle;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChunkProviderServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ChunkProviderServerHandle extends Template.Handle {
    /** @See {@link ChunkProviderServerClass} */
    public static final ChunkProviderServerClass T = new ChunkProviderServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkProviderServerHandle.class, "net.minecraft.server.ChunkProviderServer", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static ChunkProviderServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<BiomeMetaHandle> getBiomeSpawnInfo(Object enumcreaturetype, IntVector3 position);
    public abstract boolean isLoaded(int cx, int cz);
    public abstract ChunkHandle getChunkIfLoaded(int cx, int cz);
    public abstract ChunkHandle getChunkAt(int cx, int cz);
    public abstract ChunkHandle getChunkAtAsync(int cx, int cz, Runnable runnable);

    public void saveLoadedChunk(ChunkHandle chunk) {
        if (T.saveChunk_new.isAvailable()) {
            T.saveChunk_new.invoke(getRaw(), chunk, false);
        } else {
            T.saveChunk_old.invoke(getRaw(), chunk);
        }
    }
    public abstract Object getChunkLoader();
    public abstract void setChunkLoader(Object value);
    public abstract WorldServerHandle getWorld();
    public abstract void setWorld(WorldServerHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.ChunkProviderServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkProviderServerClass extends Template.Class<ChunkProviderServerHandle> {
        public final Template.Field.Converted<Object> chunkLoader = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<WorldServerHandle> world = new Template.Field.Converted<WorldServerHandle>();

        public final Template.Method.Converted<List<BiomeMetaHandle>> getBiomeSpawnInfo = new Template.Method.Converted<List<BiomeMetaHandle>>();
        public final Template.Method<Boolean> isLoaded = new Template.Method<Boolean>();
        public final Template.Method.Converted<ChunkHandle> getChunkIfLoaded = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method.Converted<ChunkHandle> getChunkAt = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method.Converted<ChunkHandle> getChunkAtAsync = new Template.Method.Converted<ChunkHandle>();
        @Template.Optional
        public final Template.Method.Converted<Void> saveChunk_old = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> saveChunk_new = new Template.Method.Converted<Void>();

    }

}

