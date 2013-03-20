package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
	public static final FieldAccessor<Integer> viewDistance = TEMPLATE.getField("b");
	public static final FieldAccessor<Integer> updateInterval = TEMPLATE.getField("c");
	public static final FieldAccessor<Integer> timeSinceLocationSync = TEMPLATE.getField("u");
	public static final FieldAccessor<Boolean> isMobile = TEMPLATE.getField("isMoving");
	public static final TranslatorFieldAccessor<List<Player>> viewers = TEMPLATE.getField("trackedPlayers").translate(ConversionPairs.playerList);
	private static final MethodAccessor<Object> getSpawnPacket = TEMPLATE.getMethod("b");

	public static final CommonPacket getSpawnPacket(Object instance) {
		return new CommonPacket(getSpawnPacket.invoke(instance));
	}
}
