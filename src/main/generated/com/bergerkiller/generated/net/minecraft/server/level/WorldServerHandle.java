package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ForcedChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import java.util.List;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.WorldServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.WorldServer")
public abstract class WorldServerHandle extends WorldHandle {
    /** @see WorldServerClass */
    public static final WorldServerClass T = Template.Class.create(WorldServerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static WorldServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static World getByDimensionKey(ResourceKey<World> key) {
        return T.getByDimensionKey.invoke(key);
    }

    public abstract PlayerChunkMapHandle getPlayerChunkMap();
    public abstract ChunkProviderServerHandle getChunkProviderServer();
    public abstract boolean isLoaded();
    public abstract List<EntityPlayerHandle> getPlayers();
    public abstract Entity getEntityByUUID(UUID entityUUID);
    public abstract EntityTracker getEntityTracker();
    public abstract Object getEntityTrackerHandle();
    public abstract void setEntityTrackerHandle(Object entityTrackerHandle);
    public abstract ResourceKey<World> getDimensionKey();
    public abstract Chunk getChunkIfLoaded(int cx, int cz);
    public abstract void setForceLoadedAsync(int x, int z, Plugin plugin, boolean loaded, int radius);
    public abstract Iterable<Entity> getEntities();
    public abstract void removeEntity(EntityHandle entity);
    public abstract void removeEntityWithoutDeath(EntityHandle entity);
    public abstract boolean addEntity(EntityHandle entity);
    public abstract MinecraftServerHandle getMinecraftServer();
    public abstract void saveLevel();
    public abstract Location findSafeSpawn(PlayerRespawnPointNearBlock respawnPoint, boolean isDeathRespawn);
    @Deprecated
    public org.bukkit.Location findSafeSpawn(PlayerRespawnPointNearBlock respawnPoint, boolean alsoWhenDestroyed, boolean isDeathRespawn) {
        return findSafeSpawn(respawnPoint.withForced(alsoWhenDestroyed), isDeathRespawn);
    }

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
    /**
     * Stores class members for <b>net.minecraft.server.level.WorldServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldServerClass extends Template.Class<WorldServerHandle> {
        @Template.Optional
        public final Template.Field.Converted<ChunkProviderServerHandle> field_chunkProviderServer = new Template.Field.Converted<ChunkProviderServerHandle>();

        public final Template.StaticMethod.Converted<World> getByDimensionKey = new Template.StaticMethod.Converted<World>();

        public final Template.Method.Converted<PlayerChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<PlayerChunkMapHandle>();
        public final Template.Method.Converted<ChunkProviderServerHandle> getChunkProviderServer = new Template.Method.Converted<ChunkProviderServerHandle>();
        public final Template.Method<Boolean> isLoaded = new Template.Method<Boolean>();
        public final Template.Method.Converted<List<EntityPlayerHandle>> getPlayers = new Template.Method.Converted<List<EntityPlayerHandle>>();
        public final Template.Method.Converted<Entity> getEntityByUUID = new Template.Method.Converted<Entity>();
        public final Template.Method<EntityTracker> getEntityTracker = new Template.Method<EntityTracker>();
        public final Template.Method<Object> getEntityTrackerHandle = new Template.Method<Object>();
        public final Template.Method.Converted<Void> setEntityTrackerHandle = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ResourceKey<World>> getDimensionKey = new Template.Method.Converted<ResourceKey<World>>();
        public final Template.Method.Converted<Chunk> getChunkIfLoaded = new Template.Method.Converted<Chunk>();
        public final Template.Method<Void> setForceLoadedAsync = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method.Converted<ForcedChunkHandle> getForcedChunk = new Template.Method.Converted<ForcedChunkHandle>();
        public final Template.Method.Converted<Iterable<Entity>> getEntities = new Template.Method.Converted<Iterable<Entity>>();
        public final Template.Method.Converted<Void> removeEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> removeEntityWithoutDeath = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> addEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<MinecraftServerHandle> getMinecraftServer = new Template.Method.Converted<MinecraftServerHandle>();
        public final Template.Method<Void> saveLevel = new Template.Method<Void>();
        public final Template.Method.Converted<Location> findSafeSpawn = new Template.Method.Converted<Location>();

    }

}

