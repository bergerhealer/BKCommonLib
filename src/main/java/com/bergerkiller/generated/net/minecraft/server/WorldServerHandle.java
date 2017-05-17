package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Map;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.UUID;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;

public class WorldServerHandle extends WorldHandle {
    public static final WorldServerClass T = new WorldServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldServerHandle.class, "net.minecraft.server.WorldServer");


    /* ============================================================================== */

    public static WorldServerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldServerHandle handle = new WorldServerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public PlayerChunkMapHandle getPlayerChunkMap() {
        return T.getPlayerChunkMap.invoke(instance);
    }

    public ChunkProviderServerHandle getChunkProviderServer() {
        return T.getChunkProviderServer.invoke(instance);
    }

    public void saveLevel() {
        T.saveLevel.invoke(instance);
    }

    public Map<UUID, EntityHandle> getEntitiesByUUID() {
        return T.entitiesByUUID.get(instance);
    }

    public void setEntitiesByUUID(Map<UUID, EntityHandle> value) {
        T.entitiesByUUID.set(instance, value);
    }

    public static final class WorldServerClass extends Template.Class<WorldServerHandle> {
        public final Template.Field.Converted<Map<UUID, EntityHandle>> entitiesByUUID = new Template.Field.Converted<Map<UUID, EntityHandle>>();

        public final Template.Method.Converted<PlayerChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<PlayerChunkMapHandle>();
        public final Template.Method.Converted<ChunkProviderServerHandle> getChunkProviderServer = new Template.Method.Converted<ChunkProviderServerHandle>();
        public final Template.Method<Void> saveLevel = new Template.Method<Void>();

    }
}
