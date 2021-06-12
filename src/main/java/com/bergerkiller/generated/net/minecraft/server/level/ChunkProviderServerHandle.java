package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkHandle;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.ChunkProviderServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.ChunkProviderServer")
public abstract class ChunkProviderServerHandle extends Template.Handle {
    /** @See {@link ChunkProviderServerClass} */
    public static final ChunkProviderServerClass T = Template.Class.create(ChunkProviderServerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkProviderServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChunkHandle getChunkAt(int cx, int cz);
    public abstract Executor getAsyncExecutor();
    public abstract void getChunkAtAsync(int cx, int cz, Consumer<?> consumer);
    public abstract void saveLoadedChunk(ChunkHandle chunk);
    public abstract void markBlockDirty(BlockPositionHandle blockPosition);
    public abstract WorldServerHandle getWorld();
    public abstract void setWorld(WorldServerHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.level.ChunkProviderServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkProviderServerClass extends Template.Class<ChunkProviderServerHandle> {
        public final Template.Field.Converted<WorldServerHandle> world = new Template.Field.Converted<WorldServerHandle>();

        public final Template.Method.Converted<ChunkHandle> getChunkAt = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method<Executor> getAsyncExecutor = new Template.Method<Executor>();
        public final Template.Method<Void> getChunkAtAsync = new Template.Method<Void>();
        public final Template.Method.Converted<Void> saveLoadedChunk = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> markBlockDirty = new Template.Method.Converted<Void>();

    }

}

