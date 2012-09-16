package com.bergerkiller.bukkit.common.reflection;

import java.util.Set;

import net.minecraft.server.EntityTracker;

import com.bergerkiller.bukkit.common.SafeField;

@SuppressWarnings("rawtypes")
public class EntityTrackerRef {
	public static final SafeField<Set> trackerSet = new SafeField<Set>(EntityTracker.class, "b");
}
