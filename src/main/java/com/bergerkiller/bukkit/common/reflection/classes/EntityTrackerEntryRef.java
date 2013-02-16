package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_R1.EntityTrackerEntry;
import net.minecraft.server.v1_4_R1.Packet;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.accessors.EntityFieldAccessor;

public class EntityTrackerEntryRef {
	public static final ClassTemplate<EntityTrackerEntry> TEMPLATE = ClassTemplate.create(EntityTrackerEntry.class);
	public static final MethodAccessor<Packet> getSpawnPacket = TEMPLATE.getMethod("b");
	public static final EntityFieldAccessor tracker = new EntityFieldAccessor(TEMPLATE.getField("tracker"));
	public static final EntityFieldAccessor vehicle = new EntityFieldAccessor(TEMPLATE.getField("v"));
	public static final FieldAccessor<Boolean> synched = TEMPLATE.getField("s");
	public static final FieldAccessor<Double> prevX = TEMPLATE.getField("p");
	public static final FieldAccessor<Double> prevY = TEMPLATE.getField("q");
	public static final FieldAccessor<Double> prevZ = TEMPLATE.getField("r");
}
