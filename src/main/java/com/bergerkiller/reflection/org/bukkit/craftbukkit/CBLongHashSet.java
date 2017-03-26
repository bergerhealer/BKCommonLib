package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.SafeConstructor;

import java.util.Iterator;

public class CBLongHashSet {
    public static final ClassTemplate<?> T = ClassTemplate.createCB("util.LongHashSet");
    public static final SafeConstructor<?> constructor1 = T.getConstructor();
    public static final SafeConstructor<?> constructor2 = T.getConstructor(int.class);
    public static final MethodAccessor<Boolean> add2 = T.getMethod("add", int.class, int.class);
    public static final MethodAccessor<Boolean> add1 = T.getMethod("add", long.class);
    public static final MethodAccessor<Boolean> contains2 = T.getMethod("contains", int.class, int.class);
    public static final MethodAccessor<Boolean> contains1 = T.getMethod("contains", long.class);
    public static final MethodAccessor<Void> remove2 = T.getMethod("remove", int.class, int.class);
    public static final MethodAccessor<Boolean> remove1 = T.getMethod("remove", long.class);
    public static final MethodAccessor<Void> clear = T.getMethod("clear");
    public static final MethodAccessor<long[]> toArray = T.getMethod("toArray");
    public static final MethodAccessor<Long> popFirst = T.getMethod("popFirst");
    public static final MethodAccessor<long[]> popAll = T.getMethod("popAll");
    public static final MethodAccessor<Integer> hash = T.getMethod("hash", long.class);
    public static final MethodAccessor<Void> rehash0 = T.getMethod("rehash");
    public static final MethodAccessor<Void> rehash1 = T.getMethod("rehash", int.class);
    public static final MethodAccessor<Boolean> isEmpty = T.getMethod("isEmpty");
    public static final MethodAccessor<Integer> size = T.getMethod("size");
    public static final MethodAccessor<Iterator<Long>> iterator = T.getMethod("iterator");
    public static final FieldAccessor<long[]> values = T.getField("values");
    public static final FieldAccessor<Integer> freeEntries = T.getField("freeEntries");
    public static final FieldAccessor<Integer> elements = T.getField("elements");
    public static final FieldAccessor<Integer> modCount = T.getField("modCount");
    public static final long FREE = T.<Long>getStaticFieldValue("FREE", long.class).longValue();
    public static final long REMOVED = T.<Long>getStaticFieldValue("REMOVED", long.class).longValue();

}
