package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DataWatcherObject</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class DataWatcherObjectHandle extends Template.Handle {
    /** @See {@link DataWatcherObjectClass} */
    public static final DataWatcherObjectClass T = new DataWatcherObjectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DataWatcherObjectHandle.class, "net.minecraft.server.DataWatcherObject");

    /* ============================================================================== */

    public static DataWatcherObjectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DataWatcherObjectHandle handle = new DataWatcherObjectHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.DataWatcherObject</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherObjectClass extends Template.Class<DataWatcherObjectHandle> {
    }

}

