package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;

import net.minecraft.server.v1_9_R1.DataWatcherObject;

import java.util.List;

public class DataWatcherRef {

    public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("DataWatcher");
    public static final MethodAccessor<Void> write = TEMPLATE.getMethod("set", DataWatcherObject.class, Object.class);
    public static final MethodAccessor<Void> watch = TEMPLATE.getMethod("registerObject", DataWatcherObject.class, Object.class);
    public static final MethodAccessor<List<Object>> returnAllWatched = TEMPLATE.getMethod("c");
    public static final MethodAccessor<List<Object>> unwatchAndReturnAllWatched = TEMPLATE.getMethod("b");
    public static final MethodAccessor<Object> read = TEMPLATE.getMethod("c", DataWatcherObject.class);
    public static final MethodAccessor<Boolean> isChanged = TEMPLATE.getMethod("a");
    public static final MethodAccessor<Boolean> isEmpty = TEMPLATE.getMethod("d");
    public static final SafeConstructor<Object> constructor1 = TEMPLATE.getConstructor(EntityRef.TEMPLATE.getType());

    /**
     * @deprecated Use com.bergerkiller.bukkit.common.wrappers.DataWatcher
     * instead
     */
    @Deprecated
    public static void write(Object datawatcher, int index, Object value) {
        write.invoke(datawatcher, index, value);
    }

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
        return TEMPLATE.newInstance();
    }
}
