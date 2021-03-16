package com.bergerkiller.bukkit.common.internal;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Executor responsible for handling {@link com.bergerkiller.bukkit.common.utils.CommonUtil#nextTick(Runnable) nextTick(Runnable)}
 */
public final class CommonNextTickExecutor implements Executor {
    private volatile ExecutorTask executorTask = null;
    public static final CommonNextTickExecutor INSTANCE = new CommonNextTickExecutor();
    public static final Executor MAIN_THREAD = (command) -> {
        if (CommonUtil.isMainThread()) {
            try {
                command.run();
            } catch (Throwable t) {
                handleTaskError(command, t);
            }
        } else {
            INSTANCE.execute(command);
        }
    };

    /**
     * Creates new next-tick executor handler. Call
     * {@link #setExecutorTask(ExecutorTask)} to make it functional.
     *
     * @return next-tick executor
     */
    protected CommonNextTickExecutor() {
    }

    /**
     * Sets the executor task used by this next-tick executor
     *
     * @param executorTask Executor task to use. Specify <i>null</i>
     *        to disable the executor and use a fallback instead.
     */
    protected synchronized void setExecutorTask(ExecutorTask executorTask) {
        ExecutorTask previousExecutorTask = this.executorTask;
        this.executorTask = executorTask;
        if (executorTask != null) {
            executorTask.start(1, 1);
        }

        if (previousExecutorTask != null) {
            previousExecutorTask.stop();
            previousExecutorTask.run();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            return;
        }
        ExecutorTask task = this.executorTask;
        if (task != null) {
            task.offer(command);
            if (task != this.executorTask) {
                // We are in a state of confusion, because we are partway or fully disabled
                // Somehow we were offered a new task while disable() was running
                // At this point, all tasks inside the queue need to be sent to the fallback executor instead
                // We do this while synchronized(), making sure disable() completes before entering this block
                while (true) {
                    Runnable danglingCommand;
                    synchronized (this) {
                        danglingCommand = task.poll();
                    }
                    if (danglingCommand != null) {
                        executeFallback(danglingCommand);
                    } else {
                        break;
                    }
                }
            }
        } else {
            executeFallback(command);
        }
    }

    private void executeFallback(Runnable command) {
        // Try to find out what plugin this Runnable belongs to
        Plugin plugin = CommonUtil.getPluginByClass(command.getClass());
        if (plugin == null) {
            // Well...ain't that a pickle.
            // Maybe there is some other plugin we can dump this to?
            // It's a fallback...it does not have to be fair or perfect!
            synchronized (Bukkit.getPluginManager()) {
                Iterator<Plugin> iter = CommonUtil.getPluginsUnsafe().iterator();
                while (iter.hasNext()) {
                    plugin = iter.next();
                    if (plugin.isEnabled()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, command);
                        return;
                    }
                }
            }
            // Well now we just don't know...
            Logging.LOGGER.log(Level.SEVERE, "Unable to properly schedule next-tick task: " + command.getClass().getName());
            Logging.LOGGER.log(Level.SEVERE, "The task is executed right away instead...we might recover!");
            try {
                command.run();
            } catch (Throwable t) {
                handleTaskError(command, t);
            }
        } else {
            // Use the supposed plugin this Class belongs to
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, command);
        }
    }

    private static void handleTaskError(Runnable command, Throwable error) {
        Logging.LOGGER.log(Level.SEVERE, "An error occurred in next-tick task '" + command.getClass().getName() + "':");
        CommonUtil.filterStackTrace(error).printStackTrace();
    }

    /**
     * Bukkit task that actually executes scheduled runnables
     * on the main thread. Can be extended to provide at-runtime
     * context what executor is used.
     */
    protected static class ExecutorTask extends Task {
        private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
        private final AtomicInteger taskCount = new AtomicInteger(0);

        public void offer(Runnable task) {
            this.tasks.offer(task);
            this.taskCount.incrementAndGet();
        }

        public Runnable poll() {
            Runnable task = this.tasks.poll();
            if (task != null) {
                this.taskCount.decrementAndGet();
            }
            return task;
        }

        public ExecutorTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            int numTasks = this.taskCount.get();
 
            Runnable command;
            while (numTasks-- > 0 && (command = this.tasks.poll()) != null) {
                this.taskCount.decrementAndGet();
                try {
                    command.run();
                } catch (Throwable t) {
                    handleTaskError(command, t);
                }
            }
        }
    }
}
