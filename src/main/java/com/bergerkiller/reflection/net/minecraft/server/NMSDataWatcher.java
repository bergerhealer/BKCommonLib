package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import java.util.List;

import org.bukkit.entity.Entity;

public class NMSDataWatcher {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("DataWatcher");

    public static final TranslatorFieldAccessor<Entity> owner = T.selectField("private final Entity c").translate(ConversionPairs.entity);

    public static final MethodAccessor<Void> watch = T.selectMethod("private <T> void registerObject(DataWatcherObject<T> datawatcherobject, Object t0)");

    /*
     *  } else if (this.d.containsKey(Integer.valueOf(i))) {
     *      throw new IllegalArgumentException("Duplicate id value for " + i + "!");
     #  } else if (DataWatcherRegistry.b(datawatcherobject.##METHODNAME##()) < 0) {
     *      throw new IllegalArgumentException("Unregistered serializer " + datawatcherobject.b() + " for " + i + "!");
     *  } else {
     */
    public static final MethodAccessor<List<Object>> unwatchAndReturnAllWatched = T.selectMethod("public List<DataWatcher.Item<?>> b()");
    
    /*
     * Same signature as unwatchAndReturnAllWatched, find other function that matches
     */
    public static final MethodAccessor<List<Object>> returnAllWatched = T.selectMethod("public List<DataWatcher.Item<?>> c()");

    public static final MethodAccessor<Object> read = T.selectMethod("private <T> DataWatcher.Item<T> c(DataWatcherObject<T> datawatcherobject)");

    public static final MethodAccessor<Object> get = T.selectMethod("public <T> T get(DataWatcherObject<T> datawatcherobject)");
    public static final MethodAccessor<Void> set = T.selectMethod("public <T> void set(DataWatcherObject<T> datawatcherobject, T t0)");
    
    public static final MethodAccessor<Boolean> isChanged = T.selectMethod("public boolean a()");
    public static final MethodAccessor<Boolean> isEmpty = T.selectMethod("public boolean d()");
    public static final SafeConstructor<?> constructor1 = T.getConstructor(NMSEntity.T.getType());

    /**
     * @deprecated Use com.bergerkiller.bukkit.common.wrappers.DataWatcher
     * instead
     */
    @Deprecated
    public static void watch(Object datawatcher, int index, Object value) {
        watch.invoke(index, value);
    }

    /**
     * @deprecated Use com.bergerkiller.bukkit.common.wrappers.DataWatcher
     * instead
     */
    @Deprecated
    public static List<Object> getAllWatched(Object datawatcher) {
        return returnAllWatched.invoke(datawatcher);
    }

    /**
     * @deprecated Use com.bergerkiller.bukkit.common.wrappers.DataWatcher
     * instead
     */
    @Deprecated
    public static List<Object> unwatchAndGetAllWatched(Object datawatcher) {
        return unwatchAndReturnAllWatched.invoke(datawatcher);
    }

    /**
     * @deprecated Use com.bergerkiller.bukkit.common.wrappers.DataWatcher
     * instead
     */
    @Deprecated
    public static Object create() {
        return T.newInstance();
    }

    public static class Object2 {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("DataWatcherObject");
        public static final MethodAccessor<Integer> getId = T.selectMethod("public int a()");
        public static final MethodAccessor<Object> getSerializer = T.selectMethod("public DataWatcherSerializer<T> b()");
    }

    public static class Registry {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("DataWatcherRegistry");
        public static final MethodAccessor<Integer> getSerializerId = T.selectMethod("public static int b(DataWatcherSerializer<?> paramDataWatcherSerializer)");
    }

    public static class Item {
        public static final ClassTemplate<?> T = ClassTemplate.createNMS("DataWatcher.Item");
        public static final TranslatorFieldAccessor<DataWatcher.Key<?>> key = T.nextField("private final DataWatcherObject<T> a").translate(ConversionPairs.dataWatcherKey);
        public static final FieldAccessor<Object> value = T.nextFieldSignature("private T b");
        public static final FieldAccessor<Boolean> changed = T.nextFieldSignature("private boolean c");
    }

}
