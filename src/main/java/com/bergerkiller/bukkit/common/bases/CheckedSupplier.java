package com.bergerkiller.bukkit.common.bases;

/**
 * Supplier that can throw an exception
 * 
 * @param <T>
 */
public interface CheckedSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws Can throw an exception if the result could not be gotten
     */
    T get() throws Throwable;
}
