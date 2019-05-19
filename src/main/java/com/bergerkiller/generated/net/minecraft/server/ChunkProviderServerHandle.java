package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

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

    public abstract boolean isLoaded(int cx, int cz);
    public abstract ChunkHandle getChunkIfLoaded(int cx, int cz);
    public abstract ChunkHandle getChunkAt(int cx, int cz);
    public abstract Executor getAsyncExecutor();
    public abstract void getChunkAtAsync(int cx, int cz, Consumer<?> consumer);
    public abstract void saveLoadedChunk(ChunkHandle chunk);
    public abstract void markBlockDirty(BlockPositionHandle blockPosition);
    public abstract WorldServerHandle getWorld();
    public abstract void setWorld(WorldServerHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.ChunkProviderServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkProviderServerClass extends Template.Class<ChunkProviderServerHandle> {
        public final Template.Field.Converted<WorldServerHandle> world = new Template.Field.Converted<WorldServerHandle>();

        public final Template.Method<Boolean> isLoaded = new Template.Method<Boolean>();
        public final Template.Method.Converted<ChunkHandle> getChunkIfLoaded = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method.Converted<ChunkHandle> getChunkAt = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method<Executor> getAsyncExecutor = new Template.Method<Executor>();
        public final Template.Method<Void> getChunkAtAsync = new Template.Method<Void>();
        public final Template.Method.Converted<Void> saveLoadedChunk = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> markBlockDirty = new Template.Method.Converted<Void>();

    }

}

