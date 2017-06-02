package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class PlayerChunkMapHandle extends Template.Handle {
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

    public static final class PlayerChunkMapClass extends Template.Class<PlayerChunkMapHandle> {
        public final Template.Method.Converted<Boolean> isChunkEntered = new Template.Method.Converted<Boolean>();

    }

}

