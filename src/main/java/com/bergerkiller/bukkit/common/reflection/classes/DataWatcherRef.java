package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

@SuppressWarnings("rawtypes")
public class DataWatcherRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("DataWatcher");
	public static final MethodAccessor<Void> write = TEMPLATE.getMethod("a", int.class, Object.class);
	public static final MethodAccessor<Void> watch = TEMPLATE.getMethod("watch", int.class, Object.class);
	public static final MethodAccessor<List> returnAllWatched = TEMPLATE.getMethod("c");
	public static final MethodAccessor<List> unwatchAndReturnAllWatched = TEMPLATE.getMethod("b");

	public static void write(Object datawatcher, int index, Object value) {
		write.invoke(datawatcher, index, value);
	}
	
	public static void watch(Object datawatcher, int index, Object value) {
		watch.invoke(index, value);
	}
	
	public static List getAllWatched(Object datawatcher) {
		return returnAllWatched.invoke(datawatcher);
	}
	
	public static List unwatchAndGetAllWatched(Object datawatcher) {
		return unwatchAndReturnAllWatched.invoke(datawatcher);
	}

	public static Object create() {
		return TEMPLATE.newInstance();
	}
}
