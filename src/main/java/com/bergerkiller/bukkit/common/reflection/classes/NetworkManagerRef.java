package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class NetworkManagerRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("NetworkManager");
	public static final FieldAccessor<Integer> queueSize = TEMPLATE.getField("z");
	public static final FieldAccessor<Object> lockObject = TEMPLATE.getField("h");
	public static final FieldAccessor<List<Object>> lowPriorityQueue = TEMPLATE.getField("lowPriorityQueue");
	public static final FieldAccessor<List<Object>> highPriorityQueue = TEMPLATE.getField("highPriorityQueue");
}
