package com.bergerkiller.bukkit.common.reflection;

import java.util.Set;

import net.minecraft.server.EntityTracker;
import net.minecraft.server.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.SafeField;

public class EntityTrackerRef {
	public static final SafeField<Set<EntityTrackerEntry>> trackerSet = new SafeField<Set<EntityTrackerEntry>>(EntityTracker.class, "b");
}
