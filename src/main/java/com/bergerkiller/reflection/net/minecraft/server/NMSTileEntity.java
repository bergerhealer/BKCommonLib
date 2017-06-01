package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.server.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.TileEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.World;
import org.bukkit.block.Block;

public class NMSTileEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("TileEntity");
    public static final TranslatorFieldAccessor<World> world = T.selectField("protected World world").translate(DuplexConversion.world);
    public static final TranslatorFieldAccessor<IntVector3> position = T.selectField("protected BlockPosition position").translate(DuplexConversion.blockPosition);
    private static final MethodAccessor<Object> getUpdatePacket = T.selectMethod("public PacketPlayOutTileEntityData getUpdatePacket()");

    public static final MethodAccessor<Void> load = T.selectMethod("public void a(NBTTagCompound nbttagcompound)");
    public static final MethodAccessor<Void> save = T.selectMethod("public NBTTagCompound save(NBTTagCompound nbttagcompound)");

    public static boolean hasWorld(Object tileEntity) {
        return TileEntityHandle.T.getWorld.invoke(tileEntity) != null;
    }

    public static Object getFromWorld(Block block) {
        return getFromWorld(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static Object getFromWorld(World world, Object blockPosition) {
        return WorldHandle.T.getTileEntity.raw.invoke(HandleConversion.toWorldHandle(world), blockPosition);
    }

    public static Object getFromWorld(World world, int x, int y, int z) {
        return getFromWorld(world, BlockPositionHandle.createNew(x, y, z).getRaw());
    }

    public static CommonPacket getUpdatePacket(Object tileEntity) {
        if (tileEntity == null) {
            return null;
        }
        return Conversion.toCommonPacket.convert(getUpdatePacket.invoke(tileEntity));
    }

    public static Block getBlock(Object tileEntity) {
        TileEntityHandle handle = TileEntityHandle.createHandle(tileEntity);
        BlockPositionHandle pos = handle.getPosition();
        return handle.getWorld().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }
}
