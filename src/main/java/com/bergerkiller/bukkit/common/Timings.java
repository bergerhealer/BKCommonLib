package com.bergerkiller.bukkit.common;

import org.bukkit.plugin.Plugin;

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
        return String.format("Task: %s v%s Runnable: %s", 
                plugin.getName(),
                plugin.getDescription().getVersion(),
                name);
    }
}
