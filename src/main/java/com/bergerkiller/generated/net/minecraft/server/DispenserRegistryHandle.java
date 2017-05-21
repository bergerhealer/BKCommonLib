package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class DispenserRegistryHandle extends Template.Handle {
    public static final DispenserRegistryClass T = new DispenserRegistryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DispenserRegistryHandle.class, "net.minecraft.server.DispenserRegistry");


    /* ============================================================================== */

    public static DispenserRegistryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DispenserRegistryHandle handle = new DispenserRegistryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static void bootstrap() {
        T.bootstrap.invokeVA();
    }

    public static final class DispenserRegistryClass extends Template.Class<DispenserRegistryHandle> {
        public final Template.StaticMethod<Void> bootstrap = new Template.StaticMethod<Void>();

    }
}
