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
    private static final int TASK_NOT_SCHEDULED = -1;
    private static final int TASK_SCHEDULED_SOON = -2;

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
        this._scheduledId = new AtomicInteger(TASK_NOT_SCHEDULED);
        this._logicProxy = () -> {
            // First, reset the scheduled task id to TASK_NOT_SCHEDULED
            // If the task was still scheduled (not cancelled), run the task.
            int oldTaskId;
            do {
                oldTaskId = this._scheduledId.get();
            } while (!this._scheduledId.compareAndSet(oldTaskId, TASK_NOT_SCHEDULED));

            if (oldTaskId >= 0) {
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
        return this._scheduledId.get() != TASK_NOT_SCHEDULED;
    }

    /**
     * Schedules a runnable to run some ticks delayed from now.
     * If already scheduled, cancels that scheduled run. This keeps
     * delaying execution by the delay every time this method is called.
     * 
     * @param delay Tick delay until the runnable is run
     */
    public void restart(long delay) {
        int newTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy, delay);
        int previousTaskId = this._scheduledId.getAndSet(newTaskId);
        if (previousTaskId >= 0) {
            Bukkit.getScheduler().cancelTask(previousTaskId);
        }
    }

    /**
     * Schedules a runnable to run some ticks delayed from now.
     * If already scheduled, will not schedule again.
     * 
     * @param delay Tick delay until the runnable is run
     */
    public void start(long delay) {
        if (this._scheduledId.compareAndSet(TASK_NOT_SCHEDULED, TASK_SCHEDULED_SOON)) {
            int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy, delay);
            if (!this._scheduledId.compareAndSet(TASK_SCHEDULED_SOON, taskId)) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
    }

    /**
     * Schedules a runnable to run one tick delayed from now.
     * If already scheduled, nothing happens.
     */
    public void start() {
        if (this._scheduledId.compareAndSet(TASK_NOT_SCHEDULED, TASK_SCHEDULED_SOON)) {
            int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, this._logicProxy);
            if (!this._scheduledId.compareAndSet(TASK_SCHEDULED_SOON, taskId)) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
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
        } while (!this._scheduledId.compareAndSet(oldTaskId, TASK_NOT_SCHEDULED));

        if (oldTaskId >= 0) {
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
        } while (!this._scheduledId.compareAndSet(oldTaskId, TASK_NOT_SCHEDULED));

        if (oldTaskId >= 0) {
            Bukkit.getScheduler().cancelTask(oldTaskId);
        }
    }
}
