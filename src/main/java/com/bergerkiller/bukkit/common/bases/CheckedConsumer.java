package com.bergerkiller.bukkit.common.bases;

/**
 * Consumer that can throw a checked exception
 * 
 * @param <T>
 */
@FunctionalInterface
public interface CheckedConsumer<T> {

    /**
     * Consumes a value
     *
     * @param value The value to consume
     * @throws Throwable Can throw an exception if the value could not be consumed
     */
    void accept(T value) throws Throwable;
}
