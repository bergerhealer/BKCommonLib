package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.Packet;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;

public class EntityTrackerEntryRef {
	public static final ClassTemplate<EntityTrackerEntry> TEMPLATE = ClassTemplate.create(EntityTrackerEntry.class);
	private static final SafeMethod getSpawnPacketMethod = TEMPLATE.getMethod("b");
	public static final SafeField<Entity> vehicle = TEMPLATE.getField("v");
	public static final SafeField<Boolean> synched = TEMPLATE.getField("s");
	public static final SafeField<Double> prevX = TEMPLATE.getField("p");
	public static final SafeField<Double> prevY = TEMPLATE.getField("q");
	public static final SafeField<Double> prevZ = TEMPLATE.getField("r");

	public static Packet getSpawnPacket(EntityTrackerEntry instance) {
		return (Packet) getSpawnPacketMethod.invoke(instance);
	}
}
