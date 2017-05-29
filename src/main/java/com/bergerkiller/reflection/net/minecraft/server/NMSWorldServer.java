package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import java.util.Map;
import java.util.UUID;

public class NMSWorldServer extends NMSWorld {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("WorldServer");
    
    public static final FieldAccessor<Object> playerChunkMap = WorldServerHandle.T.playerChunkMapField.raw.toFieldAccessor();
    public static final FieldAccessor<Object> server = WorldServerHandle.T.minecraftServer.raw.toFieldAccessor();
    public static final FieldAccessor<EntityTracker> entityTracker = WorldServerHandle.T.entityTracker.toFieldAccessor();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static final FieldAccessor<Map<UUID, Object>> entitiesByUUID = (FieldAccessor) WorldServerHandle.T.entitiesByUUID.raw.toFieldAccessor();

    public static final FieldAccessor<Object> chunkProviderServer = WorldHandle.T.chunkProvider.raw.toFieldAccessor();
}
