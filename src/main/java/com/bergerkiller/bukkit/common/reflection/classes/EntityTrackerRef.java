package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Set;

import net.minecraft.server.v1_4_5.EntityTracker;
import net.minecraft.server.v1_4_5.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class EntityTrackerRef {
	public static final FieldAccessor<Set<EntityTrackerEntry>> trackerSet = new SafeField<Set<EntityTrackerEntry>>(EntityTracker.class, "b");
}
