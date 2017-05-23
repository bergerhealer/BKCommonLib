package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class DataWatcherObjectHandle extends Template.Handle {
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

    public static final class DataWatcherObjectClass extends Template.Class<DataWatcherObjectHandle> {
    }
}
