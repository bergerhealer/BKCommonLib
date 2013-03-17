package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.PriorityQueue;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class CraftSchedulerRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("scheduler.CraftScheduler"));
	public static final FieldAccessor<PriorityQueue<?>> pending = TEMPLATE.getField("pending");
}
