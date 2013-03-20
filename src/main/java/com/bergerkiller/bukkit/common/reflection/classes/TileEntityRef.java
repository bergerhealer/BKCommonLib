package com.bergerkiller.bukkit.common.reflection.classes;

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

public class TileEntityRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("TileEntity");
	public static final TranslatorFieldAccessor<World> world = TEMPLATE.getField("world").translate(ConversionPairs.world);
	public static final FieldAccessor<Integer> x = TEMPLATE.getField("x");
	public static final FieldAccessor<Integer> y = TEMPLATE.getField("y");
	public static final FieldAccessor<Integer> z = TEMPLATE.getField("z");
	private static final MethodAccessor<Object> getUpdatePacket = TEMPLATE.getMethod("getUpdatePacket");

	public static Object get(Block block) {
		return get(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public static Object get(World world, int x, int y, int z) {
		return CommonNMS.getNative(world).getTileEntity(x, y, z);
	}

	public static CommonPacket getUpdatePacket(Object tileEntity) {
		return Conversion.toCommonPacket.convert(getUpdatePacket.invoke(tileEntity));
	}
}
