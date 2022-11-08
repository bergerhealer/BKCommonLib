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
     * @return value The value to comsume
     * @throws Can throw an exception if the value could not be consumed
     */
    void accept(T value) throws Throwable;
}
