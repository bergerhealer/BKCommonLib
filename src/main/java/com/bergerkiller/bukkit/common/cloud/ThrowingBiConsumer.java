package com.bergerkiller.bukkit.common.cloud;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U> {
    void accept(T t, U u) throws Throwable;
}
