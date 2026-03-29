package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import org.bukkit.Chunk;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.ServerChunkCache</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.ServerChunkCache")
public abstract class ServerChunkCacheHandle extends Template.Handle {
    /** @see ServerChunkCacheClass */
    public static final ServerChunkCacheClass T = Template.Class.create(ServerChunkCacheClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ServerChunkCacheHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Chunk unpackGetChunkAsyncResult(Object result) {
        return T.unpackGetChunkAsyncResult.invoke(result);
    }

    public abstract LevelChunkHandle getChunkAt(int cx, int cz);
    public abstract Executor getAsyncExecutor();
    public abstract void getChunkAtAsync(int cx, int cz, Consumer<?> consumer);
    public abstract void saveLoadedChunk(LevelChunkHandle chunk);
    public abstract void markBlockDirty(BlockPosHandle blockPosition);
    public abstract ServerLevelHandle getWorld();
    public abstract void setWorld(ServerLevelHandle value);
    /**
     * Stores class members for <b>net.minecraft.server.level.ServerChunkCache</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ServerChunkCacheClass extends Template.Class<ServerChunkCacheHandle> {
        public final Template.Field.Converted<ServerLevelHandle> world = new Template.Field.Converted<ServerLevelHandle>();

        public final Template.StaticMethod.Converted<Chunk> unpackGetChunkAsyncResult = new Template.StaticMethod.Converted<Chunk>();

        public final Template.Method.Converted<LevelChunkHandle> getChunkAt = new Template.Method.Converted<LevelChunkHandle>();
        public final Template.Method<Executor> getAsyncExecutor = new Template.Method<Executor>();
        public final Template.Method<Void> getChunkAtAsync = new Template.Method<Void>();
        public final Template.Method.Converted<Void> saveLoadedChunk = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> markBlockDirty = new Template.Method.Converted<Void>();

    }

}

