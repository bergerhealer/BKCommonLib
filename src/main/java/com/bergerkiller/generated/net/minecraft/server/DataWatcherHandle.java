package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class DataWatcherHandle extends Template.Handle {
    public static final DataWatcherClass T = new DataWatcherClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DataWatcherHandle.class, "net.minecraft.server.DataWatcher");


    /* ============================================================================== */

    public static DataWatcherHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DataWatcherHandle handle = new DataWatcherHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public EntityHandle getOwner() {
        return T.owner.get(instance);
    }

    public void setOwner(EntityHandle value) {
        T.owner.set(instance, value);
    }

    public static final class DataWatcherClass extends Template.Class<DataWatcherHandle> {
        public final Template.Field.Converted<EntityHandle> owner = new Template.Field.Converted<EntityHandle>();

    }
}
