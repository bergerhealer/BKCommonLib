package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EnumParticleHandle extends Template.Handle {
    public static final EnumParticleClass T = new EnumParticleClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EnumParticleHandle.class, "net.minecraft.server.EnumParticle");

    /* ============================================================================== */

    public static EnumParticleHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EnumParticleHandle handle = new EnumParticleHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static EnumParticleHandle byName(String name) {
        return T.byName.invokeVA(name);
    }

    public static final class EnumParticleClass extends Template.Class<EnumParticleHandle> {
        public final Template.StaticMethod.Converted<EnumParticleHandle> byName = new Template.StaticMethod.Converted<EnumParticleHandle>();

    }

}

