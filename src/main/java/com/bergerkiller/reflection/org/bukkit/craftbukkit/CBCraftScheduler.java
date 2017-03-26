package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

import java.util.PriorityQueue;

public class CBCraftScheduler {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("scheduler.CraftScheduler");
    public static final FieldAccessor<PriorityQueue<?>> pending = T.selectField("private final PriorityQueue<CraftTask> pending");
}
