package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.PriorityQueue;

import org.bukkit.craftbukkit.v1_4_R1.scheduler.CraftScheduler;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class CraftSchedulerRef {
	public static final FieldAccessor<PriorityQueue<?>> pending = new SafeField<PriorityQueue<?>>(CraftScheduler.class, "pending");
}
