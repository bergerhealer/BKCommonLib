package com.bergerkiller.bukkit.common.bases;

/**
 * Runnable that can throw a checked exception
 */
@FunctionalInterface
public interface CheckedRunnable {

    /**
     * Executes this runnable
     *
     * @throws Throwable if something goes wrong in a checked way
     */
    void run() throws Throwable;
}
