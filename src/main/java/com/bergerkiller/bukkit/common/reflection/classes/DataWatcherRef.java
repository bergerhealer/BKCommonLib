package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_4_R1.DataWatcher;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;

public class DataWatcherRef {
	public static final ClassTemplate<DataWatcher> TEMPLATE = ClassTemplate.create(DataWatcher.class);
	public static final MethodAccessor<Void> write = TEMPLATE.getMethod("a", int.class, Object.class);
	
	public static void write(Object datawatcher, int index, Object value) {
		write.invoke(datawatcher, index, value);
	}
	
	public static Object create() {
		try {
			return DataWatcher.class.newInstance();
		} catch (Exception e) {
			return null;
		}
	}
}
