package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class NibbleArrayHandle extends Template.Handle {
    public static final NibbleArrayClass T = new NibbleArrayClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NibbleArrayHandle.class, "net.minecraft.server.NibbleArray");


    /* ============================================================================== */

    public static NibbleArrayHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NibbleArrayHandle handle = new NibbleArrayHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final NibbleArrayHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */

    public static final class NibbleArrayClass extends Template.Class<NibbleArrayHandle> {
        public final Template.Constructor.Converted<NibbleArrayHandle> constr = new Template.Constructor.Converted<NibbleArrayHandle>();

    }
}
