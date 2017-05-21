package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class IWorldAccessHandle extends Template.Handle {
    public static final IWorldAccessClass T = new IWorldAccessClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IWorldAccessHandle.class, "net.minecraft.server.IWorldAccess");


    /* ============================================================================== */

    public static IWorldAccessHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        IWorldAccessHandle handle = new IWorldAccessHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void onEntityAdded(EntityHandle entity) {
        T.onEntityAdded.invoke(instance, entity);
    }

    public void onEntityRemoved(EntityHandle entity) {
        T.onEntityRemoved.invoke(instance, entity);
    }

    public static final class IWorldAccessClass extends Template.Class<IWorldAccessHandle> {
        public final Template.Method.Converted<Void> onEntityAdded = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> onEntityRemoved = new Template.Method.Converted<Void>();

    }
}
