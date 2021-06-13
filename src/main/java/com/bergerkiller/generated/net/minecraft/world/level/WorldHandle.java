package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.MovingObjectPositionHandle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import java.util.List;
import java.util.Random;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.World</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.World")
public abstract class WorldHandle extends IBlockAccessHandle {
    /** @See {@link WorldClass} */
    public static final WorldClass T = Template.Class.create(WorldClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static WorldHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void method_profiler_begin(String label);
    public abstract void method_profiler_end();
    public abstract World getWorld();
    public abstract Server getServer();
    public abstract BlockData getBlockData(IntVector3 blockposition);
    public abstract BlockData getBlockDataAtCoord(int x, int y, int z);
    public abstract boolean setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags);
    public abstract long getTime();
    public abstract DimensionType getDimensionType();
    public abstract ResourceKey<DimensionType> getDimensionTypeKey();
    public abstract boolean isWithinWorldBorder(EntityHandle entity);
    public abstract boolean isNotCollidingWithBlocks(EntityHandle entity, AxisAlignedBBHandle axisalignedbb);
    public abstract List<?> getRawEntitiesOfType(Class<?> rawType, AxisAlignedBBHandle bounds);
    public abstract List<EntityHandle> getNearbyEntities(EntityHandle entity, AxisAlignedBBHandle axisalignedbb);
    public abstract TileEntityHandle getTileEntity(IntVector3 blockposition);
    public abstract boolean isBurnArea(AxisAlignedBBHandle bounds);
    public abstract Entity getEntityById(int entityId);
    public abstract boolean areChunksLoaded(IntVector3 blockposition, int distance);
    public abstract MovingObjectPositionHandle rayTrace(Vector point1, Vector point2);
    public abstract void applyBlockPhysics(IntVector3 position, BlockData causeType);

    public static final int UPDATE_PHYSICS = 0x1; // flag specifying block physics should occur after the change
    public static final int UPDATE_NOTIFY = 0x2; // flag specifying the change should be updated to players
    public static final int UPDATE_DEFAULT = (UPDATE_PHYSICS | UPDATE_NOTIFY); // default flags used when updating block types


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
    public abstract Random getRandom();
    public abstract void setRandom(Random value);
    public abstract World getBukkitWorld();
    public abstract void setBukkitWorld(World value);
    public abstract boolean isKeepSpawnInMemory();
    public abstract void setKeepSpawnInMemory(boolean value);
    /**
     * Stores class members for <b>net.minecraft.world.level.World</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldClass extends Template.Class<WorldHandle> {
        public final Template.Field<Random> random = new Template.Field<Random>();
        @Template.Optional
        public final Template.Field.Converted<Object> field_chunkProvider = new Template.Field.Converted<Object>();
        public final Template.Field.Converted<World> bukkitWorld = new Template.Field.Converted<World>();
        public final Template.Field.Boolean keepSpawnInMemory = new Template.Field.Boolean();

        public final Template.Method<Void> method_profiler_begin = new Template.Method<Void>();
        public final Template.Method<Void> method_profiler_end = new Template.Method<Void>();
        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted<World>();
        public final Template.Method.Converted<Server> getServer = new Template.Method.Converted<Server>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method<BlockData> getBlockDataAtCoord = new Template.Method<BlockData>();
        public final Template.Method.Converted<Boolean> setBlockData = new Template.Method.Converted<Boolean>();
        public final Template.Method<Long> getTime = new Template.Method<Long>();
        @Template.Optional
        public final Template.Method<Object> getChunkProvider = new Template.Method<Object>();
        public final Template.Method.Converted<DimensionType> getDimensionType = new Template.Method.Converted<DimensionType>();
        public final Template.Method.Converted<ResourceKey<DimensionType>> getDimensionTypeKey = new Template.Method.Converted<ResourceKey<DimensionType>>();
        public final Template.Method.Converted<Boolean> isWithinWorldBorder = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> getBlockCollisions = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> isNotCollidingWithBlocks = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<List<AxisAlignedBBHandle>> opt_getCubes_1_8 = new Template.Method.Converted<List<AxisAlignedBBHandle>>();
        public final Template.Method.Converted<List<?>> getRawEntitiesOfType = new Template.Method.Converted<List<?>>();
        public final Template.Method.Converted<List<EntityHandle>> getNearbyEntities = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<TileEntityHandle> getTileEntity = new Template.Method.Converted<TileEntityHandle>();
        @Template.Optional
        public final Template.Method<Void> makeSound = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> isBurnArea = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Entity> getEntityById = new Template.Method.Converted<Entity>();
        public final Template.Method.Converted<Boolean> areChunksLoaded = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<MovingObjectPositionHandle> rayTrace = new Template.Method.Converted<MovingObjectPositionHandle>();
        @Template.Optional
        public final Template.Method.Converted<Void> opt_applyPhysics = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method.Converted<Void> opt_applyPhysics_old = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> applyBlockPhysics = new Template.Method.Converted<Void>();

    }

}

