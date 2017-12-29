package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Tracks the current server tick to check for when the current tick has changed.
 * Can be used to execute certain logic every tick, when no tick-task is used.
 */
public class TickTracker {
    private int _tick = Integer.MIN_VALUE;
    private Runnable _runnable = null;

    /**
     * Sets a runnable to execute every tick in {@link #update()}.
     * 
     * @param runnable to execute, null to disable
     */
    public void setRunnable(Runnable runnable) {
        this._runnable = runnable;
    }

    /**
     * Executes the runnable set using {@link #setRunnable(Runnable)} when the current
     * tick has changed. When no runnable is set, it functions similar to {@link #check()}.
     * 
     * @return True if the tick was changed
     */
    public boolean update() {
        if (!this.check()) return false;
        if (this._runnable != null) this._runnable.run();
        return true;
    }

    /**
     * Checks whether the current tick has changed. This method only
     * returns true once per tick.
     * 
     * @return True if the tick has changed.
     */
    public boolean check() {
        int current_tick = CommonUtil.getServerTicks();
        if (current_tick != this._tick) {
            this._tick = current_tick;
            return true;
        } else {
            return false;
        }
    }
}
