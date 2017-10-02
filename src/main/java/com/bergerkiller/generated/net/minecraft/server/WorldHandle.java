package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.List;
import java.util.Random;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.World</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class WorldHandle extends Template.Handle {
    /** @See {@link WorldClass} */
    public static final WorldClass T = new WorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldHandle.class, "net.minecraft.server.World");

    /* ============================================================================== */

    public static WorldHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public World getWorld() {
        return T.getWorld.invoke(getRaw());
    }

    public Server getServer() {
        return T.getServer.invoke(getRaw());
    }

    public BlockData getBlockData(IntVector3 blockposition) {
        return T.getBlockData.invoke(getRaw(), blockposition);
    }

    public BlockData getBlockDataAtCoord(int x, int y, int z) {
        return T.getBlockDataAtCoord.invoke(getRaw(), x, y, z);
    }

    public boolean setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags) {
        return T.setBlockData.invoke(getRaw(), blockposition, iblockdata, updateFlags);
    }

    public long getTime() {
        return T.getTime.invoke(getRaw());
    }

    public List<?> getRawEntitiesOfType(Class<?> rawType, AxisAlignedBBHandle bounds) {
        return T.getRawEntitiesOfType.invoke(getRaw(), rawType, bounds);
    }

    public List<AxisAlignedBBHandle> getCubes(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        return T.getCubes.invoke(getRaw(), entity, axisalignedbb);
    }

    public List<EntityHandle> getEntities(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        return T.getEntities.invoke(getRaw(), entity, axisalignedbb);
    }

    public TileEntityHandle getTileEntity(IntVector3 blockposition) {
        return T.getTileEntity.invoke(getRaw(), blockposition);
    }

    public WorldDataHandle getWorldData() {
        return T.getWorldData.invoke(getRaw());
    }

    public boolean isBurnArea(AxisAlignedBBHandle bounds) {
        return T.isBurnArea.invoke(getRaw(), bounds);
    }

    public void removeEntity(EntityHandle entity) {
        T.removeEntity.invoke(getRaw(), entity);
    }

    public boolean addEntity(EntityHandle entity) {
        return T.addEntity.invoke(getRaw(), entity);
    }

    public Entity getEntityById(int entityId) {
        return T.getEntityById.invoke(getRaw(), entityId);
    }

    public IDataManagerHandle getDataManager() {
        return T.getDataManager.invoke(getRaw());
    }

    public float getExplosionFactor(Vector vec3d, AxisAlignedBBHandle bounds) {
        return T.getExplosionFactor.invoke(getRaw(), vec3d, bounds);
    }

    public boolean areChunksLoaded(IntVector3 blockposition, int distance) {
        return T.areChunksLoaded.invoke(getRaw(), blockposition, distance);
    }

    public MovingObjectPositionHandle rayTrace(Vector point1, Vector point2, boolean flag) {
        return T.rayTrace.invoke(getRaw(), point1, point2, flag);
    }

    public boolean isChunkLoaded(int cx, int cz, boolean flag) {
        return T.isChunkLoaded.invoke(getRaw(), cx, cz, flag);
    }


    public void applyPhysics(IntVector3 position, BlockData causeType, boolean self) {
        if (T.opt_applyPhysics.isAvailable()) {
            T.opt_applyPhysics.invoke(getRaw(), position, causeType, self);
        } else if (T.opt_applyPhysics_old.isAvailable()) {
            T.opt_applyPhysics_old.invoke(getRaw(), position, causeType);
        } else {
            throw new UnsupportedOperationException("Apply physics function not available on this server");
        }
    }


    public org.bukkit.World toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toWorld.convert(getRaw());
    }

    public static WorldHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }
    public List<EntityHandle> getEntityList() {
        return T.entityList.get(getRaw());
    }

    public void setEntityList(List<EntityHandle> value) {
        T.entityList.set(getRaw(), value);
    }

    public List<EntityHumanHandle> getPlayers() {
        return T.players.get(getRaw());
    }

    public void setPlayers(List<EntityHumanHandle> value) {
        T.players.set(getRaw(), value);
    }

    public Random getRandom() {
        return T.random.get(getRaw());
    }

    public void setRandom(Random value) {
        T.random.set(getRaw(), value);
    }

    public WorldProviderHandle getWorldProvider() {
        return T.worldProvider.get(getRaw());
    }

    public void setWorldProvider(WorldProviderHandle value) {
        T.worldProvider.set(getRaw(), value);
    }

    public List<IWorldAccessHandle> getAccessList() {
        return T.accessList.get(getRaw());
    }

    public void setAccessList(List<IWorldAccessHandle> value) {
        T.accessList.set(getRaw(), value);
    }

    public MethodProfilerHandle getMethodProfiler() {
        return T.methodProfiler.get(getRaw());
    }

    public void setMethodProfiler(MethodProfilerHandle value) {
        T.methodProfiler.set(getRaw(), value);
    }

    public World getBukkitWorld() {
        return T.bukkitWorld.get(getRaw());
    }

    public void setBukkitWorld(World value) {
        T.bukkitWorld.set(getRaw(), value);
    }

    public boolean isKeepSpawnInMemory() {
        return T.keepSpawnInMemory.getBoolean(getRaw());
    }

    public void setKeepSpawnInMemory(boolean value) {
        T.keepSpawnInMemory.setBoolean(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.World</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldClass extends Template.Class<WorldHandle> {
        public final Template.Field.Converted<List<EntityHandle>> entityList = new Template.Field.Converted<List<EntityHandle>>();
        @Template.Optional
        public final Template.Field.Converted<List<TileEntityHandle>> tileEntityList = new Template.Field.Converted<List<TileEntityHandle>>();
        public final Template.Field.Converted<List<EntityHumanHandle>> players = new Template.Field.Converted<List<EntityHumanHandle>>();
        public final Template.Field<Random> random = new Template.Field<Random>();
        public final Template.Field.Converted<WorldProviderHandle> worldProvider = new Template.Field.Converted<WorldProviderHandle>();
        @Template.Optional
        public final Template.Field.Converted<Object> navigationListener = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<List<IWorldAccessHandle>> accessList = new Template.Field.Converted<List<IWorldAccessHandle>>();
        @Template.Optional
        public final Template.Field.Converted<Object> field_chunkProvider = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<MethodProfilerHandle> methodProfiler = new Template.Field.Converted<MethodProfilerHandle>();
        public final Template.Field.Converted<World> bukkitWorld = new Template.Field.Converted<World>();
        public final Template.Field.Boolean keepSpawnInMemory = new Template.Field.Boolean();

        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted<World>();
        public final Template.Method.Converted<Server> getServer = new Template.Method.Converted<Server>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method<BlockData> getBlockDataAtCoord = new Template.Method<BlockData>();
        public final Template.Method.Converted<Boolean> setBlockData = new Template.Method.Converted<Boolean>();
        public final Template.Method<Long> getTime = new Template.Method<Long>();
        @Template.Optional
        public final Template.Method.Converted<Object> getChunkProvider = new Template.Method.Converted<Object>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<List<AxisAlignedBBHandle>> getBlockAndEntityCollisions = new Template.Method.Converted<List<AxisAlignedBBHandle>>();
        public final Template.Method.Converted<List<?>> getRawEntitiesOfType = new Template.Method.Converted<List<?>>();
        public final Template.Method.Converted<List<AxisAlignedBBHandle>> getCubes = new Template.Method.Converted<List<AxisAlignedBBHandle>>();
        public final Template.Method.Converted<List<EntityHandle>> getEntities = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<TileEntityHandle> getTileEntity = new Template.Method.Converted<TileEntityHandle>();
        public final Template.Method.Converted<WorldDataHandle> getWorldData = new Template.Method.Converted<WorldDataHandle>();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Void> removeEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> addEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Entity> getEntityById = new Template.Method.Converted<Entity>();
        public final Template.Method.Converted<IDataManagerHandle> getDataManager = new Template.Method.Converted<IDataManagerHandle>();
        public final Template.Method.Converted<Float> getExplosionFactor = new Template.Method.Converted<Float>();
        public final Template.Method.Converted<Boolean> areChunksLoaded = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<MovingObjectPositionHandle> rayTrace = new Template.Method.Converted<MovingObjectPositionHandle>();
        @Template.Optional
        public final Template.Method.Converted<Void> opt_applyPhysics = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> opt_applyPhysics_old = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isChunkLoaded = new Template.Method<Boolean>();

    }

}

