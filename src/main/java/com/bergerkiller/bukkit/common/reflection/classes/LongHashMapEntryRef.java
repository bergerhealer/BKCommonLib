package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class LongHashMapEntryRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("LongHashMapEntry");
    public static final FieldAccessor<Long> entryKey = TEMPLATE.getField("a");
    public static final FieldAccessor<Object> entryValue = TEMPLATE.getField("b");
}
