package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Queue;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class NetworkManagerRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("NetworkManager");
	public static final FieldAccessor<Queue<Object>> lowPriorityQueue = TEMPLATE.getField("i");
	public static final FieldAccessor<Queue<Object>> highPriorityQueue = TEMPLATE.getField("j");
}
