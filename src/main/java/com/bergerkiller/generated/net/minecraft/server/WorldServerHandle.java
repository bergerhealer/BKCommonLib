package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Dimension;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import java.util.Map;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldServerHandle extends WorldHandle {
    /** @See {@link WorldServerClass} */
    public static final WorldServerClass T = new WorldServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldServerHandle.class, "net.minecraft.server.WorldServer", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static WorldServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract PlayerChunkMapHandle getPlayerChunkMap();
    public abstract ChunkProviderServerHandle getChunkProviderServer();
    public abstract void saveLevel();

    public static WorldServerHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }

    public void setChunkProviderServer(ChunkProviderServerHandle chunkProviderServerHandle) {
        if (T.field_chunkProviderServer.isAvailable()) {
            T.field_chunkProviderServer.set(getRaw(), chunkProviderServerHandle);
        }
        if (WorldHandle.T.field_chunkProvider.isAvailable()) {
            WorldHandle.T.field_chunkProvider.set(getRaw(), chunkProviderServerHandle.getRaw());
        }
    }
    public abstract MinecraftServerHandle getMinecraftServer();
    public abstract void setMinecraftServer(MinecraftServerHandle value);
    public abstract EntityTracker getEntityTracker();
    public abstract void setEntityTracker(EntityTracker value);
    public abstract PlayerChunkMapHandle getPlayerChunkMapField();
    public abstract void setPlayerChunkMapField(PlayerChunkMapHandle value);
    public abstract Map<UUID, EntityHandle> getEntitiesByUUID();
    public abstract void setEntitiesByUUID(Map<UUID, EntityHandle> value);
    public abstract Dimension getDimension();
    public abstract void setDimension(Dimension value);
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
        public final Template.Field.Converted<Dimension> dimension = new Template.Field.Converted<Dimension>();

        public final Template.Method.Converted<PlayerChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<PlayerChunkMapHandle>();
        public final Template.Method.Converted<ChunkProviderServerHandle> getChunkProviderServer = new Template.Method.Converted<ChunkProviderServerHandle>();
        public final Template.Method<Void> saveLevel = new Template.Method<Void>();

    }

}

