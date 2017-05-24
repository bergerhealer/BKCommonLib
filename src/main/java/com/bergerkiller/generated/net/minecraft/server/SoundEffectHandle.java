package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class SoundEffectHandle extends Template.Handle {
    public static final SoundEffectClass T = new SoundEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(SoundEffectHandle.class, "net.minecraft.server.SoundEffect");


    /* ============================================================================== */

    public static SoundEffectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        SoundEffectHandle handle = new SoundEffectHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public MinecraftKeyHandle getName() {
        return T.name.get(instance);
    }

    public void setName(MinecraftKeyHandle value) {
        T.name.set(instance, value);
    }

    public static final class SoundEffectClass extends Template.Class<SoundEffectHandle> {
        public final Template.Field.Converted<MinecraftKeyHandle> name = new Template.Field.Converted<MinecraftKeyHandle>();

    }
}
