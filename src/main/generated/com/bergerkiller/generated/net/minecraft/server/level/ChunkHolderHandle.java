package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.ChunkHolder</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.ChunkHolder")
public abstract class ChunkHolderHandle extends Template.Handle {
    /** @see ChunkHolderClass */
    public static final ChunkHolderClass T = Template.Class.create(ChunkHolderClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkHolderHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChunkMapHandle getPlayerChunkMap();
    public abstract boolean resendChunk();
    public abstract boolean resendAllLighting();
    public abstract Collection<Player> getPlayers();
    public abstract IntVector2 getLocation();
    public abstract Chunk getChunkIfLoaded();
    /**
     * Stores class members for <b>net.minecraft.server.level.ChunkHolder</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkHolderClass extends Template.Class<ChunkHolderHandle> {
        public final Template.Method.Converted<ChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<ChunkMapHandle>();
        public final Template.Method<Boolean> resendChunk = new Template.Method<Boolean>();
        public final Template.Method<Boolean> resendAllLighting = new Template.Method<Boolean>();
        public final Template.Method.Converted<Collection<Player>> getPlayers = new Template.Method.Converted<Collection<Player>>();
        public final Template.Method.Converted<IntVector2> getLocation = new Template.Method.Converted<IntVector2>();
        public final Template.Method.Converted<Chunk> getChunkIfLoaded = new Template.Method.Converted<Chunk>();

    }

}

