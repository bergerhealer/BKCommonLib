package com.bergerkiller.bukkit.common.reflection;

import com.bergerkiller.bukkit.common.ClassTemplate;
import com.bergerkiller.bukkit.common.SafeField;

public class LongHashMapEntryRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create("net.minecraft.server.LongHashMapEntry");
	public static final SafeField<Object> entryValue = TEMPLATE.getField("b");
}
