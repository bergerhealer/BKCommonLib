package com.bergerkiller.bukkit.common;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

import co.aikar.timings.lib.MCTiming;

/**
 * Thin wrapper around MC Timings by aikar for easily storing and performing timings measurements.
 * https://github.com/aikar/minecraft-timings<br>
 * <br>
 * Implements AutoCloseable so it can be used with Java7's try with resources statement.
 *
 * @deprecated Timings is being phased out of paper/spigot. This class doesn't do anything anymore.
 */
@Deprecated
public class Timings implements AutoCloseable {
    private static final Map<TimingsKey, Timings> cachedTimings = new HashMap<TimingsKey, Timings>();
    private static final boolean hasTimingsV2;
    private static final MCTiming NOOP_MC_TIMINGS;
    private static final Timings NOOP;

    private final Plugin plugin;
    private final MCTiming timing;

    public Timings(Plugin plugin) {
        this.plugin = plugin;
        this.timing = NOOP_MC_TIMINGS;
    }

    public Timings(Plugin plugin, String name) {
        this.plugin = plugin;
        this.timing = NOOP_MC_TIMINGS;
        //this.timing = TimingManager.of(plugin).of(getTimingName(plugin, name));
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
        return cachedTimings.computeIfAbsent(new TimingsKey(profiledClass, name), key -> {
            Plugin plugin = CommonUtil.getPluginByClass(profiledClass);
            if (plugin == null) {
                plugin = CommonPlugin.getInstance();
            }
            return new Timings(plugin, profiledClass.getSimpleName() + "::" + name);
        }).start();
    }

    /**
     * Creates a new Timings instance for a plugin with the given name.
     * These timings are not cached, so it is up to the caller to store the
     * result in a field for better performance.
     *
     * @param plugin Plugin to file the timings under
     * @param name Name to give to the timings
     * @return timings object
     */
    public static Timings create(Plugin plugin, String name) {
        try (Timings tmp = new Timings(plugin)) {
            return tmp.create(name);
        }
    }

    /**
     * Obtains the no-operation timings instance. These timings do nothing when started and
     * stopped, and as a result impose no performance penalty when used.
     *
     * @return No-Op timings instance
     */
    public static Timings noop() {
        return NOOP;
    }

    private static String getTimingName(Plugin plugin, String name) {
        if (hasTimingsV2) {
            return name;
        } else {
            // Timings v1 requires this format or it won't work
            return String.format("Plugin: %s v%s Event: %s", 
                    plugin.getName(),
                    plugin.getDescription().getVersion(),
                    name);
        }
    }

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

    static {
        boolean tv2 = false;
        try {
            Class.forName("co.aikar.timings.Timing").getMethod("startTiming");
            tv2 = true;
        } catch (Throwable t) {}
        hasTimingsV2 = tv2;

        NOOP_MC_TIMINGS = new MCTiming() {
            @Override
            public MCTiming startTiming() {
                return this;
            }

            @Override
            public void stopTiming() {
            }
        };

        NOOP = new Timings(CommonPlugin.hasInstance() ? CommonPlugin.getInstance() : null);
    }
}
