package com.bergerkiller.bukkit.common.bases;

/**
 * Function that can throw a checked exception
 * 
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Can throw an exception if the result could not be gotten
     */
    R apply(T t) throws Throwable;

    /**
     * Turns this CheckedFunction into a CheckedConsumer by discarding
     * the return value
     *
     * @return consumer which calls {@link #apply(Object)} with the consumed value
     */
    default CheckedConsumer<T> asConsumer() {
        return value -> apply(value);
    }
}
