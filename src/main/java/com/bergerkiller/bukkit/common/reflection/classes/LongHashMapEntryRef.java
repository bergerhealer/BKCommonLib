package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import net.minecraft.server.v1_9_R1.LongHashMap;

public class LongHashMapEntryRef {

    public static ClassTemplate<?> TEMPLATE = null;
    public static FieldAccessor<Long> entryKey = null;
    public static FieldAccessor<Object> entryValue = null;

    static {
        Class[] possible = LongHashMap.class.getDeclaredClasses();
        Class qp = null;
        for (Class p : possible) {
            if (p.getName().endsWith("LongHashMapEntry")) {
                qp = p;
            }
        }
        TEMPLATE = ClassTemplate.create(qp);
        entryKey = TEMPLATE.getField("a");
        TEMPLATE.getField("b");
    }
}
