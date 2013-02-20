package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class TileEntityRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("TileEntity");
	public static final TranslatorFieldAccessor<World> world = TEMPLATE.getField("world").translate(ConversionPairs.world);
	public static final FieldAccessor<Integer> x = TEMPLATE.getField("x");
	public static final FieldAccessor<Integer> y = TEMPLATE.getField("y");
	public static final FieldAccessor<Integer> z = TEMPLATE.getField("z");
}
