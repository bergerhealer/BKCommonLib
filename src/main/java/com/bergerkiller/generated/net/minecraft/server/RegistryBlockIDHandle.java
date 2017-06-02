package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class RegistryBlockIDHandle extends Template.Handle {
    public static final RegistryBlockIDClass T = new RegistryBlockIDClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegistryBlockIDHandle.class, "net.minecraft.server.RegistryBlockID");

    /* ============================================================================== */

    public static RegistryBlockIDHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RegistryBlockIDHandle handle = new RegistryBlockIDHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getId(Object value) {
        return T.getId.invoke(instance, value);
    }

    public static final class RegistryBlockIDClass extends Template.Class<RegistryBlockIDHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

