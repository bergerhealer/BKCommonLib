package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.TileEntity;
import org.bukkit.World;
import org.bukkit.block.Block;

public class NMSTileEntity {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("TileEntity");
    public static final TranslatorFieldAccessor<World> world = T.selectField("protected World world").translate(ConversionPairs.world);
    public static final TranslatorFieldAccessor<IntVector3> position = T.selectField("protected BlockPosition position").translate(ConversionPairs.blockPosition);
    private static final MethodAccessor<Object> getUpdatePacket = T.selectMethod("public PacketPlayOutTileEntityData getUpdatePacket()");

    public static final MethodAccessor<Void> load = T.selectMethod("public void a(NBTTagCompound nbttagcompound)");
    public static final MethodAccessor<Void> save = T.selectMethod("public NBTTagCompound save(NBTTagCompound nbttagcompound)");

    public static boolean hasWorld(Object tileEntity) {
        return ((TileEntity) tileEntity).getWorld() != null;
    }

    public static Object getFromWorld(Block block) {
        return getFromWorld(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    public static Object getFromWorld(World world, Object blockPosition) {
    	return CommonNMS.getNative(world).getTileEntity((BlockPosition) blockPosition);
    }

    public static Object getFromWorld(World world, int x, int y, int z) {
    	return getFromWorld(world, new BlockPosition(x, y, z));
    }

    public static CommonPacket getUpdatePacket(Object tileEntity) {
        if (tileEntity == null) {
            return null;
        }
        return Conversion.toCommonPacket.convert(getUpdatePacket.invoke(tileEntity));
    }

    public static Block getBlock(Object tileEntity) {
        TileEntity tile = (TileEntity) tileEntity;
        return tile.getWorld().getWorld().getBlockAt(tile.getPosition().getX(), tile.getPosition().getY(), tile.getPosition().getZ());
    }
}
