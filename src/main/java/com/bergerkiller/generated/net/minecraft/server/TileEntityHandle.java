package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.TileEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class TileEntityHandle extends Template.Handle {
    /** @See {@link TileEntityClass} */
    public static final TileEntityClass T = new TileEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(TileEntityHandle.class, "net.minecraft.server.TileEntity");

    /* ============================================================================== */

    public static TileEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        TileEntityHandle handle = new TileEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public WorldHandle getWorld() {
        return T.getWorld.invoke(instance);
    }

    public BlockPositionHandle getPosition() {
        return T.getPosition.invoke(instance);
    }

    public int getRawData() {
        return T.getRawData.invoke(instance);
    }

    public Material getType() {
        return T.getType.invoke(instance);
    }

    public CommonPacket getUpdatePacket() {
        return T.getUpdatePacket.invoke(instance);
    }

    public void load(CommonTagCompound nbttagcompound) {
        T.load.invoke(instance, nbttagcompound);
    }

    public void save(CommonTagCompound nbttagcompound) {
        T.save.invoke(instance, nbttagcompound);
    }


    public org.bukkit.block.BlockState toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toBlockState.convert(getRaw());
    }

    public static TileEntityHandle fromBukkit(org.bukkit.block.BlockState blockState) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toTileEntityHandle.convert(blockState));
    }
    public World getWorld_field() {
        return T.world_field.get(instance);
    }

    public void setWorld_field(World value) {
        T.world_field.set(instance, value);
    }

    public IntVector3 getPosition_field() {
        return T.position_field.get(instance);
    }

    public void setPosition_field(IntVector3 value) {
        T.position_field.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.TileEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class TileEntityClass extends Template.Class<TileEntityHandle> {
        public final Template.Field.Converted<World> world_field = new Template.Field.Converted<World>();
        public final Template.Field.Converted<IntVector3> position_field = new Template.Field.Converted<IntVector3>();

        public final Template.Method.Converted<WorldHandle> getWorld = new Template.Method.Converted<WorldHandle>();
        public final Template.Method.Converted<BlockPositionHandle> getPosition = new Template.Method.Converted<BlockPositionHandle>();
        public final Template.Method<Integer> getRawData = new Template.Method<Integer>();
        public final Template.Method.Converted<Material> getType = new Template.Method.Converted<Material>();
        public final Template.Method.Converted<CommonPacket> getUpdatePacket = new Template.Method.Converted<CommonPacket>();
        public final Template.Method.Converted<Void> load = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> save = new Template.Method.Converted<Void>();

    }

}

