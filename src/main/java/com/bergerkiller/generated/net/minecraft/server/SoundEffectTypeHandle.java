package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class SoundEffectTypeHandle extends Template.Handle {
    public static final SoundEffectTypeClass T = new SoundEffectTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundEffectTypeHandle.class, "net.minecraft.server.SoundEffectType");

    /* ============================================================================== */

    public static SoundEffectTypeHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SoundEffectTypeHandle handle = new SoundEffectTypeHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public SoundEffectHandle getDefault() {
        return T.getDefault.invoke(instance);
    }

    public static final class SoundEffectTypeClass extends Template.Class<SoundEffectTypeHandle> {
        public final Template.Method.Converted<SoundEffectHandle> getDefault = new Template.Method.Converted<SoundEffectHandle>();

    }

}

