package com.bergerkiller.bukkit.common.reflection;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.Packet;

import com.bergerkiller.bukkit.common.ClassTemplate;
import com.bergerkiller.bukkit.common.SafeField;
import com.bergerkiller.bukkit.common.SafeMethod;

public class EntityTrackerEntryRef {
	public static final ClassTemplate<EntityTrackerEntry> TEMPLATE = ClassTemplate.create(EntityTrackerEntry.class);
	private static final SafeMethod getSpawnPacketMethod = new SafeMethod(EntityTrackerEntry.class, "b");
	public static final SafeField<Entity> vehicle = new SafeField<Entity>(EntityTrackerEntry.class, "v");
	public static final SafeField<Boolean> synched = new SafeField<Boolean>(EntityTrackerEntry.class, "s");

	public static Packet getSpawnPacket(EntityTrackerEntry instance) {
		return (Packet) getSpawnPacketMethod.invoke(instance);
	}
}
