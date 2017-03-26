package com.bergerkiller.bukkit.common._unused;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common._unused.reflection.ClassBuilder_unused;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import java.util.Arrays;
import java.util.logging.Level;

public class SpigotDummyFlatMap_unused {

    private static final Object INSTANCE;

    static {
        Class<?> flatMapClass = CommonUtil.getClass("org.spigotmc.FlatMap");
        Object flatInstance = null;
        if (flatMapClass == null) {
        	Logging.LOGGER.log(Level.SEVERE, "The Spigot FlatMap class could not be located!");
        } else {
            try {
                // Initialize a new dummy flatmap (that does nothing)
                ClassBuilder_unused builder = new ClassBuilder_unused(flatMapClass, FlatMapImpl.class);
                flatInstance = builder.create(new Class<?>[0], new Object[0], Arrays.asList((Object) new FlatMapImpl()));
            } catch (Throwable t) {
            	Logging.LOGGER.log(Level.SEVERE, "Failed to initialize the Spigot Dummy FlatMap:", t);
            }
        }
        INSTANCE = flatInstance;
    }

    public static Object getInstance() {
        return INSTANCE;
    }

    public static class FlatMapImpl implements FlatMapMethods {

        public Object get(long msw, long lsw) {
            return null;
        }

        public Object get(long key) {
            return null;
        }

        public void put(long msw, long lsw, Object value) {
        }

        public void put(long key, Object value) {
        }
    }

    public static interface FlatMapMethods {

        public Object get(long msw, long lsw);

        public Object get(long key);

        public void put(long msw, long lsw, Object value);

        public void put(long key, Object value);
    }
}
