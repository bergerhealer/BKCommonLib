# Task

`com.bergerkiller.bukkit.common.Task` is a small wrapper around Bukkit's scheduler for running plugin work on the server thread. Construct it with the owning `JavaPlugin`, implement `run()`, then start it with a delay or repeating interval.

```java
import com.bergerkiller.bukkit.common.Task;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
    private Task updateTask;

    @Override
    public void onEnable() {
        updateTask = new Task(this) {
            @Override
            public void run() {
                getLogger().info("Updating");
            }
        };
        updateTask.start(20, 100);
    }

    @Override
    public void onDisable() {
        Task.stop(updateTask);
    }
}
```

The delay and interval are measured in server ticks. `start()` schedules a single execution for the next tick, `start(delay)` schedules one execution after a delay, and `start(delay, interval)` schedules repeated executions. These are synchronous Bukkit scheduler tasks, not background tasks. Keep the work short to avoid slowing the server tick.

Call `stop()` to cancel a scheduled task. The static `Task.stop(task)` also accepts `null`. `isQueued()` checks whether the scheduler has the task queued; `isRunning()` checks whether it is currently executing, not whether a repeating task is scheduled between executions. Calling `start(...)` again does not cancel the previous schedule, so stop the task first if restarting it; otherwise only the most recently scheduled task is tracked by that instance.

## Alternatives

- [`RunOnceTask`](common-logic-utilities.md#coalescing-scheduled-work-with-runoncetask) coalesces repeated requests into one pending execution.
- [`CommonUtil.nextTick()`](common-logic-utilities.md#deferring-work-to-the-next-tick) dispatches a runnable to the server thread on a later tick.
- Bukkit's own scheduler is also available for plugin-owned synchronous or asynchronous tasks.
