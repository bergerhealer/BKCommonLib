package com.bergerkiller.bukkit.common;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import co.aikar.timings.lib.MCTiming;
import co.aikar.timings.lib.TimingManager;

/**
 * Thin wrapper around MC Timings by aikar for easily storing and performing timings measurements.
 * https://github.com/aikar/minecraft-timings<br>
 * <br>
 * Implements AutoCloseable so it can be used with Java7's try with resources statement.
 */
public class Timings implements AutoCloseable {
    private final Plugin plugin;
    private final MCTiming timing;

    public Timings(Plugin plugin) {
        this.plugin = plugin;
        this.timing = null;
    }

    public Timings(Plugin plugin, String name) {
        this.plugin = plugin;
        this.timing = TimingManager.of(plugin).of(getTimingName(plugin, name));
    }

    /**
     * Creates a new timing object for the same plugin
     * 
     * @param name of the timing
     * @return timing
     */
    public final Timings create(String name) {
        return new Timings(this.plugin, name);
    }

    /**
     * Starts the timings measurement
     * 
     * @return this timing
     */
    public final Timings start() {
        this.timing.startTiming();
        return this;
    }

    /**
     * Stops the timings measurement
     */
    public final void stop() {
        this.timing.stopTiming();
    }

    /**
     * Executes {@link Runnable#run()} on a runnable whilst performing timings measurements
     * 
     * @param runnable to run
     * @return input runnable
     */
    public <T extends Runnable> T run(T runnable) {
        try {
            start();
            runnable.run();
        } finally {
            stop();
        }
        return runnable;
    }

    /**
     * Equivalent to calling {@link #stop()}, is used to satisfy the {@link AutoCloseable} interface
     * which allows this class to be used using java7's try-with-resources feature.
     */
    @Override
    public void close() {
        stop();
    }

    private static String getTimingName(Plugin plugin, String name) {
        return String.format("Plugin: %s v%s Event: %s", 
                plugin.getName(),
                plugin.getDescription().getVersion(),
                name);
    }

    /**
     * Helper function for debugging that detects the plugin to use from a profiled class,
     * then creates a timings object if one does not exist in cache. The profiled class name
     * excluding path is prepended to the name. This function automatically
     * starts the timings measurement. Best used inside a try-with-resources block.<br>
     * <br>
     * Not recommended for use in final releases, because the HashMap lookup is slow for high number of calls.
     * Hence this method is <b>deprecated</b>.
     * 
     * @param profiledClass class where the timings are done
     * @param name for the timings
     * @return timings object
     */
    @Deprecated
    public static Timings start(Class<?> profiledClass, String name) {
        final TimingsKey key = new TimingsKey(profiledClass, name);
        Timings t = cachedTimings.get(key);
        if (t == null) {
            Plugin plugin = CommonUtil.getPluginByClass(profiledClass);
            if (plugin == null) {
                plugin = CommonPlugin.getInstance();
            }
            t = new Timings(plugin, profiledClass.getSimpleName() + "::" + name);
            cachedTimings.put(key, t);
        }
        return t.start();
    }

    private static final Map<TimingsKey, Timings> cachedTimings = new HashMap<TimingsKey, Timings>();

    private static final class TimingsKey {
        public final Class<?> profiledClass;
        public final String name;

        public TimingsKey(Class<?> profiledClass, String name) {
            this.profiledClass = profiledClass;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return 203 + 29 * this.name.hashCode() + this.profiledClass.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            TimingsKey k = (TimingsKey) o;
            return k.name.equals(this.name) && k.profiledClass.equals(this.profiledClass);
        }
    }
}
