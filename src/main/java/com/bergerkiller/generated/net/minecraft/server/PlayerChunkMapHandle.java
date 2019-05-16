package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerChunkMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PlayerChunkMapHandle extends Template.Handle {
    /** @See {@link PlayerChunkMapClass} */
    public static final PlayerChunkMapClass T = new PlayerChunkMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerChunkMapHandle.class, "net.minecraft.server.PlayerChunkMap", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PlayerChunkMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract PlayerChunkHandle getChunk(int x, int z);
    public abstract boolean isChunkEntered(EntityPlayerHandle entityplayer, int chunkX, int chunkZ);
    /**
     * Stores class members for <b>net.minecraft.server.PlayerChunkMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerChunkMapClass extends Template.Class<PlayerChunkMapHandle> {
        public final Template.Method.Converted<PlayerChunkHandle> getChunk = new Template.Method.Converted<PlayerChunkHandle>();
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();

    }

}

