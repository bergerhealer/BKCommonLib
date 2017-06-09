package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.SoundEffectType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class SoundEffectTypeHandle extends Template.Handle {
    /** @See {@link SoundEffectTypeClass} */
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

    /**
     * Stores class members for <b>net.minecraft.server.SoundEffectType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class SoundEffectTypeClass extends Template.Class<SoundEffectTypeHandle> {
        public final Template.Method.Converted<SoundEffectHandle> getDefault = new Template.Method.Converted<SoundEffectHandle>();

    }

}

