package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.generated.net.minecraft.server.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import java.util.List;

import org.bukkit.entity.Entity;

/**
 * <b>Deprecated: </b>Use the appropriate {@link DataWatcherHandle} and related classes instead
 */
@Deprecated
public class NMSDataWatcher {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("DataWatcher");

    public static final TranslatorFieldAccessor<Entity> owner = DataWatcherHandle.T.owner.raw.toFieldAccessor().translate(DuplexConversion.entity);

    public static final MethodAccessor<Void> watch = DataWatcherHandle.T.register.raw.toMethodAccessor();

    /*
     *  } else if (this.d.containsKey(Integer.valueOf(i))) {
     *      throw new IllegalArgumentException("Duplicate id value for " + i + "!");
     #  } else if (DataWatcherRegistry.b(datawatcherobject.##METHODNAME##()) < 0) {
     *      throw new IllegalArgumentException("Unregistered serializer " + datawatcherobject.b() + " for " + i + "!");
     *  } else {
     */
    public static final MethodAccessor<List<Object>> unwatchAndReturnAllWatched = DataWatcherHandle.T.unwatchAndReturnAllWatched.raw.toMethodAccessor();
    
    /*
     * Same signature as unwatchAndReturnAllWatched, find other function that matches
     */
    public static final MethodAccessor<List<Object>> returnAllWatched = DataWatcherHandle.T.returnAllWatched.raw.toMethodAccessor();

    public static final MethodAccessor<Object> read = DataWatcherHandle.T.read.raw.toMethodAccessor();

    public static final MethodAccessor<Object> get = DataWatcherHandle.T.get.raw.toMethodAccessor();
    public static final MethodAccessor<Void> set = DataWatcherHandle.T.set.raw.toMethodAccessor();

    public static final MethodAccessor<Boolean> isChanged = DataWatcherHandle.T.isChanged.toMethodAccessor();
    public static final MethodAccessor<Boolean> isEmpty = DataWatcherHandle.T.isEmpty.toMethodAccessor();
    public static final SafeConstructor<?> constructor1 = T.getConstructor(EntityHandle.T.getType());

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

}
