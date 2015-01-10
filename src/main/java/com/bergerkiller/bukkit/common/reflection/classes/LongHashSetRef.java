package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.Iterator;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class LongHashSetRef {

    public static final ClassTemplate<?> TEMPLATE = ClassTemplate.create(CommonUtil.getCBClass("util.LongHashSet"));
    public static final SafeConstructor<?> constructor1;
    public static final SafeConstructor<?> constructor2;
    public static final MethodAccessor<Boolean> add2 = TEMPLATE.getMethod("add", int.class, int.class);
    public static final MethodAccessor<Boolean> add1 = TEMPLATE.getMethod("add", long.class);
    public static final MethodAccessor<Boolean> contains2 = TEMPLATE.getMethod("contains", int.class, int.class);
    public static final MethodAccessor<Boolean> contains1 = TEMPLATE.getMethod("contains", long.class);
    public static final MethodAccessor<Void> remove2 = TEMPLATE.getMethod("remove", int.class, int.class);
    public static final MethodAccessor<Boolean> remove1 = TEMPLATE.getMethod("remove", long.class);
    public static final MethodAccessor<Void> clear = TEMPLATE.getMethod("clear");
    public static final MethodAccessor<long[]> toArray = TEMPLATE.getMethod("toArray");
    public static final MethodAccessor<Long> popFirst = TEMPLATE.getMethod("popFirst");
    public static final MethodAccessor<long[]> popAll = TEMPLATE.getMethod("popAll");
    public static final MethodAccessor<Integer> hash = TEMPLATE.getMethod("hash", long.class);
    public static final MethodAccessor<Void> rehash0 = TEMPLATE.getMethod("rehash");
    public static final MethodAccessor<Void> rehash1 = TEMPLATE.getMethod("rehash", int.class);
    public static final MethodAccessor<Boolean> isEmpty = TEMPLATE.getMethod("isEmpty");
    public static final MethodAccessor<Integer> size = TEMPLATE.getMethod("size");
    public static final MethodAccessor<Iterator<Long>> iterator = TEMPLATE.getMethod("iterator");
    public static final FieldAccessor<long[]> values = TEMPLATE.getField("values");
    public static final FieldAccessor<Integer> freeEntries = TEMPLATE.getField("freeEntries");
    public static final FieldAccessor<Integer> elements = TEMPLATE.getField("elements");
    public static final FieldAccessor<Integer> modCount = TEMPLATE.getField("modCount");
    public static final long FREE = TEMPLATE.<Long>getStaticFieldValue("FREE").longValue();
    public static final long REMOVED = TEMPLATE.<Long>getStaticFieldValue("REMOVED").longValue();

    static {
        /*
         if (Common.IS_SPIGOT_SERVER) {
         // Load the Spigot dummy flatmap in advance
         SpigotDummyFlatMap.getInstance();
         // Undo the 'FlatMap' change by Spigot - it is inefficient for our use cases
         constructor1 = constructor2 = new SafeConstructor<Object>(null) {
         private final FieldAccessor<Object> flat = TEMPLATE.getField("flat");

         @Override
         public boolean isValid() {
         return true;
         }

         @Override
         public Object newInstance(Object... parameters) {
         Object instance = TEMPLATE.newInstanceNull();
         final int size = parameters.length == 1 ? Math.max((Integer) parameters[0], 1) : 3;
         // Set the initial field values
         values.set(instance, new long[size]);
         elements.set(instance, 0);
         freeEntries.set(instance, size);
         modCount.set(instance, 0);
         flat.set(instance, SpigotDummyFlatMap.getInstance());
         // All done!
         return instance;
         }
         };
         } else {
         */
        constructor1 = TEMPLATE.getConstructor();
        constructor2 = TEMPLATE.getConstructor(int.class);
        //}
    }
}
