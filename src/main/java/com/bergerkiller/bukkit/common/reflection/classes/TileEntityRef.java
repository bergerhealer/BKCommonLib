package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_8_R1.TileEntity;

import org.bukkit.World;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;
import net.minecraft.server.v1_8_R1.BlockPosition;

public class TileEntityRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("TileEntity");
    public static final TranslatorFieldAccessor<World> world = TEMPLATE.getField("world").translate(ConversionPairs.world);
    public static final FieldAccessor<Integer> x = TEMPLATE.getField("x");
    public static final FieldAccessor<Integer> y = TEMPLATE.getField("y");
    public static final FieldAccessor<Integer> z = TEMPLATE.getField("z");
    private static final MethodAccessor<Object> getUpdatePacket = TEMPLATE.getMethod("getUpdatePacket");

    public static boolean hasWorld(Object tileEntity) {
        return ((TileEntity) tileEntity).getWorld() != null;
    }

    public static Object getFromWorld(Block block) {
        return getFromWorld(block.getWorld(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
    }

    public static Object getFromWorld(World world, BlockPosition blockposition) {
        return CommonNMS.getNative(world).getTileEntity(blockposition);
    }

    public static CommonPacket getUpdatePacket(Object tileEntity) {
        return Conversion.toCommonPacket.convert(getUpdatePacket.invoke(tileEntity));
    }

    public static Block getBlock(Object tileEntity) {
        TileEntity tile = (TileEntity) tileEntity;
        return tile.getWorld().getWorld().getBlockAt(tile.getPosition().getX(), tile.getPosition().getY(), tile.getPosition().getZ());
    }
}
