package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
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
	public static final TranslatorFieldAccessor<Entity> vehicle = TEMPLATE.getField("w").translate(ConversionPairs.entity);
	public static final FieldAccessor<Boolean> synched = TEMPLATE.getField("isMoving");
	public static final FieldAccessor<Double> prevX = TEMPLATE.getField("q");
	public static final FieldAccessor<Double> prevY = TEMPLATE.getField("r");
	public static final FieldAccessor<Double> prevZ = TEMPLATE.getField("s");
	public static final FieldAccessor<Integer> viewDistance = TEMPLATE.getField("b");
	public static final FieldAccessor<Integer> updateInterval = TEMPLATE.getField("c");
	public static final FieldAccessor<Integer> timeSinceLocationSync = TEMPLATE.getField("v");
	public static final FieldAccessor<Boolean> isMobile = TEMPLATE.getField("u");
	public static final TranslatorFieldAccessor<Set<Player>> viewers = TEMPLATE.getField("trackedPlayers").translate(ConversionPairs.playerSet);
	private static final MethodAccessor<Object> getSpawnPacket = TEMPLATE.getMethod("c");
	private static final MethodAccessor<Void> scanPlayers = TEMPLATE.getMethod("scanPlayers", List.class);
	private static final MethodAccessor<Void> updatePlayer = TEMPLATE.getMethod("updatePlayer", EntityPlayerRef.TEMPLATE.getType());

	/*
	 * Note: isMoving is wrongly deobfuscated by CraftBukkit team!
	 */

	public static CommonPacket getSpawnPacket(Object instance) {
		return Conversion.toCommonPacket.convert(getSpawnPacket.invoke(instance));
	}

	public static void scanPlayers(Object instance, List<Player> players) {
		scanPlayers.invoke(instance, Conversion.toPlayerHandleList.convert(players));
	}

	public static void updatePlayer(Object instance, Player player) {
		updatePlayer.invoke(instance, Conversion.toEntityHandle.convert(player));
	}
}
