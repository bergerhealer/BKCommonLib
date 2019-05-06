package com.bergerkiller.bukkit.common.collections;

import java.util.Optional;
import java.util.function.Consumer;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Calls {@link Runnable#run()} when consuming objects.
 * 
 * @param <T> type of Object
 */
public final class RunnableConsumer<T> implements Consumer<T> {
    private static final Class<?> DATAFIXER_EITHER = CommonUtil.getClass("com.mojang.datafixers.util.Either");
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
    @SuppressWarnings("unchecked")
    public void accept(T value) {
        this.value = (T) unpack(value);
        this.runnable.run();
    }

    @SuppressWarnings("unchecked")
    public static Object unpack(Object value) {
        if (DATAFIXER_EITHER != null && DATAFIXER_EITHER.isAssignableFrom(value.getClass())) {
            try {
                Optional<Object> opt_left = (Optional<Object>) DATAFIXER_EITHER.getMethod("left").invoke(value);
                if (opt_left.isPresent()) {
                    return opt_left.get();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        } else {
            return value;
        }
    }
    
    public static <T> RunnableConsumer<T> create(Runnable runnable) {
        return new RunnableConsumer<T>(runnable);
    }
}
