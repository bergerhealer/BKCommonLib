package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;

public class ClassLoaderRef {
	public static final ClassTemplate<ClassLoader> TEMPLATE = ClassTemplate.create(ClassLoader.class);
	public static final MethodAccessor<Class<?>> loadClass = TEMPLATE.getMethod("loadClass", String.class, boolean.class);
}
