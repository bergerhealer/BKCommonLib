package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Server;
import org.bukkit.util.Vector;
import java.util.Random;
import org.bukkit.World;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.List;

public class WorldHandle extends Template.Handle {
    public static final WorldClass T = new WorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldHandle.class, "net.minecraft.server.World");

    /* ============================================================================== */

    public static WorldHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        WorldHandle handle = new WorldHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public World getWorld() {
        return T.getWorld.invoke(instance);
    }

    public Server getServer() {
        return T.getServer.invoke(instance);
    }

    public BlockData getBlockData(IntVector3 blockposition) {
        return T.getBlockData.invoke(instance, blockposition);
    }

    public boolean setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags) {
        return T.setBlockData.invoke(instance, blockposition, iblockdata, updateFlags);
    }

    public long getTime() {
        return T.getTime.invoke(instance);
    }

    public boolean getBlockCollisions(EntityHandle entity, AxisAlignedBBHandle bounds, boolean flag, List<AxisAlignedBBHandle> list) {
        return T.getBlockCollisions.invoke(instance, entity, bounds, flag, list);
    }

    public List<?> getRawEntitiesOfType(Class<?> rawType, AxisAlignedBBHandle bounds) {
        return T.getRawEntitiesOfType.invoke(instance, rawType, bounds);
    }

    public List<AxisAlignedBBHandle> getCubes(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        return T.getCubes.invoke(instance, entity, axisalignedbb);
    }

    public List<EntityHandle> getEntities(EntityHandle entity, AxisAlignedBBHandle axisalignedbb) {
        return T.getEntities.invoke(instance, entity, axisalignedbb);
    }

    public TileEntityHandle getTileEntity(IntVector3 blockposition) {
        return T.getTileEntity.invoke(instance, blockposition);
    }

    public WorldDataHandle getWorldData() {
        return T.getWorldData.invoke(instance);
    }

    public boolean isBurnArea(AxisAlignedBBHandle bounds) {
        return T.isBurnArea.invoke(instance, bounds);
    }

    public void removeEntity(EntityHandle entity) {
        T.removeEntity.invoke(instance, entity);
    }

    public boolean addEntity(EntityHandle entity) {
        return T.addEntity.invoke(instance, entity);
    }

    public IDataManagerHandle getDataManager() {
        return T.getDataManager.invoke(instance);
    }

    public float getExplosionFactor(Vector vec3d, AxisAlignedBBHandle bounds) {
        return T.getExplosionFactor.invoke(instance, vec3d, bounds);
    }

    public boolean areChunksLoaded(IntVector3 blockposition, int distance) {
        return T.areChunksLoaded.invoke(instance, blockposition, distance);
    }

    public MovingObjectPositionHandle rayTrace(Vector point1, Vector point2, boolean flag) {
        return T.rayTrace.invoke(instance, point1, point2, flag);
    }

    public void applyPhysics(IntVector3 position, BlockData causeType, boolean self) {
        T.applyPhysics.invoke(instance, position, causeType, self);
    }

    public boolean isChunkLoaded(int cx, int cz, boolean flag) {
        return T.isChunkLoaded.invoke(instance, cx, cz, flag);
    }


    public org.bukkit.World toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toWorld.convert(instance);
    }

    public static WorldHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }
    public List<EntityHandle> getEntityList() {
        return T.entityList.get(instance);
    }

    public void setEntityList(List<EntityHandle> value) {
        T.entityList.set(instance, value);
    }

    public List<EntityHumanHandle> getPlayers() {
        return T.players.get(instance);
    }

    public void setPlayers(List<EntityHumanHandle> value) {
        T.players.set(instance, value);
    }

    public Random getRandom() {
        return T.random.get(instance);
    }

    public void setRandom(Random value) {
        T.random.set(instance, value);
    }

    public WorldProviderHandle getWorldProvider() {
        return T.worldProvider.get(instance);
    }

    public void setWorldProvider(WorldProviderHandle value) {
        T.worldProvider.set(instance, value);
    }

    public Object getNavigationListener() {
        return T.navigationListener.get(instance);
    }

    public void setNavigationListener(Object value) {
        T.navigationListener.set(instance, value);
    }

    public List<IWorldAccessHandle> getAccessList() {
        return T.accessList.get(instance);
    }

    public void setAccessList(List<IWorldAccessHandle> value) {
        T.accessList.set(instance, value);
    }

    public Object getChunkProvider() {
        return T.chunkProvider.get(instance);
    }

    public void setChunkProvider(Object value) {
        T.chunkProvider.set(instance, value);
    }

    public MethodProfilerHandle getMethodProfiler() {
        return T.methodProfiler.get(instance);
    }

    public void setMethodProfiler(MethodProfilerHandle value) {
        T.methodProfiler.set(instance, value);
    }

    public World getBukkitWorld() {
        return T.bukkitWorld.get(instance);
    }

    public void setBukkitWorld(World value) {
        T.bukkitWorld.set(instance, value);
    }

    public boolean isKeepSpawnInMemory() {
        return T.keepSpawnInMemory.getBoolean(instance);
    }

    public void setKeepSpawnInMemory(boolean value) {
        T.keepSpawnInMemory.setBoolean(instance, value);
    }

    public static final class WorldClass extends Template.Class<WorldHandle> {
        public final Template.Field.Converted<List<EntityHandle>> entityList = new Template.Field.Converted<List<EntityHandle>>();
        @Template.Optional
        public final Template.Field.Converted<List<TileEntityHandle>> tileEntityList = new Template.Field.Converted<List<TileEntityHandle>>();
        public final Template.Field.Converted<List<EntityHumanHandle>> players = new Template.Field.Converted<List<EntityHumanHandle>>();
        public final Template.Field<Random> random = new Template.Field<Random>();
        public final Template.Field.Converted<WorldProviderHandle> worldProvider = new Template.Field.Converted<WorldProviderHandle>();
        public final Template.Field.Converted<Object> navigationListener = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<List<IWorldAccessHandle>> accessList = new Template.Field.Converted<List<IWorldAccessHandle>>();
        public final Template.Field.Converted<Object> chunkProvider = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<MethodProfilerHandle> methodProfiler = new Template.Field.Converted<MethodProfilerHandle>();
        public final Template.Field.Converted<World> bukkitWorld = new Template.Field.Converted<World>();
        public final Template.Field.Boolean keepSpawnInMemory = new Template.Field.Boolean();

        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted<World>();
        public final Template.Method.Converted<Server> getServer = new Template.Method.Converted<Server>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Boolean> setBlockData = new Template.Method.Converted<Boolean>();
        public final Template.Method<Long> getTime = new Template.Method<Long>();
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<List<?>> getRawEntitiesOfType = new Template.Method.Converted<List<?>>();
        public final Template.Method.Converted<List<AxisAlignedBBHandle>> getCubes = new Template.Method.Converted<List<AxisAlignedBBHandle>>();
        public final Template.Method.Converted<List<EntityHandle>> getEntities = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<TileEntityHandle> getTileEntity = new Template.Method.Converted<TileEntityHandle>();
        public final Template.Method.Converted<WorldDataHandle> getWorldData = new Template.Method.Converted<WorldDataHandle>();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Void> removeEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> addEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<IDataManagerHandle> getDataManager = new Template.Method.Converted<IDataManagerHandle>();
        public final Template.Method.Converted<Float> getExplosionFactor = new Template.Method.Converted<Float>();
        public final Template.Method.Converted<Boolean> areChunksLoaded = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<MovingObjectPositionHandle> rayTrace = new Template.Method.Converted<MovingObjectPositionHandle>();
        public final Template.Method.Converted<Void> applyPhysics = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isChunkLoaded = new Template.Method<Boolean>();

    }

}

