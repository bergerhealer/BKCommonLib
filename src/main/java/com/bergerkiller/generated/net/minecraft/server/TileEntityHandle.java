package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.TileEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class TileEntityHandle extends Template.Handle {
    /** @See {@link TileEntityClass} */
    public static final TileEntityClass T = new TileEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(TileEntityHandle.class, "net.minecraft.server.TileEntity", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static TileEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract WorldHandle getWorld();
    public abstract BlockPositionHandle getPosition();
    public abstract BlockData getBlockDataIfCached();
    public abstract BlockData getBlockData();
    public abstract Material getType();
    public abstract CommonPacket getUpdatePacket();
    public abstract void load(CommonTagCompound nbttagcompound);
    public abstract void save(CommonTagCompound nbttagcompound);

    public org.bukkit.block.BlockState toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toBlockState.convert(getRaw());
    }

    public static TileEntityHandle fromBukkit(org.bukkit.block.BlockState blockState) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toTileEntityHandle.convert(blockState));
    }
    public abstract World getWorld_field();
    public abstract void setWorld_field(World value);
    public abstract IntVector3 getPosition_field();
    public abstract void setPosition_field(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.server.TileEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TileEntityClass extends Template.Class<TileEntityHandle> {
        public final Template.Field.Converted<World> world_field = new Template.Field.Converted<World>();
        public final Template.Field.Converted<IntVector3> position_field = new Template.Field.Converted<IntVector3>();

        public final Template.Method.Converted<WorldHandle> getWorld = new Template.Method.Converted<WorldHandle>();
        public final Template.Method.Converted<BlockPositionHandle> getPosition = new Template.Method.Converted<BlockPositionHandle>();
        public final Template.Method.Converted<BlockData> getBlockDataIfCached = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Material> getType = new Template.Method.Converted<Material>();
        @Template.Optional
        public final Template.Method<Integer> getLegacyData = new Template.Method<Integer>();
        public final Template.Method.Converted<CommonPacket> getUpdatePacket = new Template.Method.Converted<CommonPacket>();
        public final Template.Method.Converted<Void> load = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> save = new Template.Method.Converted<Void>();

    }

}

