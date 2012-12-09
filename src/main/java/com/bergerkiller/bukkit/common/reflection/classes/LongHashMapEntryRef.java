package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;

public class LongHashMapEntryRef {
	public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(Common.NMS_ROOT + ".LongHashMapEntry");
	public static final FieldAccessor<Object> entryValue = TEMPLATE.getField("b");
}
