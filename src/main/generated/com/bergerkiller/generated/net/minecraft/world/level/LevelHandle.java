package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.HitResultHandle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.Level</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.Level")
public abstract class LevelHandle extends BlockGetterHandle {
    /** @see LevelClass */
    public static final LevelClass T = Template.Class.create(LevelClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static LevelHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setKeepSpawnInMemoryDuringInit(boolean loaded);
    public abstract void method_profiler_begin(String label);
    public abstract void method_profiler_end();
    public abstract World getWorld();
    public abstract Server getServer();
    public abstract BlockData getBlockData(IntVector3 blockposition);
    public abstract BlockData getBlockDataAtCoord(int x, int y, int z);
    public abstract boolean setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags);
    public abstract long getTime();
    public abstract DimensionType getDimensionType();
    public abstract boolean isWithinWorldBorder(EntityHandle entity);
    public abstract boolean isNotCollidingWithBlocks(EntityHandle entity, AABBHandle axisalignedbb);
    public abstract List<?> getRawEntitiesOfType(Class<?> rawType, AABBHandle bounds);
    public abstract List<EntityHandle> getNearbyEntities(EntityHandle entity, AABBHandle axisalignedbb);
    public abstract BlockEntityHandle getTileEntity(IntVector3 blockposition);
    public abstract boolean isBurnArea(AABBHandle bounds);
    public abstract Entity getEntityById(int entityId);
    public abstract boolean areChunksLoaded(IntVector3 blockposition, int distance);
    public abstract HitResultHandle rayTrace(Vector point1, Vector point2);
    public abstract void applyBlockPhysicsAround(IntVector3 position, BlockData causeType);
    public abstract void applyBlockPhysics(IntVector3 position, BlockData causeType);
    public abstract int getMinBuildHeight();
    public abstract int getMaxBuildHeight();
    public abstract int getNetherPortalSearchRadius();
    public abstract int getNetherPortalCreateRadius();
    public static final int UPDATE_PHYSICS = 0x1; // flag specifying block physics should occur after the change
    public static final int UPDATE_NOTIFY = 0x2; // flag specifying the change should be updated to players
    public static final int UPDATE_DEFAULT = (UPDATE_PHYSICS | UPDATE_NOTIFY); // default flags used when updating block types

    public void applyBlockPhysics(IntVector3 position, BlockData causeType, boolean self) {
        applyBlockPhysicsAround(position, causeType);
        if (self) {
            applyBlockPhysics(position, causeType);
        }
    }

    public org.bukkit.World toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toWorld.convert(getRaw());
    }

    public static LevelHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }
    public abstract RandomSourceHandle getRandom();
    public abstract void setRandom(RandomSourceHandle value);
    public abstract World getBukkitWorld();
    public abstract void setBukkitWorld(World value);
    /**
     * Stores class members for <b>net.minecraft.world.level.Level</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class LevelClass extends Template.Class<LevelHandle> {
        public final Template.Field.Converted<RandomSourceHandle> random = new Template.Field.Converted<RandomSourceHandle>();
        @Template.Optional
        public final Template.Field.Converted<Object> field_chunkProvider = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<World> bukkitWorld = new Template.Field.Converted<World>();

        public final Template.Method<Void> setKeepSpawnInMemoryDuringInit = new Template.Method<Void>();
        public final Template.Method<Void> method_profiler_begin = new Template.Method<Void>();
        public final Template.Method<Void> method_profiler_end = new Template.Method<Void>();
        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted<World>();
        public final Template.Method.Converted<Server> getServer = new Template.Method.Converted<Server>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method<BlockData> getBlockDataAtCoord = new Template.Method<BlockData>();
        @Template.Optional
        public final Template.Method<Object> getChunkProvider = new Template.Method<Object>();
        public final Template.Method.Converted<Boolean> setBlockData = new Template.Method.Converted<Boolean>();
        public final Template.Method<Long> getTime = new Template.Method<Long>();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted<DimensionType>();
        public final Template.Method.Converted<Boolean> isWithinWorldBorder = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> isNotCollidingWithBlocks = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<List<AABBHandle>> opt_getCubes_1_8 = new Template.Method.Converted<List<AABBHandle>>();
        public final Template.Method.Converted<List<?>> getRawEntitiesOfType = new Template.Method.Converted<List<?>>();
        public final Template.Method.Converted<List<EntityHandle>> getNearbyEntities = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<BlockEntityHandle> getTileEntity = new Template.Method.Converted<BlockEntityHandle>();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Entity> getEntityById = new Template.Method.Converted<Entity>();
        public final Template.Method.Converted<Boolean> areChunksLoaded = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<HitResultHandle> rayTrace = new Template.Method.Converted<HitResultHandle>();
        public final Template.Method.Converted<Void> applyBlockPhysicsAround = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> applyBlockPhysics = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getMinBuildHeight = new Template.Method<Integer>();
        public final Template.Method<Integer> getMaxBuildHeight = new Template.Method<Integer>();
        public final Template.Method<Integer> getNetherPortalSearchRadius = new Template.Method<Integer>();
        public final Template.Method<Integer> getNetherPortalCreateRadius = new Template.Method<Integer>();

    }

}

