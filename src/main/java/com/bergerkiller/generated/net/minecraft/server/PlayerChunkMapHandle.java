package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PlayerChunkMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PlayerChunkMapHandle extends Template.Handle {
    /** @See {@link PlayerChunkMapClass} */
    public static final PlayerChunkMapClass T = new PlayerChunkMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PlayerChunkMapHandle.class, "net.minecraft.server.PlayerChunkMap");

    /* ============================================================================== */

    public static PlayerChunkMapHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PlayerChunkMapHandle handle = new PlayerChunkMapHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public boolean isChunkEntered(EntityPlayerHandle entityplayer, int chunkX, int chunkZ) {
        return T.isChunkEntered.invoke(instance, entityplayer, chunkX, chunkZ);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PlayerChunkMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerChunkMapClass extends Template.Class<PlayerChunkMapHandle> {
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();

    }

}

