package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class SoundEffectsHandle extends Template.Handle {
    public static final SoundEffectsClass T = new SoundEffectsClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundEffectsHandle.class, "net.minecraft.server.SoundEffects");

    public static final SoundEffectHandle EXTINGUISH_FIRE = T.EXTINGUISH_FIRE.getSafe();
    /* ============================================================================== */

    public static SoundEffectsHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SoundEffectsHandle handle = new SoundEffectsHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class SoundEffectsClass extends Template.Class<SoundEffectsHandle> {
        public final Template.StaticField.Converted<SoundEffectHandle> EXTINGUISH_FIRE = new Template.StaticField.Converted<SoundEffectHandle>();

    }

}

