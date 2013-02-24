package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;

public class IntHashMapRef {
	public static final ClassTemplate<?> TEMPLATE = new NMSClassTemplate("IntHashMap");
	public static final SafeConstructor<?> constructor = TEMPLATE.getConstructor();
	public static final MethodAccessor<Object> get = TEMPLATE.getMethod("get", int.class);
	public static final MethodAccessor<Object> remove = TEMPLATE.getMethod("d", int.class);
	public static final MethodAccessor<Void> put = TEMPLATE.getMethod("a", int.class, Object.class);
	public static final MethodAccessor<Boolean> contains = TEMPLATE.getMethod("b", int.class);
	public static final MethodAccessor<Object> clear = TEMPLATE.getMethod("c");
}
