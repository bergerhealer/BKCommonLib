package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.PriorityQueue;

import org.bukkit.craftbukkit.scheduler.CraftScheduler;

import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftSchedulerRef {
	public static final SafeField<PriorityQueue<?>> pending = new SafeField<PriorityQueue<?>>(CraftScheduler.class, "pending");
}
