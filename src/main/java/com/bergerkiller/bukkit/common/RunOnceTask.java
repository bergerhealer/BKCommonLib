package com.bergerkiller.bukkit.common;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Task that can be asked to run repeatedly, but once scheduled, will
 * not schedule a second time. This allows a task to run one tick delayed,
 * no matter how many times it is asked to run during a single tick.
 * Once executed, it can be started again for another execution in the future.<br>
 * <br>
 * This class is thread-safe.
 */
public abstract class RunOnceTask implements Runnable {
    private final Runnable _logicProxy;
    private final Plugin _plugin;
    private final AtomicInteger _scheduledId;

    /**
     * Constructs a new RunOnceTask, calling {@link #run()} when scheduled.
     * 
     * @param plugin
     */
    public RunOnceTask(Plugin plugin) {
        this._plugin = plugin;
        this._scheduledId = new AtomicInteger(-1);
        this._logicProxy = () -> {
            // First, reset the scheduled task id to -1
            // If the task was still scheduled (not cancelled), run the task.
            int oldTaskId;
            do {
                oldTaskId = this._scheduledId.get();
            } while (!this._scheduledId.compareAndSet(oldTaskId, -1));
            if (oldTaskId != -1) {
                RunOnceTask.this.run();
            }
        };
    }

    /**
     * Creates a new RunOnceTask that calls a runnable a tick delayed when started.
     * 
     * @param plugin
     * @param runnable
     * @return new RunOnceTask that executes the runnable
     */
    public static RunOnceTask create(Plugin plugin, Runnable runnable) {
        return new RunOnceTask(plugin) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Gets the plugin owner of this task
     * 
     * @return plugin
     */
    public Plugin getPlugin() {
        return this._plugin;
    }

    /**
     * Gets whether this task is currently scheduled, and the {@link #run()}
     * method will soon be executed.
     * 
     * @return True if scheduled
     */
    public boolean isScheduled() {
        return this._scheduledId.get() != -1;
    }

    /**
     * Schedules a runnable to run one tick delayed from now.
     * If already scheduled, nothing happens.
     */
    public void start() {
        if (this._scheduledId.compareAndSet(-1, 1)) {
            this._scheduledId.set(Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy));
        }
    }

    /**
     * Runs the task right now, if it was scheduled before. The original
     * scheduled task is cancelled. If no task was scheduled to run, this
     * method does nothing.
     */
    public void runNowIfScheduled() {
        int oldTaskId;
        do {
            oldTaskId = this._scheduledId.get();
        } while (!this._scheduledId.compareAndSet(oldTaskId, -1));
        if (oldTaskId != -1) {
            Bukkit.getScheduler().cancelTask(oldTaskId);
            this.run();
        }
    }

    /**
     * If asked to start prior, cancels that operation
     */
    public void cancel() {
        int oldTaskId;
        do {
            oldTaskId = this._scheduledId.get();
        } while (!this._scheduledId.compareAndSet(oldTaskId, -1));
        if (oldTaskId != -1) {
            Bukkit.getScheduler().cancelTask(oldTaskId);
        }
    }
}
