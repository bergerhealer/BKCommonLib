package com.bergerkiller.bukkit.common.bases;

/**
 * Supplier that can throw a checked exception
 * 
 * @param <T>
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws Can throw an exception if the result could not be gotten
     */
    T get() throws Throwable;

    /**
     * Turns this CheckedSupplier into a CheckedRunnable by discarding
     * the return value
     *
     * @return runnable which calls {@link #get()}
     */
    default CheckedRunnable asRunnable() {
        return () -> get();
    }
}
