package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Proxy class for MC 1.8.8 translating the old int Id to a DataWatcherObject
 */
public class DataWatcherObject<T> {
    private final int id;
    private final Object serializer;

    public DataWatcherObject(int id, Object serializer) {
        this.id = id;
        this.serializer = serializer;
    }

    public int getId() {
        return this.id;
    }

    public Object getSerializer() {
        return this.serializer;
    }
}
