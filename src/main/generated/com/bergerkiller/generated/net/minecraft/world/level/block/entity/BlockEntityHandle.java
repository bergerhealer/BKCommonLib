package com.bergerkiller.generated.net.minecraft.world.level.block.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.block.entity.BlockEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.block.entity.BlockEntity")
public abstract class BlockEntityHandle extends Template.Handle {
    /** @see BlockEntityClass */
    public static final BlockEntityClass T = Template.Class.create(BlockEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract LevelHandle getWorld();
    public abstract BlockPosHandle getPosition();
    public abstract BlockData getBlockDataIfCached();
    public abstract void load(BlockData blockData, CommonTagCompound nbttagcompound);
    public abstract CommonTagCompound save();
    public abstract BlockData getBlockData();
    public abstract Object getRawBlockData();
    public abstract Material getType();
    public abstract CommonPacket getUpdatePacket();
    public abstract boolean isRemoved();
    @SuppressWarnings("deprecation")
    public org.bukkit.block.BlockState toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion.INSTANCE.tileEntityToBlockState(getRaw());
    }

    public static BlockEntityHandle fromBukkit(org.bukkit.block.BlockState blockState) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion.INSTANCE.blockStateToTileEntity(blockState));
    }
    public abstract World getWorld_field();
    public abstract void setWorld_field(World value);
    public abstract IntVector3 getPosition_field();
    public abstract void setPosition_field(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.world.level.block.entity.BlockEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockEntityClass extends Template.Class<BlockEntityHandle> {
        public final Template.Field.Converted<World> world_field = new Template.Field.Converted<World>();
        public final Template.Field.Converted<IntVector3> position_field = new Template.Field.Converted<IntVector3>();

        public final Template.Method.Converted<LevelHandle> getWorld = new Template.Method.Converted<LevelHandle>();
        public final Template.Method.Converted<BlockPosHandle> getPosition = new Template.Method.Converted<BlockPosHandle>();
        public final Template.Method.Converted<BlockData> getBlockDataIfCached = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> load = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<CommonTagCompound> save = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method<Object> getRawBlockData = new Template.Method<Object>();
        public final Template.Method.Converted<Material> getType = new Template.Method.Converted<Material>();
        @Template.Optional
        public final Template.Method<Integer> getLegacyData = new Template.Method<Integer>();
        public final Template.Method.Converted<CommonPacket> getUpdatePacket = new Template.Method.Converted<CommonPacket>();
        public final Template.Method<Boolean> isRemoved = new Template.Method<Boolean>();

    }

}

