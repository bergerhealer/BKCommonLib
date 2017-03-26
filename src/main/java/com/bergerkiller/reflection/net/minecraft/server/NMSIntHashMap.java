package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;
import com.bergerkiller.reflection.SafeConstructor;

public class NMSIntHashMap {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("IntHashMap");
    public static final SafeConstructor<?> constructor = T.getConstructor();
    public static final FieldAccessor<Object[]> entries = T.selectField("private transient IntHashMapEntry<V>[] a");
    public static final MethodAccessor<Object> get = T.selectMethod("public V get(int paramInt)");
    public static final MethodAccessor<Object> getEntry = T.selectMethod("final IntHashMapEntry<V> c(int paramInt)");
    public static final MethodAccessor<Object> remove = T.selectMethod("public V d(int paramInt)");
    public static final MethodAccessor<Void> put = T.selectMethod("public void a(int paramInt, V paramV)");
    public static final MethodAccessor<Boolean> contains = T.selectMethod("public boolean b(int paramInt)");
    public static final MethodAccessor<Void> clear = T.selectMethod("public void c()");

    public static class Entry {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("IntHashMap.IntHashMapEntry");
        public static final FieldAccessor<Integer> key = T.selectField("final int a");
        public static final FieldAccessor<Object> value = T.selectField("V b");
    }
}
