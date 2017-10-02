package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import java.util.Map;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class WorldServerHandle extends WorldHandle {
    /** @See {@link WorldServerClass} */
    public static final WorldServerClass T = new WorldServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldServerHandle.class, "net.minecraft.server.WorldServer");

    /* ============================================================================== */

    public static WorldServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public PlayerChunkMapHandle getPlayerChunkMap() {
        return T.getPlayerChunkMap.invoke(getRaw());
    }

    public void saveLevel() {
        T.saveLevel.invoke(getRaw());
    }


    public static WorldServerHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }

    public ChunkProviderServerHandle getChunkProviderServer() {
        if (T.getChunkProviderServer.isAvailable()) {
            return T.getChunkProviderServer.invoke(getRaw());
        } else if (WorldHandle.T.getChunkProvider.isAvailable()) {
            return ChunkProviderServerHandle.createHandle(WorldHandle.T.getChunkProvider.invoke(getRaw()));
        } else if (T.field_chunkProviderServer.isAvailable()) {
            return T.field_chunkProviderServer.get(getRaw());   
        } else {
            throw new UnsupportedOperationException("Chunk Provider Server can not be accessed for worlds on this server");
        }
    }

    public void setChunkProviderServer(ChunkProviderServerHandle chunkProviderServerHandle) {
        if (T.field_chunkProviderServer.isAvailable()) {
            T.field_chunkProviderServer.set(getRaw(), chunkProviderServerHandle);
        }
        if (WorldHandle.T.field_chunkProvider.isAvailable()) {
            WorldHandle.T.field_chunkProvider.set(getRaw(), chunkProviderServerHandle.getRaw());
        }
    }
    public MinecraftServerHandle getMinecraftServer() {
        return T.minecraftServer.get(getRaw());
    }

    public void setMinecraftServer(MinecraftServerHandle value) {
        T.minecraftServer.set(getRaw(), value);
    }

    public EntityTracker getEntityTracker() {
        return T.entityTracker.get(getRaw());
    }

    public void setEntityTracker(EntityTracker value) {
        T.entityTracker.set(getRaw(), value);
    }

    public PlayerChunkMapHandle getPlayerChunkMapField() {
        return T.playerChunkMapField.get(getRaw());
    }

    public void setPlayerChunkMapField(PlayerChunkMapHandle value) {
        T.playerChunkMapField.set(getRaw(), value);
    }

    public Map<UUID, EntityHandle> getEntitiesByUUID() {
        return T.entitiesByUUID.get(getRaw());
    }

    public void setEntitiesByUUID(Map<UUID, EntityHandle> value) {
        T.entitiesByUUID.set(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.WorldServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldServerClass extends Template.Class<WorldServerHandle> {
        public final Template.Field.Converted<MinecraftServerHandle> minecraftServer = new Template.Field.Converted<MinecraftServerHandle>();
        public final Template.Field.Converted<EntityTracker> entityTracker = new Template.Field.Converted<EntityTracker>();
        public final Template.Field.Converted<PlayerChunkMapHandle> playerChunkMapField = new Template.Field.Converted<PlayerChunkMapHandle>();
        public final Template.Field.Converted<Map<UUID, EntityHandle>> entitiesByUUID = new Template.Field.Converted<Map<UUID, EntityHandle>>();
        @Template.Optional
        public final Template.Field.Converted<ChunkProviderServerHandle> field_chunkProviderServer = new Template.Field.Converted<ChunkProviderServerHandle>();

        public final Template.Method.Converted<PlayerChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<PlayerChunkMapHandle>();
        @Template.Optional
        public final Template.Method.Converted<ChunkProviderServerHandle> getChunkProviderServer = new Template.Method.Converted<ChunkProviderServerHandle>();
        public final Template.Method<Void> saveLevel = new Template.Method<Void>();

    }

}

