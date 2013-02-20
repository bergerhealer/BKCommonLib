package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class WorldTypeRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("WorldType");
	public static final FieldAccessor<String> name = TEMPLATE.getField("name");
	public static final MethodAccessor<Object> getType = TEMPLATE.getMethod("getType", String.class);
}
