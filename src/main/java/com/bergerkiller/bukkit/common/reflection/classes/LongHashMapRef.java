package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.*;

public class LongHashMapRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("LongHashMap");
    public static final FieldAccessor<Object[]> entriesField = TEMPLATE.getField("entries");
    public static final FieldAccessor<Integer> countField = TEMPLATE.getField("count");
    public static final MethodAccessor<Boolean> contains = TEMPLATE.getMethod("contains", long.class);
    public static final MethodAccessor<Object> get = TEMPLATE.getMethod("getEntry", long.class);
    public static final MethodAccessor<Object> remove = TEMPLATE.getMethod("remove", long.class);
    public static final MethodAccessor<Void> put = TEMPLATE.getMethod("put", long.class, Object.class);
    public static final MethodAccessor<Void> setCapacity = TEMPLATE.getMethod("a", int.class);
    public static final SafeConstructor<?> constructor1 = TEMPLATE.getConstructor();
}
