package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_5.Entity;
import net.minecraft.server.v1_4_5.EntityTrackerEntry;
import net.minecraft.server.v1_4_5.Packet;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;

public class EntityTrackerEntryRef {
	public static final ClassTemplate<EntityTrackerEntry> TEMPLATE = ClassTemplate.create(EntityTrackerEntry.class);
	public static final MethodAccessor<Packet> getSpawnPacket = TEMPLATE.getMethod("b");
	public static final FieldAccessor<Entity> vehicle = TEMPLATE.getField("v");
	public static final FieldAccessor<Boolean> synched = TEMPLATE.getField("s");
	public static final FieldAccessor<Double> prevX = TEMPLATE.getField("p");
	public static final FieldAccessor<Double> prevY = TEMPLATE.getField("q");
	public static final FieldAccessor<Double> prevZ = TEMPLATE.getField("r");
}
