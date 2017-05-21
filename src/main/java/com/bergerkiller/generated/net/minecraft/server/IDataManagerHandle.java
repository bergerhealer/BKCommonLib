package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IDataManagerHandle extends Template.Handle {
    public static final IDataManagerClass T = new IDataManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IDataManagerHandle.class, "net.minecraft.server.IDataManager");


    /* ============================================================================== */

    public static IDataManagerHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IDataManagerHandle handle = new IDataManagerHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class IDataManagerClass extends Template.Class<IDataManagerHandle> {
    }
}
