package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.ChunkMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.ChunkMap")
public abstract class ChunkMapHandle extends Template.Handle {
    /** @see ChunkMapClass */
    public static final ChunkMapClass T = Template.Class.create(ChunkMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChunkHolderHandle getVisibleChunk(int x, int z);
    public abstract ChunkHolderHandle getUpdatingChunk(int x, int z);
    public abstract boolean isChunkEntered(ServerPlayerHandle entityplayer, int chunkX, int chunkZ);
    public java.util.Collection<org.bukkit.entity.Player> getChunkEnteredPlayers(int chunkX, int chunkZ) {
        ChunkHolderHandle playerChunk = getVisibleChunk(chunkX, chunkZ);
        if (playerChunk == null || playerChunk.getChunkIfLoaded() == null) {
            return java.util.Collections.emptyList();
        } else {
            return playerChunk.getPlayers();
        }
    }
    /**
     * Stores class members for <b>net.minecraft.server.level.ChunkMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkMapClass extends Template.Class<ChunkMapHandle> {
        public final Template.Method.Converted<ChunkHolderHandle> getVisibleChunk = new Template.Method.Converted<ChunkHolderHandle>();
        public final Template.Method.Converted<ChunkHolderHandle> getUpdatingChunk = new Template.Method.Converted<ChunkHolderHandle>();
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Void> trackEntity = new Template.Method.Converted<Void>();

    }

}

