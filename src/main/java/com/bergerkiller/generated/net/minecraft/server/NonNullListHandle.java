package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import java.util.List;

@Template.Optional
public class NonNullListHandle extends Template.Handle {
    public static final NonNullListClass T = new NonNullListClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(NonNullListHandle.class, "net.minecraft.server.NonNullList");

    /* ============================================================================== */

    public static NonNullListHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        NonNullListHandle handle = new NonNullListHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static List<?> create() {
        return T.create.invokeVA();
    }

    public static final class NonNullListClass extends Template.Class<NonNullListHandle> {
        public final Template.StaticMethod.Converted<List<?>> create = new Template.StaticMethod.Converted<List<?>>();

    }

}

