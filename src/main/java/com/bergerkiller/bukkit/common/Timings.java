package com.bergerkiller.bukkit.common;

import org.bukkit.plugin.Plugin;

/**
 * Thin wrapper around MC Timings by aikar for easily storing and performing timings measurements.
 * https://github.com/aikar/minecraft-timings<br>
 * <br>
 * Implements AutoCloseable so it can be used with Java7's try with resources statement.
 *
 * @deprecated Timings has beeng phased out of paper/spigot. This class doesn't do anything anymore.
 */
@Deprecated
public class Timings implements AutoCloseable {
    private static final Timings NOOP = new Timings(null);

    public Timings(Plugin plugin) {
    }

    public Timings(Plugin plugin, String name) {
    }

    /**
     * Creates a new timing object for the same plugin
     * 
     * @param name of the timing
     * @return timing
     */
    public final Timings create(String name) {
        return new Timings(null, name);
    }

    /**
     * Starts the timings measurement
     * 
     * @return this timing
     */
    public final Timings start() {
        return this;
    }

    /**
     * Stops the timings measurement
     */
    public final void stop() {
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
        return NOOP;
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
}
