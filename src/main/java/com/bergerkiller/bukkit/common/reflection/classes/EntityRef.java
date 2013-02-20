package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class EntityRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("Entity");
	public static final FieldAccessor<org.bukkit.entity.Entity> bukkitEntity = TEMPLATE.getField("bukkitEntity");
	public static final FieldAccessor<Integer> chunkX = TEMPLATE.getField("ai");
	public static final FieldAccessor<Integer> chunkY = TEMPLATE.getField("aj");
	public static final FieldAccessor<Integer> chunkZ = TEMPLATE.getField("ak");
	public static final FieldAccessor<Boolean> positionChanged = TEMPLATE.getField("am");
	public static final FieldAccessor<Double> locX = TEMPLATE.getField("locX");
	public static final FieldAccessor<Double> locY = TEMPLATE.getField("locY");
	public static final FieldAccessor<Double> locZ = TEMPLATE.getField("locZ");
	public static final FieldAccessor<Double> motX = TEMPLATE.getField("motX");
	public static final FieldAccessor<Double> motY = TEMPLATE.getField("motY");
	public static final FieldAccessor<Double> motZ = TEMPLATE.getField("motZ");
	public static final FieldAccessor<Float> yaw = TEMPLATE.getField("yaw");
	public static final FieldAccessor<Float> pitch = TEMPLATE.getField("pitch");
	public static final TranslatorFieldAccessor<World> world = TEMPLATE.getField("world").translate(ConversionPairs.world);
}
