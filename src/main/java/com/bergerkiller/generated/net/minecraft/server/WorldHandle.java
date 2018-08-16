package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.World</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldHandle extends IBlockAccessHandle {
    /** @See {@link WorldClass} */
    public static final WorldClass T = new WorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldHandle.class, "net.minecraft.server.World");

    /* ============================================================================== */

    public static WorldHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract World getWorld();
    public abstract Server getServer();
    public abstract BlockData getBlockData(IntVector3 blockposition);
    public abstract BlockData getBlockDataAtCoord(int x, int y, int z);
    public abstract boolean setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags);
    public abstract long getTime();
    public abstract VoxelShapeHandle getCollisionShape(EntityHandle entity, AxisAlignedBBHandle boundingBox, double dx, double dy, double dz);
    public abstract List<AxisAlignedBBHandle> getCubes(EntityHandle entity, AxisAlignedBBHandle axisalignedbb);
    public abstract List<?> getRawEntitiesOfType(Class<?> rawType, AxisAlignedBBHandle bounds);
    public abstract List<EntityHandle> getEntities(EntityHandle entity, AxisAlignedBBHandle axisalignedbb);
    public abstract TileEntityHandle getTileEntity(IntVector3 blockposition);
    public abstract WorldDataHandle getWorldData();
    public abstract boolean isBurnArea(AxisAlignedBBHandle bounds);
    public abstract void removeEntity(EntityHandle entity);
    public abstract boolean addEntity(EntityHandle entity);
    public abstract Entity getEntityById(int entityId);
    public abstract IDataManagerHandle getDataManager();
    public abstract float getExplosionFactor(Vector vec3d, AxisAlignedBBHandle bounds);
    public abstract boolean areChunksLoaded(IntVector3 blockposition, int distance);
    public abstract MovingObjectPositionHandle rayTrace(Vector point1, Vector point2, boolean flag);
    public abstract MovingObjectPositionHandle rayTrace2(Vector point1, Vector point2);
    public abstract void applyBlockPhysics(IntVector3 position, BlockData causeType);
    public abstract boolean isChunkLoaded(int cx, int cz, boolean flag);

    public void applyPhysics(IntVector3 position, BlockData causeType, boolean self) {
        if (T.opt_applyPhysics.isAvailable()) {
            T.opt_applyPhysics.invoke(getRaw(), position, causeType, self);
        } else if (T.opt_applyPhysics_old.isAvailable()) {
            T.opt_applyPhysics_old.invoke(getRaw(), position, causeType);
        } else {
            throw new UnsupportedOperationException("Apply physics function not available on this server");
        }
        if (self) {
            applyBlockPhysics(position, causeType);
        }
    }


    public org.bukkit.World toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toWorld.convert(getRaw());
    }

    public static WorldHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }
    public abstract List<EntityHandle> getEntityList();
    public abstract void setEntityList(List<EntityHandle> value);
    public abstract Collection<EntityHandle> getEntityRemoveQueue();
    public abstract void setEntityRemoveQueue(Collection<EntityHandle> value);
    public abstract List<EntityHumanHandle> getPlayers();
    public abstract void setPlayers(List<EntityHumanHandle> value);
    public abstract Random getRandom();
    public abstract void setRandom(Random value);
    public abstract WorldProviderHandle getWorldProvider();
    public abstract void setWorldProvider(WorldProviderHandle value);
    public abstract List<IWorldAccessHandle> getAccessList();
    public abstract void setAccessList(List<IWorldAccessHandle> value);
    public abstract MethodProfilerHandle getMethodProfiler();
    public abstract void setMethodProfiler(MethodProfilerHandle value);
    public abstract World getBukkitWorld();
    public abstract void setBukkitWorld(World value);
    public abstract boolean isKeepSpawnInMemory();
    public abstract void setKeepSpawnInMemory(boolean value);
    /**
     * Stores class members for <b>net.minecraft.server.World</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldClass extends Template.Class<WorldHandle> {
        public final Template.Field.Converted<List<EntityHandle>> entityList = new Template.Field.Converted<List<EntityHandle>>();
        public final Template.Field.Converted<Collection<EntityHandle>> entityRemoveQueue = new Template.Field.Converted<Collection<EntityHandle>>();
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
        public final Template.Method.Converted<Boolean> getBlockCollisions2 = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<VoxelShapeHandle> getCollisionShape = new Template.Method.Converted<VoxelShapeHandle>();
        public final Template.Method.Converted<List<AxisAlignedBBHandle>> getCubes = new Template.Method.Converted<List<AxisAlignedBBHandle>>();
        public final Template.Method.Converted<List<?>> getRawEntitiesOfType = new Template.Method.Converted<List<?>>();
        public final Template.Method.Converted<List<EntityHandle>> getEntities = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<TileEntityHandle> getTileEntity = new Template.Method.Converted<TileEntityHandle>();
        public final Template.Method.Converted<WorldDataHandle> getWorldData = new Template.Method.Converted<WorldDataHandle>();
        @Template.Optional
        public final Template.Method<Void> makeSound = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Void> removeEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> addEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Entity> getEntityById = new Template.Method.Converted<Entity>();
        public final Template.Method.Converted<IDataManagerHandle> getDataManager = new Template.Method.Converted<IDataManagerHandle>();
        public final Template.Method.Converted<Float> getExplosionFactor = new Template.Method.Converted<Float>();
        public final Template.Method.Converted<Boolean> areChunksLoaded = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<MovingObjectPositionHandle> rayTrace = new Template.Method.Converted<MovingObjectPositionHandle>();
        public final Template.Method.Converted<MovingObjectPositionHandle> rayTrace2 = new Template.Method.Converted<MovingObjectPositionHandle>();
        @Template.Optional
        public final Template.Method.Converted<Void> opt_applyPhysics = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> opt_applyPhysics_old = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> applyBlockPhysics = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isChunkLoaded = new Template.Method<Boolean>();

    }

}

