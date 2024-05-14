package com.bergerkiller.bukkit.common.internal.logic;

/**
 * Exception thrown when attempting to call setValue() on the immutable
 * UnsetDataWatcherItem instance.
 */
public final class UnsetDataWatcherItemException extends RuntimeException {
    /**
     * Static singleton instance of this exception, to avoid overhead of construction
     */
    public static final UnsetDataWatcherItemException INSTANCE = new UnsetDataWatcherItemException();

    private UnsetDataWatcherItemException() {
    }

    @Override
    public Throwable fillInStackTrace() {
        return this; // Slight performance perk
    }
}
