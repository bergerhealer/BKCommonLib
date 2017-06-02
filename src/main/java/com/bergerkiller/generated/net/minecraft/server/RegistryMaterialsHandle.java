package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class RegistryMaterialsHandle extends Template.Handle {
    public static final RegistryMaterialsClass T = new RegistryMaterialsClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegistryMaterialsHandle.class, "net.minecraft.server.RegistryMaterials");

    /* ============================================================================== */

    public static RegistryMaterialsHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        RegistryMaterialsHandle handle = new RegistryMaterialsHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object get(Object key) {
        return T.get.invoke(instance, key);
    }

    public static final class RegistryMaterialsClass extends Template.Class<RegistryMaterialsHandle> {
        public final Template.Method<Object> get = new Template.Method<Object>();

    }

}

