package com.bergerkiller.bukkit.common.collections;

import java.util.function.Consumer;

/**
 * Calls {@link Runnable#run()} when consuming objects.
 * 
 * @param <T> type of Object
 */
public final class RunnableConsumer<T> implements Consumer<T> {
    private final Runnable runnable;
    private T value = null;

    private RunnableConsumer(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * Gets the last consumed value
     * 
     * @return value
     */
    public T getValue() {
        return this.value;
    }

    @Override
    public void accept(T value) {
        this.value = value;
        this.runnable.run();
    }

    public static <T> RunnableConsumer<T> create(Runnable runnable) {
        return new RunnableConsumer<T>(runnable);
    }
}
