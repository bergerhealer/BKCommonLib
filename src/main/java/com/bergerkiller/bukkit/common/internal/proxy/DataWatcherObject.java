package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Proxy class for MC 1.8.8 translating the old int Id to a DataWatcherObject
 */
public class DataWatcherObject<T> {
    private final int id;

    public DataWatcherObject(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
