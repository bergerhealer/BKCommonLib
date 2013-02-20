package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class EntityTrackerEntryRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("EntityTrackerEntry");
	public static final TranslatorFieldAccessor<Entity> tracker = TEMPLATE.getField("tracker").translate(ConversionPairs.entity);
	public static final TranslatorFieldAccessor<Entity> vehicle = TEMPLATE.getField("v").translate(ConversionPairs.entity);
	public static final FieldAccessor<Boolean> synched = TEMPLATE.getField("s");
	public static final FieldAccessor<Double> prevX = TEMPLATE.getField("p");
	public static final FieldAccessor<Double> prevY = TEMPLATE.getField("q");
	public static final FieldAccessor<Double> prevZ = TEMPLATE.getField("r");
	private static final MethodAccessor<Object> getSpawnPacket = TEMPLATE.getMethod("b");

	public static final CommonPacket getSpawnPacket(Object instance) {
		return new CommonPacket(getSpawnPacket.invoke(instance));
	}
}
