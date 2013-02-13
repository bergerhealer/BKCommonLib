package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class DataWatcherRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("DataWatcher");
	public static final MethodAccessor<Void> write = TEMPLATE.getMethod("a", int.class, Object.class);

	public static void write(Object datawatcher, int index, Object value) {
		write.invoke(datawatcher, index, value);
	}

	public static Object create() {
		return TEMPLATE.newInstance();
	}
}
