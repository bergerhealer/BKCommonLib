package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.PlayerChunkMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.PlayerChunkMap")
public abstract class PlayerChunkMapHandle extends Template.Handle {
    /** @see PlayerChunkMapClass */
    public static final PlayerChunkMapClass T = Template.Class.create(PlayerChunkMapClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerChunkMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract PlayerChunkHandle getVisibleChunk(int x, int z);
    public abstract PlayerChunkHandle getUpdatingChunk(int x, int z);
    public abstract boolean isChunkEntered(EntityPlayerHandle entityplayer, int chunkX, int chunkZ);

    public java.util.Collection<org.bukkit.entity.Player> getChunkEnteredPlayers(int chunkX, int chunkZ) {
        PlayerChunkHandle playerChunk = getVisibleChunk(chunkX, chunkZ);
        if (playerChunk == null || playerChunk.getChunkIfLoaded() == null) {
            return java.util.Collections.emptyList();
        } else {
            return playerChunk.getPlayers();
        }
    }
    /**
     * Stores class members for <b>net.minecraft.server.level.PlayerChunkMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerChunkMapClass extends Template.Class<PlayerChunkMapHandle> {
        public final Template.Method.Converted<PlayerChunkHandle> getVisibleChunk = new Template.Method.Converted<PlayerChunkHandle>();
        public final Template.Method.Converted<PlayerChunkHandle> getUpdatingChunk = new Template.Method.Converted<PlayerChunkHandle>();
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Void> trackEntity = new Template.Method.Converted<Void>();

    }

}

